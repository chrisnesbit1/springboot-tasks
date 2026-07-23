import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import TaskForm from '@/components/TaskForm.vue'

describe('TaskForm', () => {
  it('shows a validation error and does not emit create when the title is blank', async () => {
    const wrapper = mount(TaskForm, { props: { serverError: null } })

    await wrapper.find('form').trigger('submit')

    expect(wrapper.text()).toContain('Title is required')
    expect(wrapper.emitted('create')).toBeUndefined()
  })

  it('emits create with trimmed values when valid', async () => {
    const wrapper = mount(TaskForm, { props: { serverError: null } })

    await wrapper.find('#task-title').setValue('  Write demo notes  ')
    await wrapper.find('#task-description').setValue('  Prep for review  ')
    await wrapper.find('form').trigger('submit')

    expect(wrapper.emitted('create')?.[0]).toEqual([
      { title: 'Write demo notes', description: 'Prep for review' },
    ])
  })

  it('displays a server-side error passed in via props', () => {
    const wrapper = mount(TaskForm, { props: { serverError: 'Task not found' } })
    expect(wrapper.text()).toContain('Task not found')
  })
})
