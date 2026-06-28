package com.chrisnesbit.tasks.repository;

import com.chrisnesbit.tasks.model.Task;
import java.util.List;

public interface TaskRepository {

    List<Task> findAll();
}
