export const money = (value: number) => value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
export const normalize = (text: string) => text.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR')
export const initials = (name: string) => name.trim().split(/\s+/).filter((_, i, parts) => i === 0 || i === parts.length - 1).map(part => part[0]).join('')
export const today = () => {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
export const currentMonth = () => today().slice(0, 7)
export const displayDate = (date: string) => date.split('-').reverse().join('/')
export const cents = (value: number) => Math.round(value * 100)
export const sumCosts = (items: { valor: number }[]) => items.reduce((total, item) => total + cents(item.valor), 0) / 100
export const validMonth = (value: string) => /^(?!0000)\d{4}-(0[1-9]|1[0-2])$/.test(value)
export const validDate = (value: string) => /^(?!0000)\d{4}-\d{2}-\d{2}$/.test(value) && !Number.isNaN(Date.parse(value)) && new Date(value).toISOString().slice(0, 10) === value
