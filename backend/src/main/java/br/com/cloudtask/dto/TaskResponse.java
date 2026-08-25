package br.com.cloudtask.dto;

import br.com.cloudtask.domain.TaskPriority;
import br.com.cloudtask.domain.TaskStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
