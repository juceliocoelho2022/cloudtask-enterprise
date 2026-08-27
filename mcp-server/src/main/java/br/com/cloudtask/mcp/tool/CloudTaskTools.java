package br.com.cloudtask.mcp.tool;

import br.com.cloudtask.mcp.client.CloudTaskApiClient;
import br.com.cloudtask.mcp.model.TaskCommand;
import br.com.cloudtask.mcp.model.TaskPriority;
import br.com.cloudtask.mcp.model.TaskStatus;
import br.com.cloudtask.mcp.model.TaskView;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Component
public class CloudTaskTools {

    private final CloudTaskApiClient apiClient;

    public CloudTaskTools(CloudTaskApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @McpTool(
            name = "list_tasks",
            description = "List CloudTask tasks for the authenticated user, optionally filtered by status.",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = true,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false
            )
    )
    public List<TaskView> listTasks(
            @McpToolParam(
                    description = "Optional status filter: TODO, IN_PROGRESS or DONE.",
                    required = false
            ) String status
    ) {
        return apiClient.listTasks(parseStatus(status, null));
    }

    @McpTool(
            name = "create_task",
            description = "Create a task for the authenticated CloudTask user.",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = false,
                    destructiveHint = false,
                    idempotentHint = false,
                    openWorldHint = false
            )
    )
    public TaskView createTask(
            @McpToolParam(description = "Task title.", required = true) String title,
            @McpToolParam(description = "Optional task description.", required = false) String description,
            @McpToolParam(
                    description = "Optional status: TODO, IN_PROGRESS or DONE. Defaults to TODO.",
                    required = false
            ) String status,
            @McpToolParam(
                    description = "Optional priority: LOW, MEDIUM, HIGH or CRITICAL. Defaults to MEDIUM.",
                    required = false
            ) String priority,
            @McpToolParam(
                    description = "Optional due date in ISO format YYYY-MM-DD.",
                    required = false
            ) String dueDate
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        TaskCommand command = new TaskCommand(
                title.trim(),
                normalizeOptionalText(description),
                parseStatus(status, TaskStatus.TODO),
                parsePriority(priority, TaskPriority.MEDIUM),
                parseDueDate(dueDate)
        );

        return apiClient.createTask(command);
    }

    @McpTool(
            name = "update_task_status",
            description = "Update only the status of an existing CloudTask task while preserving its other fields.",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = false,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false
            )
    )
    public TaskView updateTaskStatus(
            @McpToolParam(description = "Task identifier.", required = true) Long id,
            @McpToolParam(
                    description = "New status: TODO, IN_PROGRESS or DONE.",
                    required = true
            ) String status
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Task id must be greater than zero");
        }

        TaskView current = apiClient.findTask(id);
        TaskCommand command = new TaskCommand(
                current.title(),
                current.description(),
                parseStatus(status, null),
                current.priority(),
                current.dueDate()
        );

        return apiClient.updateTask(id, command);
    }

    @McpTool(
            name = "delete_task",
            description = "Permanently delete a CloudTask task owned by the authenticated user.",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = false,
                    destructiveHint = true,
                    idempotentHint = false,
                    openWorldHint = false
            )
    )
    public String deleteTask(
            @McpToolParam(description = "Task identifier to delete.", required = true) Long id
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Task id must be greater than zero");
        }

        apiClient.deleteTask(id);
        return "Task " + id + " deleted successfully";
    }

    private static TaskStatus parseStatus(String value, TaskStatus defaultValue) {
        if (value == null || value.isBlank()) {
            if (defaultValue != null) {
                return defaultValue;
            }
            return null;
        }

        try {
            return TaskStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid status. Use TODO, IN_PROGRESS or DONE",
                    ex
            );
        }
    }

    private static TaskPriority parsePriority(String value, TaskPriority defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return TaskPriority.valueOf(value.trim().toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid priority. Use LOW, MEDIUM, HIGH or CRITICAL",
                    ex
            );
        }
    }

    private static LocalDate parseDueDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value.trim());
        }
        catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid due date. Use YYYY-MM-DD", ex);
        }
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
