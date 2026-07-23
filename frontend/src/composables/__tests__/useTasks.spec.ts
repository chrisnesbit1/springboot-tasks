import { describe, expect, it, vi, beforeEach } from 'vitest'
import { useTasks } from '@/composables/useTasks'
import { ApiError } from '@/api/client'
import { withSetup } from './withSetup'
import { deferred } from './deferred'
import type { CreateTaskInput, Task, UpdateTaskInput } from '@/types/task'

vi.mock('@/api/tasks', () => ({
  listTasks: vi.fn<(signal?: AbortSignal) => Promise<Task[]>>(),
  createTask: vi.fn<(input: CreateTaskInput, signal?: AbortSignal) => Promise<Task>>(),
  updateTask: vi.fn<(id: string, input: UpdateTaskInput, signal?: AbortSignal) => Promise<Task>>(),
  deleteTask: vi.fn<(id: string, signal?: AbortSignal) => Promise<void>>(),
}))

import { listTasks, createTask, updateTask, deleteTask } from '@/api/tasks'

const openTask: Task = {
  id: '1',
  title: 'Write demo notes',
  description: 'Prep for review',
  status: 'OPEN',
  createdDate: '2026-07-23T00:00:00Z',
  completedDate: null,
}

beforeEach(() => {
  vi.clearAllMocks()
})

describe('useTasks', () => {
  it('loads tasks successfully', async () => {
    vi.mocked(listTasks).mockResolvedValue([openTask])

    const { result } = withSetup(() => useTasks())
    await result.load()

    expect(result.tasks.value).toEqual([openTask])
    expect(result.loading.value).toBe(false)
    expect(result.error.value).toBeNull()
  })

  it('reflects an empty task list', async () => {
    vi.mocked(listTasks).mockResolvedValue([])

    const { result } = withSetup(() => useTasks())
    await result.load()

    expect(result.tasks.value).toEqual([])
    expect(result.error.value).toBeNull()
  })

  it('surfaces an API failure without throwing', async () => {
    vi.mocked(listTasks).mockRejectedValue(new ApiError('Unable to reach the server.', 0, null))

    const { result } = withSetup(() => useTasks())
    await result.load()

    expect(result.error.value).toBe('Unable to reach the server.')
    expect(result.tasks.value).toEqual([])
  })

  it('creates a task and reloads the list', async () => {
    vi.mocked(createTask).mockResolvedValue(openTask)
    vi.mocked(listTasks).mockResolvedValue([openTask])

    const { result } = withSetup(() => useTasks())
    await result.create({ title: 'Write demo notes', description: 'Prep for review' })

    expect(createTask).toHaveBeenCalledWith({
      title: 'Write demo notes',
      description: 'Prep for review',
    })
    expect(result.tasks.value).toEqual([openTask])
  })

  it('propagates a validation failure from create to the caller', async () => {
    const validationError = new ApiError('Validation failed', 400, { title: 'Title is required' })
    vi.mocked(createTask).mockRejectedValue(validationError)

    const { result } = withSetup(() => useTasks())

    await expect(result.create({ title: '', description: null })).rejects.toBe(validationError)
  })

  it('updates completion status and reloads the list', async () => {
    const completedTask: Task = { ...openTask, status: 'COMPLETED', completedDate: '2026-07-23T01:00:00Z' }
    vi.mocked(updateTask).mockResolvedValue(completedTask)
    vi.mocked(listTasks).mockResolvedValue([completedTask])

    const { result } = withSetup(() => useTasks())
    await result.setStatus(openTask, 'COMPLETED')

    expect(updateTask).toHaveBeenCalledWith(openTask.id, {
      title: openTask.title,
      description: openTask.description,
      status: 'COMPLETED',
    })
    expect(result.tasks.value).toEqual([completedTask])
    expect(result.error.value).toBeNull()
  })

  it('sets a list-level error when updating status fails', async () => {
    vi.mocked(updateTask).mockRejectedValue(new ApiError('Task not found', 404, null))

    const { result } = withSetup(() => useTasks())
    await result.setStatus(openTask, 'COMPLETED')

    expect(result.error.value).toBe('Task not found')
  })

  it('deletes a task and reloads the list', async () => {
    vi.mocked(deleteTask).mockResolvedValue(undefined)
    vi.mocked(listTasks).mockResolvedValue([])

    const { result } = withSetup(() => useTasks())
    await result.remove(openTask)

    expect(deleteTask).toHaveBeenCalledWith(openTask.id)
    expect(result.tasks.value).toEqual([])
  })

  it('cancels a superseded load so only the latest response wins', async () => {
    const first = deferred<Task[]>()
    const second = deferred<Task[]>()
    let firstSignal: AbortSignal | undefined
    let secondSignal: AbortSignal | undefined

    vi.mocked(listTasks)
      .mockImplementationOnce((signal) => {
        firstSignal = signal
        signal?.addEventListener('abort', () => first.reject(new DOMException('Aborted', 'AbortError')))
        return first.promise
      })
      .mockImplementationOnce((signal) => {
        secondSignal = signal
        return second.promise
      })

    const { result } = withSetup(() => useTasks())

    const firstLoad = result.load()
    const secondLoad = result.load()

    expect(firstSignal?.aborted).toBe(true)
    second.resolve([openTask])

    await Promise.all([firstLoad, secondLoad])

    expect(secondSignal?.aborted).toBe(false)
    expect(result.tasks.value).toEqual([openTask])
    expect(result.error.value).toBeNull()
  })

  it('aborts an in-flight load when the component unmounts', async () => {
    const pending = deferred<Task[]>()
    let capturedSignal: AbortSignal | undefined

    vi.mocked(listTasks).mockImplementationOnce((signal) => {
      capturedSignal = signal
      return pending.promise
    })

    const { result, unmount } = withSetup(() => useTasks())
    result.load()
    unmount()

    expect(capturedSignal?.aborted).toBe(true)
  })
})
