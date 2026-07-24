<script setup lang="ts">
import type { Task, TaskStatus } from '@/types/task'
import TaskItem from '@/components/TaskItem.vue'

defineProps<{
  tasks: Task[]
  loading: boolean
  error: string | null
}>()

const emit = defineEmits<{
  'status-change': [task: Task, status: TaskStatus]
  delete: [task: Task]
}>()
</script>

<template>
  <div class="task-list">
    <p v-if="error" class="task-list__error" role="alert">{{ error }}</p>
    <p v-else-if="loading" class="task-list__status">Loading tasks…</p>
    <p v-else-if="tasks.length === 0" class="task-list__status">
      No tasks yet. Add one above to get started.
    </p>
    <ul v-else class="task-list__items">
      <TaskItem
        v-for="task in tasks"
        :key="task.id"
        :task="task"
        @status-change="(_task, status) => emit('status-change', task, status)"
        @delete="emit('delete', task)"
      />
    </ul>
  </div>
</template>

<style scoped>
.task-list__error {
  color: #b00020;
}

.task-list__status {
  color: #666;
}

.task-list__items {
  list-style: none;
  padding: 0;
  margin: 0;
}
</style>
