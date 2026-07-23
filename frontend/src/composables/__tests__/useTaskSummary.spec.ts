import { describe, expect, it, vi, beforeEach } from 'vitest'
import { useTaskSummary } from '@/composables/useTaskSummary'
import { ApiError } from '@/api/client'
import { withSetup } from './withSetup'
import { deferred } from './deferred'
import type { TaskSummary } from '@/types/task'

vi.mock('@/api/tasks', () => ({
  getSummary: vi.fn<(signal?: AbortSignal) => Promise<TaskSummary>>(),
}))

import { getSummary } from '@/api/tasks'

beforeEach(() => {
  vi.clearAllMocks()
})

describe('useTaskSummary', () => {
  it('loads the summary successfully', async () => {
    const summary: TaskSummary = { counts: { OPEN: 2, COMPLETED: 1 }, totalTasks: 3 }
    vi.mocked(getSummary).mockResolvedValue(summary)

    const { result } = withSetup(() => useTaskSummary())
    await result.refresh()

    expect(result.summary.value).toEqual(summary)
    expect(result.error.value).toBeNull()
  })

  it('surfaces an API failure without throwing', async () => {
    vi.mocked(getSummary).mockRejectedValue(new ApiError('Unable to reach the server.', 0, null))

    const { result } = withSetup(() => useTaskSummary())
    await result.refresh()

    expect(result.error.value).toBe('Unable to reach the server.')
    expect(result.summary.value).toBeNull()
  })

  it('cancels a superseded refresh so only the latest counts win', async () => {
    // Mirrors the real scenario: two mutations in quick succession each
    // trigger a summary refresh, and the earlier request must not be
    // allowed to overwrite the later, correct counts.
    const first = deferred<TaskSummary>()
    const second = deferred<TaskSummary>()
    let firstSignal: AbortSignal | undefined

    vi.mocked(getSummary)
      .mockImplementationOnce((signal) => {
        firstSignal = signal
        signal?.addEventListener('abort', () => first.reject(new DOMException('Aborted', 'AbortError')))
        return first.promise
      })
      .mockImplementationOnce(() => second.promise)

    const { result } = withSetup(() => useTaskSummary())

    const firstRefresh = result.refresh()
    const secondRefresh = result.refresh()

    expect(firstSignal?.aborted).toBe(true)

    // The aborted first request settles before the second, still-in-flight
    // one resolves. Its own `finally` must not be allowed to clear
    // `loading` in the meantime.
    await firstRefresh
    expect(result.loading.value).toBe(true)

    second.resolve({ counts: { OPEN: 1 }, totalTasks: 1 })
    await secondRefresh

    expect(result.summary.value).toEqual({ counts: { OPEN: 1 }, totalTasks: 1 })
    expect(result.error.value).toBeNull()
    expect(result.loading.value).toBe(false)
  })

  it('aborts an in-flight refresh when the component unmounts', async () => {
    const pending = deferred<TaskSummary>()
    let capturedSignal: AbortSignal | undefined

    vi.mocked(getSummary).mockImplementationOnce((signal) => {
      capturedSignal = signal
      return pending.promise
    })

    const { result, unmount } = withSetup(() => useTaskSummary())
    result.refresh()
    unmount()

    expect(capturedSignal?.aborted).toBe(true)
  })
})
