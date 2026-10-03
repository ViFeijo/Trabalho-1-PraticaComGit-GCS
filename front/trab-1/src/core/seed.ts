import type { AppState, Categoria, Departamento, Funcionario } from './models.ts'

export const departamentos: Departamento[] = [
  { id: 1, nome: 'Administrativo' }, { id: 2, nome: 'Financeiro' },
  { id: 3, nome: 'Recursos Humanos' }, { id: 4, nome: 'Tecnologia' },
]
export const categorias: Categoria[] = [
  { id: 1, nome: 'Material de escritório' }, { id: 2, nome: 'Transporte' },
  { id: 3, nome: 'Alimentação' }, { id: 4, nome: 'Tecnologia' }, { id: 5, nome: 'Serviços' },
]
export function createInitialState(): AppState {
  const funcionarios: Funcionario[] = [
    { id: 1, nome: 'Ana Souza', cargo: 'Analista Administrativo', departamento: departamentos[0] },
    { id: 2, nome: 'Bruno Lima', cargo: 'Analista Financeiro', departamento: departamentos[1] },
    { id: 3, nome: 'Carla Mendes', cargo: 'Analista de RH', departamento: departamentos[2] },
    { id: 4, nome: 'Diego Alves', cargo: 'Desenvolvedor', departamento: departamentos[3] },
    { id: 5, nome: 'Fernanda Costa', cargo: 'Coordenadora Financeira', departamento: departamentos[1] },
  ]
  return { funcionarios, operadorId: null, custos: [
    { id: 1, valor: 350, descricao: 'Compra de material de escritório', data: '2026-09-10', categoria: categorias[0], departamento: departamentos[0], funcionario: funcionarios[0] },
    { id: 2, valor: 180.5, descricao: 'Deslocamento para reunião', data: '2026-09-12', categoria: categorias[1], departamento: departamentos[1], funcionario: funcionarios[1] },
    { id: 3, valor: 95, descricao: 'Almoço de reunião', data: '2026-09-15', categoria: categorias[2], departamento: departamentos[2], funcionario: funcionarios[2] },
    { id: 4, valor: 1250, descricao: 'Compra de equipamentos de informática', data: '2026-09-18', categoria: categorias[3], departamento: departamentos[3], funcionario: funcionarios[3] },
    { id: 5, valor: 420, descricao: 'Serviço de manutenção', data: '2026-09-20', categoria: categorias[4], departamento: departamentos[1], funcionario: funcionarios[4] },
    { id: 6, valor: 275.75, descricao: 'Material para treinamento', data: '2026-09-22', categoria: categorias[0], departamento: departamentos[2], funcionario: funcionarios[2] },
  ] }
}
