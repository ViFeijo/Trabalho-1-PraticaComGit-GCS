import { createContext, useContext } from 'react'
import type { AppModule } from './modules.ts'
export const ModuleContext = createContext<AppModule[]>([])
export const useModules = () => useContext(ModuleContext)
