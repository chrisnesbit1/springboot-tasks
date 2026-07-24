import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import TaskItem from '@/components/TaskItem.vue'
import type { Task } from '@/types/task'

const baseTask: Task = {
  id: '1',
  title: 'Write demo notes',
  description: 'Prep for review',
  status: 'OPEN',
  createdDate: '2026-07-23T00:00:00Z',
  completedDate: null,
}

describe('TaskItem', () => {
  it('renders a status dropdown with the current status selected', () => {
    const wrapper = mount(TaskItem, { props: { task: baseTask } })
    const select = wrapper.get('select')
    expect((select.element as HTMLSelectElement).value).toBe('OPEN')
    const optionValues = select.findAll('option').map((option) => option.element.value)
    expect(optionValues).toEqual(['OPEN', 'IN_PROGRESS', 'COMPLETED'])
  })

  it('emits status-change with the task and new status when the dropdown changes', async () => {
    const wrapper = mount(TaskItem, { props: { task: baseTask } })
    await wrapper.get('select').setValue('IN_PROGRESS')
    expect(wrapper.emitted('status-change')?.[0]).toEqual([baseTask, 'IN_PROGRESS'])
  })

  it('shows an in-progress pill and accent styling only when status is IN_PROGRESS', () => {
    const inProgressTask: Task = { ...baseTask, status: 'IN_PROGRESS' }
    const wrapper = mount(TaskItem, { props: { task: inProgressTask } })
    expect(wrapper.find('.task-item__badge').exists()).toBe(true)
    expect(wrapper.find('.task-item__badge').text()).toBe('In Progress')
    expect(wrapper.classes()).toContain('task-item--in-progress')
  })

  it('does not show the in-progress pill or accent for an open task', () => {
    const wrapper = mount(TaskItem, { props: { task: baseTask } })
    expect(wrapper.find('.task-item__badge').exists()).toBe(false)
    expect(wrapper.classes()).not.toContain('task-item--in-progress')
  })

  it('keeps completed tasks rendered with only the existing strikethrough treatment', () => {
    const completedTask: Task = { ...baseTask, status: 'COMPLETED' }
    const wrapper = mount(TaskItem, { props: { task: completedTask } })
    expect(wrapper.find('.task-item__title--done').exists()).toBe(true)
    expect(wrapper.find('.task-item__badge').exists()).toBe(false)
    expect(wrapper.classes()).not.toContain('task-item--in-progress')
  })

  it('emits delete when the delete button is clicked', async () => {
    const wrapper = mount(TaskItem, { props: { task: baseTask } })
    await wrapper.get('.task-item__delete').trigger('click')
    expect(wrapper.emitted('delete')?.[0]).toEqual([baseTask])
  })
})
