package com.chrisnesbit.tasks.model;

import java.time.Instant;
import java.util.UUID;

public record Task(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Instant createdDate,
        Instant completedDate
) {
}
