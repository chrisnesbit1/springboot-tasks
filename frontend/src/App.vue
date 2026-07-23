<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useTasks } from '@/composables/useTasks'
import { useTaskSummary } from '@/composables/useTaskSummary'
import { ApiError } from '@/api/client'
import TaskSummary from '@/components/TaskSummary.vue'
import TaskForm from '@/components/TaskForm.vue'
import TaskList from '@/components/TaskList.vue'
import type { CreateTaskInput, Task, TaskStatus } from '@/types/task'

const { tasks, loading, error, load, create, setStatus, remove } = useTasks()
const {
  summary,
  loading: summaryLoading,
  error: summaryError,
  refresh: refreshSummary,
} = useTaskSummary()

const taskForm = ref<InstanceType<typeof TaskForm> | null>(null)
const createError = ref<string | null>(null)

onMounted(() => {
  load()
  refreshSummary()
})

async function handleCreate(input: CreateTaskInput) {
  createError.value = null
  try {
    await create(input)
    taskForm.value?.reset()
    await refreshSummary()
  } catch (err) {
    createError.value = err instanceof ApiError ? err.message : 'Unable to create task. Please try again.'
  }
}

async function handleToggle(task: Task) {
  const nextStatus: TaskStatus = task.status === 'COMPLETED' ? 'OPEN' : 'COMPLETED'
  await setStatus(task, nextStatus)
  // setStatus/remove record their own failures in `error`, so only refresh
  // the summary when the mutation actually succeeded.
  if (!error.value) {
    await refreshSummary()
  }
}

async function handleDelete(task: Task) {
  if (!window.confirm(`Delete "${task.title}"? This cannot be undone.`)) return
  await remove(task)
  if (!error.value) {
    await refreshSummary()
  }
}
</script>

<template>
  <main class="app">
    <h1>Task Tracker</h1>
    <TaskSummary :summary="summary" :loading="summaryLoading" :error="summaryError" />
    <TaskForm ref="taskForm" :server-error="createError" @create="handleCreate" />
    <TaskList
      :tasks="tasks"
      :loading="loading"
      :error="error"
      @toggle="handleToggle"
      @delete="handleDelete"
    />
  </main>
</template>

<style scoped>
.app {
  max-width: 640px;
  margin: 2rem auto;
  padding: 0 1rem 2rem;
  font-family:
    system-ui,
    -apple-system,
    sans-serif;
}
</style>
