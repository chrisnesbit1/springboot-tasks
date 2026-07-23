<script setup lang="ts">
import type { Task } from '@/types/task'

const props = defineProps<{
  task: Task
}>()

const emit = defineEmits<{
  toggle: [task: Task]
  delete: [task: Task]
}>()

function handleToggle() {
  emit('toggle', props.task)
}

function handleDelete() {
  emit('delete', props.task)
}
</script>

<template>
  <li class="task-item">
    <div class="task-item__main">
      <label class="task-item__checkbox">
        <input
          type="checkbox"
          :checked="task.status === 'COMPLETED'"
          :aria-label="`Mark '${task.title}' as ${task.status === 'COMPLETED' ? 'incomplete' : 'complete'}`"
          @change="handleToggle"
        />
        <span :class="{ 'task-item__title--done': task.status === 'COMPLETED' }">{{ task.title }}</span>
      </label>
      <span class="task-item__status">{{ task.status }}</span>
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
  border-radius: 6px;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
}

.task-item__main {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.task-item__checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  cursor: pointer;
}

.task-item__title--done {
  text-decoration: line-through;
  color: #888;
}

.task-item__status {
  font-size: 0.75rem;
  text-transform: uppercase;
  color: #666;
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
