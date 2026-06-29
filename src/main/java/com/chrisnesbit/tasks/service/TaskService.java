package com.chrisnesbit.tasks.service;

import com.chrisnesbit.tasks.dto.CreateTaskRequest;
import com.chrisnesbit.tasks.dto.SummaryResponse;
import com.chrisnesbit.tasks.dto.TaskResponse;
import com.chrisnesbit.tasks.dto.UpdateTaskRequest;
import com.chrisnesbit.tasks.exception.TaskNotFoundException;
import com.chrisnesbit.tasks.model.Task;
import com.chrisnesbit.tasks.model.TaskStatus;
import com.chrisnesbit.tasks.repository.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getTasks(TaskStatus status) {
        if (status == null) {
            return taskRepository.findAll().stream()
                    .map(this::toResponse)
                    .toList();
        } else {
            return taskRepository.findAll().stream()
                    .filter(task -> task.status() == status)
                    .map(this::toResponse)
                    .toList();
        }
    }

    public SummaryResponse getSummary() {
        List<Task> tasks = taskRepository.findAll();
        Map<TaskStatus, Long> counts = tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::status,
                        Collectors.counting()
                ));

        return new SummaryResponse(counts, tasks.size());
    }

    public TaskResponse getTask(UUID id) {
        Task existingTask = taskRepository.findById(id).orElse(null);

        if (existingTask == null) {
            throw new TaskNotFoundException(id);
        }

        return toResponse(existingTask);
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
            throw new TaskNotFoundException(id);
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
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }

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
