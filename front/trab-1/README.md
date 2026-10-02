# GCS — interface modular

Base de funcionários e estrutura compartilhada em React + TypeScript + Vite.

## Executar

Dentro de `front/trab-1`:

```sh
npm ci
npm run dev
npm run build
npm run lint
node --test tests/*.test.ts
```

Os testes usam o suporte nativo a TypeScript do Node 22.18+ (validado com Node 25).

## Módulos

`src/App.tsx` descobre `src/modules/*/module.tsx`. Cada módulo declara `id`, `label`,
`order` e, opcionalmente, `Page`, `Header` ou `CostAction`. Não é necessário editar
App para instalar uma funcionalidade. A base inclui somente a página de funcionários;
instale os demais módulos copiando os arquivos de cada pacote após disponibilizar a base.

`src/core` contém os modelos compatíveis com a dev, dados de exemplo, formatação e
estado compartilhado. Regras de cada funcionalidade ficam em seu próprio serviço.
Não há API HTTP, autenticação ou persistência. Recarregar restaura os exemplos.

O backend atual usa `id`, `nome`, `cargo` e `departamento` para funcionário.
A matrícula mencionada no PDF não foi acrescentada ao modelo de outro integrante.
Os exemplos conservam as datas do seed (setembro de 2026); meses sem custos mostram zero.

Para navegar e demonstrar o sistema completo, aplique também os módulos dos outros pacotes.
A integração definitiva com Java substituirá os adaptadores mock; não existe rota HTTP pronta.

## Visual básico

Interface em Arial, fundo branco, texto preto e bordas/ícones verdes (#008000),
sem arredondamentos, subtítulos decorativos ou favicon. Os ícones vêm apenas de
`react-icons/fi`; não há arquivos SVG próprios. Rode `npm ci` após copiar o pacote
atualizado do integrante 8, que inclui package.json e package-lock.json.

Ao atualizar a versão anterior, exclua `public/favicon.svg`, `public/icons.svg`
e `src/index.css` (legado não utilizado), conforme MODIFICACOES.md.
