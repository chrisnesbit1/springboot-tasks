<script setup lang="ts">
import type { Task, TaskStatus } from '@/types/task'

const props = defineProps<{
  task: Task
}>()

const emit = defineEmits<{
  'status-change': [task: Task, status: TaskStatus]
  delete: [task: Task]
}>()

const STATUS_OPTIONS: { value: TaskStatus; label: string }[] = [
  { value: 'OPEN', label: 'Open' },
  { value: 'IN_PROGRESS', label: 'In Progress' },
  { value: 'COMPLETED', label: 'Completed' },
]

function handleStatusChange(event: Event) {
  const status = (event.target as HTMLSelectElement).value as TaskStatus
  emit('status-change', props.task, status)
}

function handleDelete() {
  emit('delete', props.task)
}
</script>

<template>
  <li class="task-item" :class="{ 'task-item--in-progress': task.status === 'IN_PROGRESS' }">
    <div class="task-item__main">
      <select
        class="task-item__status-select"
        :value="task.status"
        :aria-label="`Change status for '${task.title}'`"
        @change="handleStatusChange"
      >
        <option v-for="option in STATUS_OPTIONS" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>
      <span class="task-item__title" :class="{ 'task-item__title--done': task.status === 'COMPLETED' }">{{
        task.title
      }}</span>
      <span v-if="task.status === 'IN_PROGRESS'" class="task-item__badge">In Progress</span>
      <button
        type="button"
        class="task-item__delete"
        :aria-label="`Delete '${task.title}'`"
        @click="handleDelete"
      >
        Delete
      </button>
    </div>
    <p v-if="task.description" class="task-item__description">{{ task.description }}</p>
  </li>
</template>

<style scoped>
.task-item {
  border: 1px solid #ddd;
  border-left: 4px solid transparent;
  border-radius: 6px;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
}

.task-item--in-progress {
  border-left-color: #2f6fed;
  background: rgba(47, 111, 237, 0.06);
}

.task-item__main {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.task-item__status-select {
  flex-shrink: 0;
}

.task-item__title {
  flex: 1;
}

.task-item__title--done {
  text-decoration: line-through;
  color: #888;
}

.task-item__badge {
  background: #2f6fed;
  color: #fff;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  padding: 0.15rem 0.55rem;
  border-radius: 999px;
  white-space: nowrap;
}

.task-item__delete {
  background: none;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 0.25rem 0.6rem;
  cursor: pointer;
  color: #b00020;
}

.task-item__description {
  margin: 0.35rem 0 0;
  color: #444;
}
</style>
