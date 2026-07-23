export type TaskStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED'

export interface Task {
  id: string
  title: string
  description: string | null
  status: TaskStatus
  createdDate: string
  completedDate: string | null
}

export interface CreateTaskInput {
  title: string
  description: string | null
}

export interface UpdateTaskInput {
  title: string
  description: string | null
  status: TaskStatus
}

export interface TaskSummary {
  counts: Partial<Record<TaskStatus, number>>
  totalTasks: number
}

export interface ApiValidationErrors {
  [field: string]: string
}

export interface ApiErrorPayload {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  validationErrors: ApiValidationErrors | null
}
