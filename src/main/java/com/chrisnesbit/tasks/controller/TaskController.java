package com.chrisnesbit.tasks.controller;

import com.chrisnesbit.tasks.dto.CreateTaskRequest;
import com.chrisnesbit.tasks.dto.SummaryResponse;
import com.chrisnesbit.tasks.dto.TaskResponse;
import com.chrisnesbit.tasks.dto.UpdateTaskRequest;
import com.chrisnesbit.tasks.model.TaskStatus;
import com.chrisnesbit.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Create, update, query, and summarize task records")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(
            summary = "List tasks",
            description = "Returns all tasks, or only tasks matching the optional status query parameter."
    )
    @ApiResponse(responseCode = "200", description = "Tasks returned")
    public List<TaskResponse> getTasks(@RequestParam(required = false) TaskStatus status) {
        return taskService.getTasks(status);
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Summarize tasks",
            description = "Returns total task count and counts grouped by task status."
    )
    @ApiResponse(responseCode = "200", description = "Task summary returned")
    public SummaryResponse getSummary() {
        return taskService.getSummary();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task", description = "Returns one task by its UUID.")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public TaskResponse getTask(@PathVariable UUID id) {
        return taskService.getTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a task", description = "Creates a task with OPEN status.")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Request validation failed")
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Replaces title, description, and status for an existing task.")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "400", description = "Request validation failed")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task", description = "Deletes an existing task by UUID.")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }
}
