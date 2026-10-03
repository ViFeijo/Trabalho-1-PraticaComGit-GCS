import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Limites por departamento/mês, mantidos apenas na instância deste serviço. */
public final class BudgetLimitService {
    private final Map<Integer, Map<YearMonth, BigDecimal>> limites = new HashMap<>();

    public void configurarLimite(Departamento departamento, YearMonth mes, BigDecimal limite) {
        validarReferencia(departamento, mes);
        Objects.requireNonNull(limite, "Informe o limite.");
        if (limite.signum() <= 0) throw new IllegalArgumentException("O limite deve ser positivo.");
        BigDecimal normalizado;
        try { normalizado = limite.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException exception) { throw new IllegalArgumentException("Use até duas casas decimais.", exception); }
        limites.computeIfAbsent(departamento.getId(), id -> new HashMap<>()).put(mes, normalizado);
    }

    public BudgetConsumption consultar(Departamento departamento, YearMonth mes, List<Custo> custos) {
        validarReferencia(departamento, mes);
        Objects.requireNonNull(custos, "Informe os custos.");
        BigDecimal consumo = BigDecimal.ZERO.setScale(2);
        for (Custo custo : custos) {
            Objects.requireNonNull(custo, "Custo inválido.");
            if (custo.getDepartamento() == null) throw new IllegalArgumentException("Custo sem departamento.");
            if (custo.getDepartamento().getId() == departamento.getId()
                    && YearMonth.from(LocalDate.parse(custo.getData())).equals(mes)) {
                if (!Double.isFinite(custo.getValor()) || custo.getValor() < 0) throw new IllegalArgumentException("Valor de custo inválido.");
                consumo = consumo.add(BigDecimal.valueOf(custo.getValor()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        BigDecimal limite = limites.getOrDefault(departamento.getId(), Map.of()).get(mes);
        return new BudgetConsumption(limite, consumo);
    }

    private void validarReferencia(Departamento departamento, YearMonth mes) {
        Objects.requireNonNull(departamento, "Informe o departamento.");
        Objects.requireNonNull(mes, "Informe o mês.");
        if (departamento.getId() <= 0 || mes.getYear() < 1 || mes.getYear() > 9999) throw new IllegalArgumentException("Departamento ou mês inválido.");
    }

    public static final class BudgetConsumption {
        private final BigDecimal limite;
        private final BigDecimal consumo;
        private BudgetConsumption(BigDecimal limite, BigDecimal consumo) { this.limite = limite; this.consumo = consumo; }
        public Optional<BigDecimal> getLimite() { return Optional.ofNullable(limite); }
        public BigDecimal getConsumo() { return consumo; }
        public Optional<BigDecimal> getSaldo() { return getLimite().map(value -> value.subtract(consumo)); }
        public Optional<BigDecimal> getPercentual() { return getLimite().map(value -> consumo.multiply(BigDecimal.valueOf(100)).divide(value, 2, RoundingMode.HALF_UP)); }
        public boolean isExcedido() { return limite != null && consumo.compareTo(limite) > 0; }
    }
}
