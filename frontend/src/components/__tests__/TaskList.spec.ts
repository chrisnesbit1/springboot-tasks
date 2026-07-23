import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import TaskList from '@/components/TaskList.vue'
import type { Task } from '@/types/task'

const sampleTask: Task = {
  id: '1',
  title: 'Write demo notes',
  description: 'Prep for review',
  status: 'OPEN',
  createdDate: '2026-07-23T00:00:00Z',
  completedDate: null,
}

describe('TaskList', () => {
  it('shows a loading indicator while loading', () => {
    const wrapper = mount(TaskList, { props: { tasks: [], loading: true, error: null } })
    expect(wrapper.text()).toContain('Loading tasks')
  })

  it('shows an empty-state message when there are no tasks', () => {
    const wrapper = mount(TaskList, { props: { tasks: [], loading: false, error: null } })
    expect(wrapper.text()).toContain('No tasks yet')
  })

  it('shows the error message when loading failed', () => {
    const wrapper = mount(TaskList, {
      props: { tasks: [], loading: false, error: 'Unable to reach the server.' },
    })
    expect(wrapper.text()).toContain('Unable to reach the server.')
  })

  it('renders a task and bubbles toggle/delete events', async () => {
    const wrapper = mount(TaskList, { props: { tasks: [sampleTask], loading: false, error: null } })
    expect(wrapper.text()).toContain('Write demo notes')

    await wrapper.find('input[type=checkbox]').setValue(true)
    expect(wrapper.emitted('toggle')?.[0]).toEqual([sampleTask])

    await wrapper.find('.task-item__delete').trigger('click')
    expect(wrapper.emitted('delete')?.[0]).toEqual([sampleTask])
  })
})
