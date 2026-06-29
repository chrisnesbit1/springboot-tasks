package com.chrisnesbit.tasks.dto;

import com.chrisnesbit.tasks.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for replacing an existing task")
public record UpdateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        @Schema(description = "Short task title", example = "Prepare portfolio demo")
        String title,
        @Size(max = 1000, message = "Description cannot exceed 1,000 characters")
        @Schema(description = "Optional task details", example = "Review Swagger docs and test coverage before sharing the project.")
        String description,
        @NotNull(message = "Status is required")
        @Schema(description = "Current workflow status", example = "IN_PROGRESS")
        TaskStatus status
) {
}
