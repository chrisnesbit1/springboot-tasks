package com.chrisnesbit.tasks.controller;

import com.chrisnesbit.tasks.dto.TaskResponse;
import com.chrisnesbit.tasks.service.TaskService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getTasks() {
        return taskService.getTasks();
    }
}
