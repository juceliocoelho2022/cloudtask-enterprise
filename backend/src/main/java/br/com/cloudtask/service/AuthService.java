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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("E-mail já cadastrado.");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(request.email().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return new AuthResponse(
                jwtService.generateToken(user.getEmail()),
                "Bearer",
                user.getName(),
                user.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais inválidas.");
        }

        return new AuthResponse(
                jwtService.generateToken(user.getEmail()),
                "Bearer",
                user.getName(),
                user.getEmail()
        );
    }
}
