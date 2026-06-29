package com.chrisnesbit.tasks.controller;

import com.chrisnesbit.tasks.dto.CreateTaskRequest;
import com.chrisnesbit.tasks.dto.SummaryResponse;
import com.chrisnesbit.tasks.dto.TaskResponse;
import com.chrisnesbit.tasks.dto.UpdateTaskRequest;
import com.chrisnesbit.tasks.model.TaskStatus;
import com.chrisnesbit.tasks.service.TaskService;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getTasks(@RequestParam(required = false) TaskStatus status) {
        return taskService.getTasks(status);
    }

    @GetMapping("/summary")
    public SummaryResponse getSummary() {
        return taskService.getSummary();
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable UUID id) {
        return taskService.getTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }
}
