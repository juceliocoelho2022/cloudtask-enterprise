package br.com.cloudtask.security;

import br.com.cloudtask.domain.Role;
import br.com.cloudtask.domain.User;
import br.com.cloudtask.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.oauth.frontend-redirect:http://localhost:5173}")
    private String frontendRedirect;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId().toLowerCase(Locale.ROOT);

        String name = resolveName(provider, principal);
        String email = resolveEmail(provider, principal);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .name(name)
                        .email(email)
                        .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .role(Role.USER)
                        .build()));

        String jwt = jwtService.generateToken(user.getEmail());
        String redirect = frontendRedirect
                + "/oauth2/callback#token=" + encode(jwt)
                + "&name=" + encode(user.getName())
                + "&email=" + encode(user.getEmail())
                + "&provider=" + encode(provider);

        response.sendRedirect(redirect);
    }

    private String resolveName(String provider, OAuth2User principal) {
        String name = principal.getAttribute("name");
        if (name != null && !name.isBlank()) return name.trim();

        if ("github".equals(provider)) {
            String login = principal.getAttribute("login");
            if (login != null && !login.isBlank()) return login.trim();
        }

        return "CloudTask User";
    }

    private String resolveEmail(String provider, OAuth2User principal) {
        String email = principal.getAttribute("email");
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase(Locale.ROOT);
        }

        if ("github".equals(provider)) {
            String login = principal.getAttribute("login");
            Object id = principal.getAttribute("id");
            String identity = login != null && !login.isBlank() ? login : String.valueOf(id);
            return identity.toLowerCase(Locale.ROOT) + "@users.noreply.github.com";
        }

        throw new IllegalStateException("O provedor OAuth não retornou um e-mail utilizável.");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
