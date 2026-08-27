package br.com.cloudtask.mcp.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TaskView(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
