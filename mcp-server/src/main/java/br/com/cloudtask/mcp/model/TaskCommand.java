package br.com.cloudtask.mcp.model;

import java.time.LocalDate;

public record TaskCommand(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {
}
