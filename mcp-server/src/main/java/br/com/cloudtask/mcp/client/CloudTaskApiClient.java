package br.com.cloudtask.mcp.client;

import br.com.cloudtask.mcp.config.CloudTaskApiProperties;
import br.com.cloudtask.mcp.model.TaskCommand;
import br.com.cloudtask.mcp.model.TaskStatus;
import br.com.cloudtask.mcp.model.TaskView;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class CloudTaskApiClient {

    private final CloudTaskApiProperties properties;
    private final RestClient restClient;

    public CloudTaskApiClient(CloudTaskApiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public List<TaskView> listTasks(TaskStatus status) {
        TaskView[] tasks = restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/api/v1/tasks");
                    if (status != null) {
                        builder.queryParam("status", status.name());
                    }
                    return builder.build();
                })
                .headers(this::applyAuthorization)
                .retrieve()
                .body(TaskView[].class);

        return tasks == null ? List.of() : Arrays.asList(tasks);
    }

    public TaskView createTask(TaskCommand command) {
        return restClient.post()
                .uri("/api/v1/tasks")
                .headers(this::applyAuthorization)
                .body(command)
                .retrieve()
                .body(TaskView.class);
    }

    public TaskView updateTask(Long id, TaskCommand command) {
        return restClient.put()
                .uri("/api/v1/tasks/{id}", id)
                .headers(this::applyAuthorization)
                .body(command)
                .retrieve()
                .body(TaskView.class);
    }

    public TaskView findTask(Long id) {
        return listTasks(null).stream()
                .filter(task -> task.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public void deleteTask(Long id) {
        restClient.delete()
                .uri("/api/v1/tasks/{id}", id)
                .headers(this::applyAuthorization)
                .retrieve()
                .toBodilessEntity();
    }

    private void applyAuthorization(HttpHeaders headers) {
        if (properties.token() == null || properties.token().isBlank()) {
            throw new IllegalStateException(
                    "CLOUDTASK_API_TOKEN is required to call the authenticated CloudTask API"
            );
        }
        headers.setBearerAuth(properties.token());
    }
}
