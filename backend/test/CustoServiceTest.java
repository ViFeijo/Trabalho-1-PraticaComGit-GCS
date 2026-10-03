import java.util.List;

public class CustoServiceTest {

    public static void main(String[] args) {
        CustoService service = new CustoService();
        List<Departamento> deps = DadosIniciais.criarDepartamentos();
        List<Funcionario> funcs = DadosIniciais.criarFuncionarios();
        List<Categoria> cats = DadosIniciais.criarCategorias();

        // 1. Verifica estado inicial com dados de seed (6 custos)
        List<Custo> custos = service.getCustos();
        verificar(custos.size() == 6, "Deve iniciar com 6 custos do seed.");

        // O mais recente inicial é o custo 6 (data 2026-09-22)
        Custo maisRecente = service.obterCustoMaisRecente();
        verificar(maisRecente != null && maisRecente.getId() == 6,
                "O custo mais recente inicial deve ser o de ID 6.");
        verificar(custos.get(0).getId() == 6,
                "O primeiro da lista ordenada deve ser o mesmo de obterCustoMaisRecente.");

        // 2. Tentar excluir um custo que não é o mais recente deve falhar
        esperarErro(() -> service.excluirCusto(1),
                "Excluir custo que não seja o mais recente deve lançar IllegalArgumentException.");

        // 3. Cadastrar custo retroativo com data anterior (ex: 2026-08-15)
        // Isso testa a resolução do item 3.2: o recém-inserido não deve ser considerado
        // o mais recente se sua data for cronologicamente mais antiga!
        Funcionario func1 = funcs.get(0); // Ana Souza, dep 1
        Departamento dep1 = deps.get(0);  // Administrativo, id 1
        Categoria cat1 = cats.get(0);

        Custo retroativo = service.registrarCusto(99.0, "Custo retroativo de Agosto", "2026-08-15", cat1, dep1, func1);
        verificar(retroativo.getId() == 7, "Novo custo deve receber ID 7.");

        // O mais recente ainda deve ser o ID 6 (2026-09-24), e NÃO o retroativo (2026-08-15)
        Custo maisRecenteAposRetroativo = service.obterCustoMaisRecente();
        verificar(maisRecenteAposRetroativo.getId() == 6,
                "Mesmo inserido depois, o custo retroativo de agosto não pode ser o mais recente.");

        // Tentar excluir o retroativo (ID 7) deve falhar, pois o ID 6 é o mais recente
        esperarErro(() -> service.excluirCusto(7),
                "Não deve permitir excluir custo retroativo enquanto houver outro com data mais recente.");

        // Excluir o ID 6 (o mais recente) deve ter sucesso!
        boolean excluiu6 = service.excluirCusto(6);
        verificar(excluiu6, "Exclusão do custo ID 6 deve ter sucesso.");

        // 4. Cadastrar custo com data mais recente (ex: 2026-10-01)
        Custo novoMaisRecente = service.registrarCusto(150.0, "Custo de Outubro", "2026-10-01", cat1, dep1, func1);
        verificar(service.obterCustoMaisRecente().getId() == novoMaisRecente.getId(),
                "Novo custo de outubro deve passar a ser o mais recente.");

        boolean excluiuNovo = service.excluirCusto(novoMaisRecente.getId());
        verificar(excluiuNovo, "Exclusão do novo mais recente deve ter sucesso.");

        // 5. Testar busca / pesquisa
        List<Custo> buscaDesc = service.pesquisarPorDescricao("retroativo");
        verificar(buscaDesc.size() == 1 && buscaDesc.get(0).getId() == 7,
                "Pesquisa por descrição deve encontrar o custo retroativo.");

        System.out.println("CustoServiceTest: OK");
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void esperarErro(Runnable acao, String mensagem) {
        try {
            acao.run();
            throw new AssertionError(mensagem);
        } catch (IllegalArgumentException esperado) {
            // Sucesso
        }
    }
}
