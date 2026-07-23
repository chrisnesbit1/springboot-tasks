import { onUnmounted, ref } from 'vue'
import { createTask, deleteTask, listTasks, updateTask } from '@/api/tasks'
import { isAbortError, toErrorMessage } from '@/api/client'
import type { CreateTaskInput, Task, TaskStatus } from '@/types/task'

export function useTasks() {
  const tasks = ref<Task[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  let loadController: AbortController | null = null

  async function load() {
    // Cancel a previous load still in flight so a slow, superseded
    // response can't overwrite the result of a newer one.
    loadController?.abort()
    loadController = new AbortController()

    loading.value = true
    error.value = null

    try {
      tasks.value = await listTasks(loadController.signal)
    } catch (err) {
      if (isAbortError(err)) return
      error.value = toErrorMessage(err)
    } finally {
      loading.value = false
    }
  }

  // Left to the caller: a create failure is shown inline on the form
  // the user is actively filling out, not as a list-level banner.
  async function create(input: CreateTaskInput) {
    await createTask(input)
    await load()
  }

  // Toggle/delete act on an existing list item, so their failures are
  // surfaced through the same list-level error state as a failed load.
  async function setStatus(task: Task, status: TaskStatus) {
    error.value = null
    try {
      await updateTask(task.id, {
        title: task.title,
        description: task.description,
        status,
      })
      await load()
    } catch (err) {
      error.value = toErrorMessage(err)
    }
  }

  async function remove(task: Task) {
    error.value = null
    try {
      await deleteTask(task.id)
      await load()
    } catch (err) {
      error.value = toErrorMessage(err)
    }
  }

  onUnmounted(() => {
    loadController?.abort()
  })

  return { tasks, loading, error, load, create, setStatus, remove }
}
