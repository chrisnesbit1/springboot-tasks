package com.chrisnesbit.tasks.service;

import com.chrisnesbit.tasks.dto.CreateTaskRequest;
import com.chrisnesbit.tasks.dto.TaskResponse;
import com.chrisnesbit.tasks.dto.UpdateTaskRequest;
import com.chrisnesbit.tasks.model.Task;
import com.chrisnesbit.tasks.model.TaskStatus;
import com.chrisnesbit.tasks.repository.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    public TaskResponse getTask(UUID id) {
        return taskRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = taskRepository.save(new Task(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                TaskStatus.OPEN,
                Instant.now(),
                null
        ));
        return toResponse(task);
    }

    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        Task existingTask = taskRepository.findById(id).orElse(null);

        if (existingTask == null) {
            return null;
        }

        Instant completedDate = request.status() == TaskStatus.COMPLETED
                ? Instant.now()
                : null;

        Task task = taskRepository.save(new Task(
                id,
                request.title(),
                request.description(),
                request.status(),
                existingTask.createdDate(),
                completedDate
        ));

        return toResponse(task);
    }

    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
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
