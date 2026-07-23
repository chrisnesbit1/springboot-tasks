import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'

// Composables call onUnmounted, which requires an active component
// instance. This mounts a throwaway component so composables can be
// exercised (and unmounted) in isolation from any real UI.
export function withSetup<T>(composable: () => T): { result: T; unmount: () => void } {
  let result!: T
  const wrapper = mount(
    defineComponent({
      setup() {
        result = composable()
        return () => null
      },
    }),
  )
  return { result, unmount: () => wrapper.unmount() }
}
