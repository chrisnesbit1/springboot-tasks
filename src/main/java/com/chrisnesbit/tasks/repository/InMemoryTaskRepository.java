package com.chrisnesbit.tasks.repository;

import com.chrisnesbit.tasks.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<UUID, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }
}
