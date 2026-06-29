package com.chrisnesbit.tasks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    private static final Instant CREATED_DATE = Instant.parse("2026-01-01T12:00:00Z");

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTasksReturnsAllTasksWhenStatusIsNull() {
        Task openTask = task(UUID.randomUUID(), "Open task", TaskStatus.OPEN);
        Task completedTask = task(UUID.randomUUID(), "Completed task", TaskStatus.COMPLETED);
        when(taskRepository.findAll()).thenReturn(List.of(openTask, completedTask));

        List<TaskResponse> tasks = taskService.getTasks(null);

        assertThat(tasks).extracting(TaskResponse::id)
                .containsExactly(openTask.id(), completedTask.id());
        verify(taskRepository).findAll();
    }

    @Test
    void getTasksFiltersByStatus() {
        Task openTask = task(UUID.randomUUID(), "Open task", TaskStatus.OPEN);
        Task completedTask = task(UUID.randomUUID(), "Completed task", TaskStatus.COMPLETED);
        when(taskRepository.findAll()).thenReturn(List.of(openTask, completedTask));

        List<TaskResponse> tasks = taskService.getTasks(TaskStatus.COMPLETED);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.getFirst().id()).isEqualTo(completedTask.id());
        assertThat(tasks.getFirst().status()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    void getSummaryReturnsCountsByStatus() {
        when(taskRepository.findAll()).thenReturn(List.of(
                task(UUID.randomUUID(), "Open task", TaskStatus.OPEN),
                task(UUID.randomUUID(), "In progress task", TaskStatus.IN_PROGRESS),
                task(UUID.randomUUID(), "Completed task", TaskStatus.COMPLETED),
                task(UUID.randomUUID(), "Another completed task", TaskStatus.COMPLETED)
        ));

        SummaryResponse summary = taskService.getSummary();

        assertThat(summary.totalTasks()).isEqualTo(4);
        assertThat(summary.counts()).containsEntry(TaskStatus.OPEN, 1L);
        assertThat(summary.counts()).containsEntry(TaskStatus.IN_PROGRESS, 1L);
        assertThat(summary.counts()).containsEntry(TaskStatus.COMPLETED, 2L);
    }

    @Test
    void getTaskThrowsWhenTaskDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found: " + id);
    }

    @Test
    void createTaskSavesOpenTask() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.createTask(
                new CreateTaskRequest("New task", "Created from a request")
        );

        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("New task");
        assertThat(response.description()).isEqualTo("Created from a request");
        assertThat(response.status()).isEqualTo(TaskStatus.OPEN);
        assertThat(response.createdDate()).isNotNull();
        assertThat(response.completedDate()).isNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskPreservesCreatedDateAndSetsCompletedDate() {
        UUID id = UUID.randomUUID();
        Task existingTask = task(id, "Existing task", TaskStatus.OPEN);
        when(taskRepository.findById(id)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTask(
                id,
                new UpdateTaskRequest("Existing task", "Done", TaskStatus.COMPLETED)
        );

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.createdDate()).isEqualTo(CREATED_DATE);
        assertThat(response.completedDate()).isNotNull();
        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskThrowsWhenTaskDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(
                id,
                new UpdateTaskRequest("Missing task", "No record", TaskStatus.OPEN)
        )).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteTaskRemovesExistingTask() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(true);

        taskService.deleteTask(id);

        verify(taskRepository).deleteById(id);
    }

    @Test
    void deleteTaskThrowsWhenTaskDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(id))
                .isInstanceOf(TaskNotFoundException.class);
    }

    private Task task(UUID id, String title, TaskStatus status) {
        return new Task(
                id,
                title,
                "Description for " + title,
                status,
                CREATED_DATE,
                status == TaskStatus.COMPLETED ? CREATED_DATE.plusSeconds(60) : null
        );
    }
}
