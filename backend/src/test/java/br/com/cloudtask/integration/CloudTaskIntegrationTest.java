package br.com.cloudtask.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudTaskIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("cloudtask_test")
            .withUsername("cloudtask")
            .withPassword("cloudtask");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCompleteAuthenticationAndTaskCrudFlow() {
        Map<String, Object> registerBody = Map.of(
                "name", "Jucelio Test",
                "email", "integration@cloudtask.dev",
                "password", "Senha@123"
        );

        ResponseEntity<JsonNode> registerResponse = restTemplate.postForEntity(
                "/api/v1/auth/register",
                jsonEntity(registerBody),
                JsonNode.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().get("email").asText()).isEqualTo("integration@cloudtask.dev");

        Map<String, Object> loginBody = Map.of(
                "email", "integration@cloudtask.dev",
                "password", "Senha@123"
        );

        ResponseEntity<JsonNode> loginResponse = restTemplate.postForEntity(
                "/api/v1/auth/login",
                jsonEntity(loginBody),
                JsonNode.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        String token = loginResponse.getBody().get("token").asText();
        assertThat(token).isNotBlank();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);

        Map<String, Object> createTaskBody = Map.of(
                "title", "Configurar CloudTask",
                "description", "Validar CRUD com Testcontainers",
                "status", "TODO",
                "priority", "HIGH",
                "dueDate", "2026-09-01"
        );

        ResponseEntity<JsonNode> createResponse = restTemplate.exchange(
                "/api/v1/tasks",
                HttpMethod.POST,
                new HttpEntity<>(createTaskBody, authHeaders),
                JsonNode.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        long taskId = createResponse.getBody().get("id").asLong();
        assertThat(createResponse.getBody().get("priority").asText()).isEqualTo("HIGH");

        ResponseEntity<JsonNode> listResponse = restTemplate.exchange(
                "/api/v1/tasks",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                JsonNode.class
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).isNotNull();
        assertThat(listResponse.getBody().isArray()).isTrue();
        assertThat(listResponse.getBody()).hasSize(1);

        Map<String, Object> updateTaskBody = Map.of(
                "title", "CloudTask validado",
                "description", "Fluxo integrado validado",
                "status", "DONE",
                "priority", "CRITICAL",
                "dueDate", "2026-09-02"
        );

        ResponseEntity<JsonNode> updateResponse = restTemplate.exchange(
                "/api/v1/tasks/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(updateTaskBody, authHeaders),
                JsonNode.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().get("status").asText()).isEqualTo("DONE");
        assertThat(updateResponse.getBody().get("priority").asText()).isEqualTo("CRITICAL");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/tasks/" + taskId,
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<JsonNode> finalListResponse = restTemplate.exchange(
                "/api/v1/tasks",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                JsonNode.class
        );

        assertThat(finalListResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(finalListResponse.getBody()).isNotNull();
        assertThat(finalListResponse.getBody()).hasSize(0);
    }

    private HttpEntity<Map<String, Object>> jsonEntity(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
