# AI-Assisted Development Plan — Vue 3 Frontend

This document records the plan Claude proposed and the author approved before any frontend code was written, as part of adding a Vue 3 client to the existing Spring Boot Task Tracker API.

## Objective

Add a small, professional Vue 3 frontend demonstrating the Composition API, `<script setup>`, real API integration, honest lifecycle-cleanup practice, and the four core states (loading/empty/success/error) — without turning a weekend backend exercise into a larger project than it is.

## Repository Observations

- Spring Boot 4.0.7, Java 21, Gradle, WAR packaging with `SpringBootServletInitializer` and `providedRuntime` Tomcat — the build is already shaped for a future single-artifact servlet deployment (the README names AWS Elastic Beanstalk as a next step), even though nothing is deployed yet.
- Clean layered backend: `controller → service → repository interface → InMemoryTaskRepository`. DTOs are Java records with Bean Validation. One `@RestControllerAdvice` normalizes all error responses.
- No CORS configuration, no `src/main/resources/static` directory, no auth, no Docker, no existing `docs/` folder or AI-instruction file.
- Six relevant endpoints: `GET /tasks` (+ optional `?status=`), `GET /tasks/{id}`, `GET /tasks/summary`, `POST /tasks`, `PUT /tasks/{id}` (full-body replace, no PATCH), `DELETE /tasks/{id}`.

## Scope

**Required:** list tasks; create a task with validation; toggle complete/incomplete; delete with confirmation; loading/empty/error states; a summary strip near the header that refreshes after every mutation.

**Optional (not required for completion):** status filter, wiring the Gradle build to auto-copy the frontend build into Spring Boot's static resources for a single deployable artifact.

**Explicit non-goals:** authentication, Vue Router, Pinia, a UI component framework, animations, drag-and-drop, pagination, backend contract changes, production observability, full deployment automation.

## Architecture Decision

`frontend/` as a standalone Vue 3 + TypeScript project (via the official `create-vue` scaffold: TypeScript, ESLint, Prettier, Vitest; no Router, no Pinia, no E2E), developed with its own Vite dev server. The dev server proxies `/tasks` to the backend on `:8080`, so the browser sees same-origin requests in both dev and a same-origin production deployment — meaning **no CORS configuration was added to the backend**, and none of the existing API contracts changed.

Build integration into a single WAR was scoped as optional and left undone for this pass, to avoid adding Gradle-plugin complexity that wasn't needed to satisfy the stated goals; `frontend/README.md` documents the manual path if it's picked up later.

## Component Plan

```
App.vue (composition root: owns both composables, orchestrates cross-cutting refresh)
├─ TaskSummary.vue   (presentational — summary counts near the header)
├─ TaskForm.vue       (owns its own input state + client-side validation; emits 'create')
└─ TaskList.vue       (presentational — loading/empty/error + renders TaskItem list)
   └─ TaskItem.vue     (presentational — checkbox + delete button, emits events)
```

State lives in two composables: `useTasks` (list/create/toggle/delete) and `useTaskSummary` (header counts). No Pinia — a single composition root with two independent composables is sufficient for an app this size; there is no cross-page state to share.

## API Integration Plan

- `src/api/client.ts` — a ~60-line `fetch` wrapper (`apiClient.get/post/put/delete`), an `ApiError` class carrying `status`/`validationErrors`, `isAbortError`, and `toErrorMessage` (never surfaces raw error objects to the user).
- `src/api/tasks.ts` — five typed functions for the endpoints the required scope actually uses.
- `src/types/task.ts` — TypeScript types mirroring the backend DTOs field-for-field.
- Native `fetch`, not axios — the API surface is six simple JSON endpoints; a dependency wasn't justified.

## Memory-Management Considerations

The only external resources in this app are two `fetch` requests that can be re-triggered before a prior call resolves: the task list reload and the summary refresh (which fires after every mutation). Both use a per-call `AbortController`, cancelling the previous in-flight request before starting a new one — a genuine race-condition guard (proven directly by tests, not just asserted), plus an `onUnmounted` abort for correctness. Nothing else in the app — no timers, listeners, watchers, or subscriptions — requires cleanup, and none was added speculatively.

## Testing Plan

Vitest for the two composables (the actual business logic, including the cancellation behavior) and for two components whose rendered states are worth asserting on (`TaskList`'s four states, `TaskForm`'s validation). No E2E — disproportionate for this app's size.

## Definition of Done

Backend tests unmodified and passing; frontend builds, type-checks, lints, and its unit tests pass; a live manual walkthrough (create/toggle/delete/error/validation) against the running backend succeeds; AI artifacts accurately reflect what was actually built and decided.
