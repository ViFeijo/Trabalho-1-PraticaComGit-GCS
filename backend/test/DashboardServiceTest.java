import java.time.LocalDate;
import java.util.List;

public class DashboardServiceTest {

    public static void main(String[] args) {
        DashboardService service = new DashboardService();
        List<Departamento> departamentos = DadosIniciais.criarDepartamentos();
        List<Custo> custos = DadosIniciais.criarCustos();
        LocalDate referencia = LocalDate.of(2026, 9, 30);

        ResumoDashboard resumo = service.calcular(custos, departamentos, referencia);

        // Verifica total do mês atual (Setembro/2026: 2571.25)
        verificar(Math.abs(resumo.getTotalMesAtual() - 2571.25) < 0.001,
                "Total do mês atual deve ser 2571.25");

        // Verifica que meses tem 3 períodos (offsets 2, 1, 0: Julho, Agosto, Setembro)
        List<TotaisMes> meses = resumo.getMeses();
        verificar(meses.size() == 3, "Devem ser calculados exatamente 3 meses no painel.");
        verificar(meses.get(0).getAno() == 2026 && meses.get(0).getMes() == 7, "Primeiro mês deve ser Julho/2026");
        verificar(meses.get(1).getAno() == 2026 && meses.get(1).getMes() == 8, "Segundo mês deve ser Agosto/2026");
        verificar(meses.get(2).getAno() == 2026 && meses.get(2).getMes() == 9, "Terceiro mês deve ser Setembro/2026");

        // Verifica valores por departamento no mês de referência (índice 2 = Setembro)
        TotaisMes setembro = meses.get(2);
        TotalDepartamento depTec = setembro.getTotais().get(3); // TI (id 4)
        verificar(Math.abs(depTec.getTotal() - 1250.00) < 0.001,
                "Total do departamento de TI em Setembro deve ser 1250.00");

        // Verifica ranking dos Top 3 funcionários com maior soma de custos
        List<RankingFuncionario> ranking = resumo.getRanking();
        verificar(ranking.size() == 3, "O ranking deve retornar os 3 primeiros colocados.");
        verificar(ranking.get(0).getFuncionario().getId() == 4, "1º lugar deve ser o funcionário 4 (Diego Alves).");
        verificar(Math.abs(ranking.get(0).getTotal() - 1250.00) < 0.001, "Total do 1º lugar deve ser 1250.00.");
        verificar(ranking.get(1).getFuncionario().getId() == 5, "2º lugar deve ser a funcionária 5 (Fernanda Costa).");
        verificar(Math.abs(ranking.get(1).getTotal() - 420.00) < 0.001, "Total do 2º lugar deve ser 420.00.");
        verificar(ranking.get(2).getFuncionario().getId() == 3, "3º lugar deve ser a funcionária 3 (Carla Mendes).");
        verificar(Math.abs(ranking.get(2).getTotal() - 370.75) < 0.001, "Total do 3º lugar deve ser 370.75.");

        // Verifica lista vazia de custos
        ResumoDashboard vazio = service.calcular(List.of(), departamentos, referencia);
        verificar(vazio.getTotalMesAtual() == 0.0, "Total com custos vazios deve ser 0.0");
        verificar(vazio.getRanking().isEmpty(), "Ranking com custos vazios deve ser vazio.");

        // Validações de parâmetros nulos
        esperarErro(() -> service.calcular(null, departamentos, referencia), "Custos nulo deve lançar exceção.");
        esperarErro(() -> service.calcular(custos, null, referencia), "Departamentos nulo deve lançar exceção.");
        esperarErro(() -> service.calcular(custos, departamentos, null), "Referência nula deve lançar exceção.");

        System.out.println("DashboardServiceTest: OK");
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
