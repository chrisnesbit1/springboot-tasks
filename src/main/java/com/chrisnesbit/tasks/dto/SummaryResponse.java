package com.chrisnesbit.tasks.dto;

import com.chrisnesbit.tasks.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Aggregate task counts grouped by status")
public record SummaryResponse(
        @Schema(description = "Number of tasks in each status")
        Map<TaskStatus, Long> counts,
        @Schema(description = "Total number of tasks", example = "5")
        long totalTasks
) {
}
