package br.com.cloudtask.service;

import br.com.cloudtask.domain.Role;
import br.com.cloudtask.domain.User;
import br.com.cloudtask.dto.AuthResponse;
import br.com.cloudtask.dto.LoginRequest;
import br.com.cloudtask.dto.RegisterRequest;
import br.com.cloudtask.exception.ConflictException;
import br.com.cloudtask.exception.UnauthorizedException;
import br.com.cloudtask.repository.UserRepository;
import br.com.cloudtask.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void shouldRegisterUserWithNormalizedEmailAndEncodedPassword() {
        RegisterRequest request = new RegisterRequest("  Jucelio  ", "JUCELIO@EXAMPLE.COM", "Senha@123");

        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken("jucelio@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getName()).isEqualTo("Jucelio");
        assertThat(savedUser.getEmail()).isEqualTo("jucelio@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.email()).isEqualTo("jucelio@example.com");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Jucelio", "jucelio@example.com", "Senha@123");
        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("E-mail já cadastrado.");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldLoginWithValidCredentials() {
        User user = User.builder()
                .id(1L)
                .name("Jucelio")
                .email("jucelio@example.com")
                .passwordHash("encoded-password")
                .build();

        LoginRequest request = new LoginRequest("jucelio@example.com", "Senha@123");
        when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail())).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.name()).isEqualTo("Jucelio");
        assertThat(response.email()).isEqualTo("jucelio@example.com");
    }

    @Test
    void shouldRejectInvalidPassword() {
        User user = User.builder()
                .id(1L)
                .name("Jucelio")
                .email("jucelio@example.com")
                .passwordHash("encoded-password")
                .build();

        LoginRequest request = new LoginRequest("jucelio@example.com", "senha-incorreta");
        when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Credenciais inválidas.");

        verify(jwtService, never()).generateToken(any());
    }
}
