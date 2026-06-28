package com.chrisnesbit.tasks.service;

import com.chrisnesbit.tasks.dto.TaskResponse;
import com.chrisnesbit.tasks.model.Task;
import com.chrisnesbit.tasks.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getTasks() {
        return taskRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.id(),
                task.title(),
                task.description(),
                task.status(),
                task.createdDate(),
                task.completedDate()
        );
    }
}
