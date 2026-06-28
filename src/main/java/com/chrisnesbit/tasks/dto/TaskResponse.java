package com.chrisnesbit.tasks.dto;

import com.chrisnesbit.tasks.model.TaskStatus;
import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Instant createdDate,
        Instant completedDate
) {
}
