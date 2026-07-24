# AI-Assisted Review Notes — Vue 3 Frontend

Meaningful engineering decisions made while planning and building the frontend with Claude. This is not a prompt transcript — it records what was decided and why.

## User-Suggested Change to the Original Plan

The initial plan (approved before any code was written) covered list/create/toggle/delete with loading/empty/error states. Mid-review, the author asked for an additional piece: **a small summary strip near the header, sourced from the existing `GET /tasks/summary` endpoint, that refreshes after every task mutation — and explicitly asked for it to showcase proper Vue memory management.**

This was accepted and changed the design in a real way, not cosmetically:
- It introduced a second composable (`useTaskSummary`), separate from `useTasks`, composed together only in `App.vue` — kept independent since the summary widget is a distinct concern from the task list.
- Because a mutation can trigger a summary refresh before a previous refresh has resolved (e.g., toggling a task complete, then quickly deleting another), the refresh needed the same `AbortController` cancellation pattern already planned for the task list. This made the cleanup story consistent — the same real cause (a superseded fetch) shows up twice — rather than being a one-off.
- We explicitly rejected polling/interval-based refresh for the summary as a way to "demonstrate" cleanup — there's no multi-tab/multi-user scenario in this app that would justify it, and adding a timer here would have been cleanup theater rather than a genuine requirement. Event-driven refresh (after each mutation) was sufficient and honest.
- Tests were added specifically to prove the cancellation works (a superseded `refresh()`/`load()` call is discarded and only the later, correct result lands in state), rather than only asserting the code exists.

## Suggestions Accepted

- TypeScript over JavaScript — the API surface is small and stable, so mirroring the four backend DTOs as types costs little and catches contract drift at compile time.
- Native `fetch` over axios; a small hand-written `apiClient` wrapper over a generic HTTP client library.
- A dedicated task composable (`useTasks`) so `App.vue` doesn't mix networking, error handling, and template logic.
- `create-vue`'s official scaffold instead of hand-assembling Vite/ESLint/Vitest config.

## Suggestions Rejected

- **Pinia** — one composition root, no cross-page shared state; would be unused abstraction.
- **Vue Router** — single view, nothing to route between.
- **A UI component framework** — not present in the existing repo, not justified by this app's scope.
- **Polling for the summary widget** — see above; rejected in favor of event-driven refresh.
- **Auto-wiring the frontend build into `build.gradle`** — kept optional/undone this pass. The backend build and CI are untouched, at zero risk, and the manual path (copy `frontend/dist` into `src/main/resources/static`) is documented for whenever single-artifact deployment actually happens.

## Corrections Made After Reviewing Generated Code

- The scaffold's default `package.json` pinned `eslint-plugin-oxlint@~1.73.0` against `oxlint@~1.74.0`, which fails to resolve (`npm install` errored). Bumped both to matching `~1.75.0` before proceeding.
- The first draft of `TaskForm.vue` cleared its inputs immediately on submit and used an exposed `setValidationError` method for the parent to call back into. Reworked before it reached the app: the form now only clears via an explicit `reset()` the parent calls *after* a confirmed successful create, and server-side errors are passed down as a plain `serverError` prop — so a rejected submission never silently discards what the user typed, and data flows one direction (props down, events up) rather than the parent reaching into the child.
- A lint rule (`vitest(require-mock-type-parameters)`) caught untyped `vi.fn()` mocks in the composable tests; added explicit type parameters matching the real API function signatures.

## Risks Discovered

- `PUT /tasks/{id}` requires the full task body (title, description, status) — there's no PATCH. The "mark complete" checkbox therefore always resends the full object; this is dictated by the existing contract, not a frontend design choice, and is called out in the implementation summary.
- The checkbox toggle only distinguishes OPEN ↔ COMPLETED. `IN_PROGRESS` exists in the backend and is a valid `TaskStatus`, but reaching it isn't part of this UI's required scope (a "mark complete or incomplete" toggle, not a three-state picker) — noted as a known limitation rather than silently ignored.

## Verification Performed

- `npm run build` (type-check + production build), `npm run lint` (oxlint + eslint), and `npm run test:unit` (21 tests) all run clean after every stage.
- Backend `test` task run after the frontend was added, confirming zero backend changes/regressions.
- The running app was driven end-to-end with a real headless browser against the live backend (not just component tests in isolation): empty state, validation failure, create, toggle complete, toggle back, delete with confirmation, and the summary counts updating correctly after each step, with no console errors.

## Remaining Limitations

- No production build integration into the Spring Boot artifact (documented as a manual/optional step, not automated).
- No status filter UI, despite the backend supporting `?status=`.
- No pagination, authentication, or multi-user considerations — all explicitly out of scope from the start.

## Follow-Up (2026-07-24): Three-State Status Dropdown

This resolves the limitation logged above in "Risks Discovered" — the checkbox only distinguished OPEN ↔ COMPLETED, with `IN_PROGRESS` unreachable from the UI. The author asked for a way to set `IN_PROGRESS` with a distinct visual treatment, without changing the add-task form.

- **No backend changes.** `PUT /tasks/{id}` already accepted `IN_PROGRESS` as a valid `TaskStatus`; this was purely a frontend gap.
- **Native `<select>` over a custom dropdown component.** Three fixed, known options with no need for search/multi-select/async loading — a plain `<select>` satisfies the requirement without a new dependency, consistent with "no UI framework."
- **`TaskItem`'s `toggle` event became `status-change`**, carrying the task and the newly selected `TaskStatus` directly, replacing the old binary compute-the-opposite-status logic in `App.vue`. `useTasks`'s `setStatus(task, status)` needed no changes — it was already generic, just previously only ever called with two of the three possible values.
- **Visual treatment for `IN_PROGRESS`**: a left accent border, a light background tint on the whole task row, and an "In Progress" pill badge (the author explicitly asked for the pill in addition to the accent). `COMPLETED` intentionally keeps its original strikethrough-only rendering, unchanged — the author was explicit that marking a task complete should not alter how it looks today.
- Added `TaskItem.spec.ts` (previously only exercised indirectly through `TaskList.spec.ts`) to assert the dropdown's options/selected value, the emitted event payload, the pill/accent appearing only for `IN_PROGRESS`, and completed tasks rendering with no pill/accent.

### Verification Performed (this follow-up)

- `npm run test:unit` (27 tests, all passing), `npm run build` (type-check + build), and `npm run lint` all clean.
- `./gradlew test` re-run to confirm zero backend impact (no backend files touched).
- Live manual walkthrough with a headless browser against the real running app: created a task, moved it Open → In Progress (accent + pill appear) → Completed (pill/accent disappear, strikethrough applies, matching the pre-existing look) → back to Open, with the summary counts updating correctly at each step and no console errors.
