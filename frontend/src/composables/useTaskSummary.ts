import { onUnmounted, ref } from 'vue'
import { getSummary } from '@/api/tasks'
import { isAbortError, toErrorMessage } from '@/api/client'
import type { TaskSummary } from '@/types/task'

export function useTaskSummary() {
  const summary = ref<TaskSummary | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  let controller: AbortController | null = null

  async function refresh() {
    // A mutation (create/update/delete) can trigger a refresh before a
    // prior one has resolved. Cancel the stale request so an
    // out-of-order response can't overwrite the header with old counts.
    controller?.abort()
    controller = new AbortController()

    loading.value = true
    error.value = null

    try {
      summary.value = await getSummary(controller.signal)
    } catch (err) {
      if (isAbortError(err)) return
      error.value = toErrorMessage(err)
    } finally {
      loading.value = false
    }
  }

  onUnmounted(() => {
    controller?.abort()
  })

  return { summary, loading, error, refresh }
}
