# CLAUDE.md

Instructions for Claude (or any AI assistant) working in this repository. Read the section you need — you don't need the whole file for a small change.

## Contents

1. [Architecture](#architecture)
2. [Build & Test Commands](#build--test-commands)
3. [Spring Boot Conventions](#spring-boot-conventions)
4. [Vue Conventions](#vue-conventions)
5. [API Contract Boundaries](#api-contract-boundaries)
6. [Dependency Rules](#dependency-rules)
7. [Testing Expectations](#testing-expectations)
8. [Lifecycle Cleanup Expectations](#lifecycle-cleanup-expectations)
9. [Security Expectations](#security-expectations)
10. [Planning & Verification Requirements](#planning--verification-requirements)
11. [Prohibited](#prohibited)

## Architecture

Two parts in one repo:

- **Backend** (`src/`): Spring Boot 4, Java 21, Gradle. Layered `controller → service → repository (interface) → InMemoryTaskRepository`. DTOs are records with Bean Validation. A single `@RestControllerAdvice` (`GlobalExceptionHandler`) normalizes all error responses.
- **Frontend** (`frontend/`): Vue 3 + `<script setup>` + TypeScript, scaffolded via `create-vue`, built with Vite. Talks to the backend over relative paths (`/tasks`) — same-origin in dev via a Vite proxy, same-origin in prod because the built assets are intended to be served from the backend's static resources.

Full narrative and rationale: `docs/ai-assisted-development-plan.md`, `docs/vue-implementation-summary.md`.

## Build & Test Commands

Backend (from repo root):
```
./gradlew test    # run backend tests
./gradlew build   # full build, includes tests
./gradlew bootRun # run the API on :8080
```

Frontend (from `frontend/`):
```
npm install
npm run dev          # Vite dev server on :5173, proxies /tasks to :8080
npm run build        # type-check + production build
npm run test:unit    # Vitest
npm run lint         # oxlint + eslint --fix
npm run format       # prettier
```

## Spring Boot Conventions

- DTOs are records, not classes.
- Constructor injection only, no field injection.
- Validation lives on the request DTOs (`jakarta.validation` annotations), not in controllers or services.
- All error responses go through `GlobalExceptionHandler` — don't add ad hoc `try/catch` → custom response bodies in controllers.
- Keep the `controller/service/repository/dto/model/exception/config` package layout.

## Vue Conventions

- `<script setup lang="ts">` only, Composition API only — no Options API.
- No Vue Router, no Pinia. This is a single-view app with one composition root (`App.vue`); don't add either without a genuine new requirement (see [Dependency Rules](#dependency-rules)).
- State/data-fetching logic lives in composables (`src/composables/`); components stay presentational where practical and communicate via props down / events up.
- Native `fetch` via `src/api/`, not axios.
- API response/request shapes are typed in `src/types/` and must mirror the backend DTOs exactly — check `src/main/java/.../dto/` before assuming a field name or shape.

## API Contract Boundaries

The backend API (`TaskController`) is the source of truth. Do not:
- invent endpoints, fields, or status codes that don't exist in the controller/DTOs
- assume partial-update (PATCH) semantics — `PUT /tasks/{id}` requires the full body (title, description, status)
- change backend request/response shapes to make the frontend simpler — fix the frontend instead, unless there's a clearly justified reason, in which case say so explicitly before changing it

## Dependency Rules

Don't add a dependency (frontend or backend) without stating, in the commit or PR description, what problem it solves and why the existing tools don't cover it. In particular, don't reach for Pinia, Vue Router, axios, a UI component framework, or an E2E framework unless a real new requirement appears that the current stack can't satisfy — "it's common in Vue apps" is not sufficient justification on its own.

## Testing Expectations

- Backend: MockMvc controller tests + service unit tests already exist as the pattern — follow it for new endpoints.
- Frontend: Vitest for composables (the logic that matters) and for component states (loading/empty/error) worth asserting on. No E2E — disproportionate for this app's size. Don't chase coverage numbers; test behavior that could plausibly break.

## Lifecycle Cleanup Expectations

- Vue's own reactivity/component teardown handles normal `ref`/`reactive` state — don't add `onUnmounted` for that.
- Only add cleanup for real external resources. Currently that's exactly one thing: in-flight `fetch` requests that can be superseded by a newer request of the same kind (task list reload, summary refresh) — cancelled via `AbortController`. If you add a genuinely new external resource (timer, subscription, listener, WebSocket), clean it up; don't add speculative cleanup for resources that don't exist.

## Security Expectations

- No auth exists; don't add security theater (fake login, client-side-only guards) without a real requirement.
- Never log or display raw error objects/stack traces to the user — surface the normalized message from `ApiErrorResponse`.
- Don't introduce `v-html` or any raw HTML injection from API data.

## Planning & Verification Requirements

- For anything broader than a small, obviously-scoped fix, propose a plan and wait for approval before editing files.
- After a meaningful change: run the relevant build/tests/lint above for whichever side you touched, and report the actual results — don't claim success without running them.

## Prohibited

- Modifying files unrelated to the task at hand.
- Inventing backend behavior, endpoints, or fields that don't exist in the controller/DTOs.
- Silent scope expansion beyond what was asked or approved.
