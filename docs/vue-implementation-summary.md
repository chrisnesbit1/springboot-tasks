# Vue Implementation Summary

## What Was Added

A small Vue 3 + TypeScript frontend (`frontend/`) for the existing Spring Boot Task Tracker API: list tasks, create a task, mark complete/incomplete, delete with confirmation, and a summary strip near the header that refreshes after every mutation. Built with `<script setup>` and the Composition API throughout.

## How to Run It

**Backend** (from the repo root):
```bash
./gradlew bootRun
```
Runs on `http://localhost:8080`.

**Frontend** (from `frontend/`):
```bash
npm install
npm run dev
```
Runs on `http://localhost:5173` with the backend already running.

## How Frontend and Backend Communicate

The frontend calls relative paths (`/tasks`, `/tasks/summary`, ...) via a small `fetch` wrapper. In development, Vite's dev-server proxy forwards those paths to `http://localhost:8080`, so the browser sees same-origin requests — no CORS configuration was needed or added to the backend. In a production deployment where the built frontend assets are served from the backend's own static resources (the natural fit given the existing WAR/servlet-container setup), the same relative paths resolve correctly with no code change.

## Important Design Decisions

- **No Pinia, no Vue Router, no axios, no UI framework.** One view, one composition root (`App.vue`), two composables, six simple JSON endpoints — none of these were justified by the app's actual requirements.
- **Two independent composables**: `useTasks` (list/create/toggle/delete) and `useTaskSummary` (header counts), composed together in `App.vue`, which also owns the policy of *when* to refresh the summary (after every successful mutation) and the delete-confirmation prompt.
- **Component boundaries**: `App.vue` owns data and orchestration; `TaskSummary`, `TaskList`, `TaskItem` are presentational (props down, events up); `TaskForm` owns its own input state and client-side validation but leaves error display and reset timing to its parent, so a failed submission never silently clears what the user typed.
- **`PUT` requires a full task body** (there's no PATCH on the backend) — so toggling completion always resends title/description/status together. This is a consequence of the existing API contract, not a frontend choice, and the contract was not changed.

## Memory Management

The only external resources in this app are two `fetch` requests that can be re-triggered before a previous call resolves — the task list reload and the summary refresh (fired after every mutation). Both use a per-call `AbortController`: a new call cancels whatever call of the same kind is still in flight, preventing a slower, superseded response from overwriting the UI with stale data — a real race condition, not a hypothetical one, since a fast sequence of actions (e.g. delete-then-toggle) can genuinely trigger overlapping summary refreshes. Both composables also abort on unmount for correctness, though in this single-view app that mostly matters for the same-session race guard rather than route-change cleanup, since there's no router.

Everything else — component state, template event bindings — is torn down automatically by Vue; no additional `onUnmounted` hooks were added anywhere else in the codebase.

## Testing Performed

- **Automated**: 21 Vitest tests — `useTasks` and `useTaskSummary` (success, empty/failure states, create/update/delete, and two tests that directly prove the `AbortController` cancellation logic works, including the unmount case), plus `TaskList` (all four render states) and `TaskForm` (validation and server-error display).
- **Manual, live**: drove the actual running app with a headless browser against the real backend — empty state, a blank-form validation error, creating a task, toggling complete and back, deleting with the confirmation dialog, and the summary counts updating correctly after each step. No console errors observed at any point.
- **Backend**: existing test suite re-run after the frontend was added; unaffected, as expected, since no backend files were changed.

## Known Limitations

- The completion toggle is binary (OPEN ↔ COMPLETED); the backend's `IN_PROGRESS` status exists but isn't reachable from this UI, by design (see `docs/ai-assisted-review-notes.md`).
- No status filter UI, though the backend supports `?status=`.
- Production build integration (copying `frontend/dist` into `src/main/resources/static` for a single deployable WAR) is documented but not automated in `build.gradle`.
- No authentication, pagination, or multi-user concerns — all out of scope from the outset.

## Possible Next Steps

- Wire a Gradle task to build the frontend and copy its output into Spring Boot's static resources, producing one deployable artifact.
- Add the optional status-filter UI against the existing `?status=` query parameter.
- If the backend ever grows a genuine multi-view need, revisit Vue Router at that point — not before.
