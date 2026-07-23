import { apiClient } from '@/api/client'
import type { CreateTaskInput, Task, TaskSummary, UpdateTaskInput } from '@/types/task'

const BASE = '/tasks'

export function listTasks(signal?: AbortSignal): Promise<Task[]> {
  return apiClient.get<Task[]>(BASE, signal)
}

export function getSummary(signal?: AbortSignal): Promise<TaskSummary> {
  return apiClient.get<TaskSummary>(`${BASE}/summary`, signal)
}

export function createTask(input: CreateTaskInput, signal?: AbortSignal): Promise<Task> {
  return apiClient.post<Task>(BASE, input, signal)
}

export function updateTask(id: string, input: UpdateTaskInput, signal?: AbortSignal): Promise<Task> {
  return apiClient.put<Task>(`${BASE}/${id}`, input, signal)
}

export function deleteTask(id: string, signal?: AbortSignal): Promise<void> {
  return apiClient.delete(`${BASE}/${id}`, signal)
}
