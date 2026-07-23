<script setup lang="ts">
import { ref } from 'vue'
import type { CreateTaskInput } from '@/types/task'

const TITLE_MAX_LENGTH = 100
const DESCRIPTION_MAX_LENGTH = 1000

defineProps<{
  serverError: string | null
}>()

const emit = defineEmits<{
  create: [input: CreateTaskInput]
}>()

const title = ref('')
const description = ref('')
const validationError = ref<string | null>(null)

function handleSubmit() {
  const trimmedTitle = title.value.trim()

  if (!trimmedTitle) {
    validationError.value = 'Title is required'
    return
  }
  if (trimmedTitle.length > TITLE_MAX_LENGTH) {
    validationError.value = `Title cannot exceed ${TITLE_MAX_LENGTH} characters`
    return
  }
  if (description.value.length > DESCRIPTION_MAX_LENGTH) {
    validationError.value = `Description cannot exceed ${DESCRIPTION_MAX_LENGTH} characters`
    return
  }

  validationError.value = null
  emit('create', {
    title: trimmedTitle,
    description: description.value.trim() || null,
  })
}

function reset() {
  title.value = ''
  description.value = ''
  validationError.value = null
}

defineExpose({ reset })
</script>

<template>
  <form class="task-form" @submit.prevent="handleSubmit">
    <div class="task-form__field">
      <label for="task-title">Title</label>
      <input
        id="task-title"
        v-model="title"
        type="text"
        placeholder="What needs doing?"
        :maxlength="TITLE_MAX_LENGTH"
      />
    </div>
    <div class="task-form__field">
      <label for="task-description">Description (optional)</label>
      <textarea
        id="task-description"
        v-model="description"
        rows="2"
        :maxlength="DESCRIPTION_MAX_LENGTH"
      ></textarea>
    </div>
    <p v-if="validationError" class="task-form__error" role="alert">{{ validationError }}</p>
    <p v-else-if="serverError" class="task-form__error" role="alert">{{ serverError }}</p>
    <button type="submit">Add task</button>
  </form>
</template>

<style scoped>
.task-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.task-form__field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

label {
  font-size: 0.85rem;
  color: #444;
}

input,
textarea {
  font: inherit;
  padding: 0.4rem 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.task-form__error {
  color: #b00020;
  margin: 0;
}

button {
  align-self: flex-start;
  padding: 0.4rem 1rem;
  cursor: pointer;
}
</style>
