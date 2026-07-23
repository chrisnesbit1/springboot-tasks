<script setup lang="ts">
import type { TaskSummary as TaskSummaryData } from '@/types/task'

defineProps<{
  summary: TaskSummaryData | null
  loading: boolean
  error: string | null
}>()
</script>

<template>
  <div class="task-summary" aria-live="polite">
    <span v-if="error" class="task-summary__error">Summary unavailable: {{ error }}</span>
    <template v-else-if="summary">
      <span>{{ summary.totalTasks }} total</span>
      <span>&middot; {{ summary.counts.OPEN ?? 0 }} open</span>
      <span>&middot; {{ summary.counts.IN_PROGRESS ?? 0 }} in progress</span>
      <span>&middot; {{ summary.counts.COMPLETED ?? 0 }} completed</span>
    </template>
    <span v-else-if="loading" class="task-summary__status">Loading summary…</span>
  </div>
</template>

<style scoped>
.task-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
  font-size: 0.875rem;
  color: #555;
  margin-bottom: 1.25rem;
}

.task-summary__error {
  color: #b00020;
}

.task-summary__status {
  color: #666;
}
</style>
