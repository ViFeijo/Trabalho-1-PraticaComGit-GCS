import { useSyncExternalStore } from 'react'
import { createInitialState } from './seed.ts'

// Infraestrutura compartilhada; regras ficam nos serviços de cada funcionalidade.
export function createStore<T>(initialState: T) {
  let state = initialState
  const listeners = new Set<() => void>()
  return {
    getSnapshot: () => state,
    subscribe: (listener: () => void) => { listeners.add(listener); return () => { listeners.delete(listener) } },
    update: (updater: (current: T) => T) => {
      state = updater(state)
      listeners.forEach(listener => listener())
    },
  }
}
export const appStore = createStore(createInitialState())
export const useAppState = () => useSyncExternalStore(appStore.subscribe, appStore.getSnapshot)
