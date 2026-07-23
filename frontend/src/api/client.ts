import type { ApiErrorPayload, ApiValidationErrors } from '@/types/task'

export class ApiError extends Error {
  readonly status: number
  readonly validationErrors: ApiValidationErrors | null

  constructor(message: string, status: number, validationErrors: ApiValidationErrors | null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.validationErrors = validationErrors
  }
}

export function isAbortError(error: unknown): boolean {
  return error instanceof DOMException && error.name === 'AbortError'
}

// Normalizes any thrown error into a message safe to show a user.
// Never surfaces raw error objects/stack traces (see CLAUDE.md security expectations).
export function toErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    return error.message
  }
  return 'Unable to reach the server. Please check your connection and try again.'
}

interface RequestOptions {
  method?: string
  body?: unknown
  signal?: AbortSignal
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await fetch(path, {
    method: options.method ?? 'GET',
    headers: options.body !== undefined ? { 'Content-Type': 'application/json' } : undefined,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    signal: options.signal,
  })

  if (response.status === 204) {
    return undefined as T
  }

  const payload = await response.json().catch(() => null)

  if (!response.ok) {
    const errorPayload = payload as ApiErrorPayload | null
    throw new ApiError(
      errorPayload?.message ?? `Request failed with status ${response.status}`,
      response.status,
      errorPayload?.validationErrors ?? null,
    )
  }

  return payload as T
}

export const apiClient = {
  get: <T>(path: string, signal?: AbortSignal) => request<T>(path, { signal }),
  post: <T>(path: string, body: unknown, signal?: AbortSignal) =>
    request<T>(path, { method: 'POST', body, signal }),
  put: <T>(path: string, body: unknown, signal?: AbortSignal) =>
    request<T>(path, { method: 'PUT', body, signal }),
  delete: (path: string, signal?: AbortSignal) => request<void>(path, { method: 'DELETE', signal }),
}
