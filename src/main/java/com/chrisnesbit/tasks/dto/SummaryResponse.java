package com.chrisnesbit.tasks.dto;

import com.chrisnesbit.tasks.model.TaskStatus;
import java.util.Map;

public record SummaryResponse(
        Map<TaskStatus, Long> counts,
        long totalTasks
) {
}
