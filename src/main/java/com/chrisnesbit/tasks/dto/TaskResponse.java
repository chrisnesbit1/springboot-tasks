package com.chrisnesbit.tasks.dto;

import com.chrisnesbit.tasks.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Task returned by the API")
public record TaskResponse(
        @Schema(description = "Task identifier", example = "5c2f9c7a-7e0f-4a13-9f0a-4dd97b7c1a22")
        UUID id,
        @Schema(description = "Short task title", example = "Prepare portfolio demo")
        String title,
        @Schema(description = "Optional task details", example = "Review Swagger docs and test coverage before sharing the project.")
        String description,
        @Schema(description = "Current workflow status", example = "OPEN")
        TaskStatus status,
        @Schema(description = "UTC timestamp when the task was created", example = "2026-06-29T14:30:00Z")
        Instant createdDate,
        @Schema(description = "UTC timestamp when the task was completed", example = "2026-06-29T15:00:00Z")
        Instant completedDate
) {
}
