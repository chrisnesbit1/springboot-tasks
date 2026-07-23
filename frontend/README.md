# Task Tracker — Frontend

A small Vue 3 + TypeScript client for the Spring Boot Task Tracker API. See `../docs/vue-implementation-summary.md` for the full write-up (architecture, design decisions, testing, limitations).

## Prerequisites

The backend must be running for the app to have anything to talk to:

```bash
# from the repo root
./gradlew bootRun
```

## Setup

```bash
npm install
```

## Development

```bash
npm run dev
```

Runs on `http://localhost:5173`. The Vite dev server proxies `/tasks` requests to `http://localhost:8080`, so no CORS setup is needed.

## Build

```bash
npm run build
```

Type-checks (`vue-tsc`) and produces a production bundle in `dist/`.

## Tests

```bash
npm run test:unit
```

## Lint / Format

```bash
npm run lint
npm run format
```
