package com.chrisnesbit.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,
        @Size(max = 100, message = "Description cannot exceed 1,000 characters")
        String description
) {
}
