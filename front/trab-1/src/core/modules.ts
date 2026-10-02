import type { ComponentType } from 'react'
import type { Custo } from './models.ts'

export interface CostActionProps { custos: Custo[] }
export interface AppModule {
  id: string
  label: string
  order: number
  Page?: ComponentType
  Header?: ComponentType
  CostAction?: ComponentType<CostActionProps>
}
