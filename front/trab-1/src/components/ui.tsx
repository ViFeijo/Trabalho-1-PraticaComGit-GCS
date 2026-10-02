import type { ReactNode } from 'react'

export function PageHeading({ title, action }: { title: string; action?: ReactNode }) {
  return <div className="page-heading"><h1>{title}</h1>{action}</div>
}

export function Stat({ label, value }: { label: string; value: string | number }) {
  return <div className="stat"><span>{label}: </span><strong>{value}</strong></div>
}

export function Person({ name, detail }: { name: string; detail?: string }) {
  return <div className="person"><strong>{name}</strong>{detail && <small>{detail}</small>}</div>
}

export function Empty({ title }: { title: string }) {
  return <div className="empty"><p>{title}</p></div>
}

export function Notice({ message, error = false }: { message: string; error?: boolean }) {
  return <div role={error ? 'alert' : 'status'} className={message ? 'notice' : undefined}>
    {message && error && <strong>Erro: </strong>}{message}
  </div>
}

export function Field({ label, id, children }: { label: string; id: string; children: ReactNode }) {
  return <div className="field"><label htmlFor={id}>{label}</label>{children}</div>
}
