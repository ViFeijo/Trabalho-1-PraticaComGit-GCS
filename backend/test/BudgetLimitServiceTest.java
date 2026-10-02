import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class BudgetLimitServiceTest {
    private static void check(boolean ok) { if (!ok) throw new AssertionError(); }
    private static void rejects(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Esperava rejeição");
    }
    public static void main(String[] args) {
        BudgetLimitService service = new BudgetLimitService();
        Departamento tecnologia = DadosIniciais.criarDepartamentos().get(3);
        Departamento financeiro = DadosIniciais.criarDepartamentos().get(1);
        List<Custo> custos = DadosIniciais.criarCustos();
        YearMonth setembro = YearMonth.of(2026, 9);
        check(service.consultar(tecnologia, setembro, custos).getLimite().isEmpty());
        rejects(() -> service.configurarLimite(tecnologia, setembro, BigDecimal.ZERO));
        rejects(() -> service.configurarLimite(tecnologia, setembro, new BigDecimal("-1")));
        rejects(() -> service.configurarLimite(tecnologia, setembro, new BigDecimal("1.001")));
        service.configurarLimite(tecnologia, setembro, new BigDecimal("1000"));
        BudgetLimitService.BudgetConsumption result = service.consultar(tecnologia, setembro, custos);
        check(result.getConsumo().compareTo(new BigDecimal("1250")) == 0);
        check(result.getSaldo().orElseThrow().compareTo(new BigDecimal("-250")) == 0);
        check(result.getPercentual().orElseThrow().compareTo(new BigDecimal("125")) == 0);
        check(result.isExcedido());
        check(service.consultar(tecnologia, setembro.plusMonths(1), custos).getLimite().isEmpty());
        check(service.consultar(financeiro, setembro, custos).getLimite().isEmpty());
        service.configurarLimite(tecnologia, setembro, new BigDecimal("1250"));
        check(!service.consultar(tecnologia, setembro, custos).isExcedido());
        check(service.consultar(tecnologia, setembro, List.of()).getConsumo().signum() == 0);
        check(new BudgetLimitService().consultar(tecnologia, setembro, custos).getLimite().isEmpty());
        check(custos.size() == 6);
        System.out.println("BudgetLimitService: OK");
    }
}
