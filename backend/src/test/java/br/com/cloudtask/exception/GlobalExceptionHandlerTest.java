package br.com.cloudtask.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/tasks/99");
    }

    @Test
    void shouldReturn404ForNotFoundException() {
        ResponseEntity<ApiError> response = handler.handleNotFound(
                new NotFoundException("Tarefa não encontrada."), request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Tarefa não encontrada.");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/tasks/99");
        assertThat(response.getBody().validationErrors()).isEmpty();
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldReturn409ForConflictException() {
        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");

        ResponseEntity<ApiError> response = handler.handleConflict(
                new ConflictException("E-mail já cadastrado."), request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("E-mail já cadastrado.");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/auth/register");
    }

    @Test
    void shouldReturn401ForUnauthorizedException() {
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");

        ResponseEntity<ApiError> response = handler.handleUnauthorized(
                new UnauthorizedException("Credenciais inválidas."), request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Credenciais inválidas.");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/auth/login");
    }

    @Test
    void shouldReturn400WithFieldErrorsForValidationException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("taskRequest", "title", "não deve estar em branco"),
                new FieldError("taskRequest", "priority", "não deve ser nulo")
        ));
        when(request.getRequestURI()).thenReturn("/api/v1/tasks");

        ResponseEntity<ApiError> response = handler.handleValidation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Dados inválidos.");
        assertThat(response.getBody().validationErrors())
                .containsEntry("title", "não deve estar em branco")
                .containsEntry("priority", "não deve ser nulo");
    }

    @Test
    void shouldReturn500WithoutExposingInternalExceptionDetails() {
        when(request.getRequestURI()).thenReturn("/api/v1/tasks");

        ResponseEntity<ApiError> response = handler.handleGeneric(
                new RuntimeException("database password leaked here"), request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Erro interno inesperado.");
        assertThat(response.getBody().message()).doesNotContain("database password");
        assertThat(response.getBody().validationErrors()).isEmpty();
    }
}
