package com.chrisnesbit.tasks.repository;

import com.chrisnesbit.tasks.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(UUID id);

    Task save(Task task);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
