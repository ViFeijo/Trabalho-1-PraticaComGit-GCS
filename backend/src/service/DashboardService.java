import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DashboardService {

    public ResumoDashboard calcular(List<Custo> custos,
                                    List<Departamento> departamentos,
                                    LocalDate referencia) {
        if (custos == null) {
            throw new IllegalArgumentException("A lista de custos é obrigatória.");
        }
        if (departamentos == null) {
            throw new IllegalArgumentException("A lista de departamentos é obrigatória.");
        }
        if (referencia == null) {
            throw new IllegalArgumentException("A data de referência é obrigatória.");
        }

        List<Departamento> ordenados = new ArrayList<>();
        for (Departamento departamento : departamentos) {
            if (departamento != null) {
                ordenados.add(departamento);
            }
        }
        ordenados.sort(Comparator.comparingInt(Departamento::getId));

        Set<Integer> ids = new HashSet<>();
        for (Departamento departamento : ordenados) {
            ids.add(departamento.getId());
        }

        Map<YearMonth, Double> totalPorMes = new HashMap<>();
        Map<YearMonth, Map<Integer, Double>> totalPorMesEDepartamento = new HashMap<>();

        for (Custo custo : custos) {
            if (custo == null) {
                continue;
            }
            YearMonth mes = interpretarMes(custo.getData());
            if (mes == null) {
                continue;
            }

            totalPorMes.merge(mes, custo.getValor(), Double::sum);

            Departamento departamento = custo.getDepartamento();
            if (departamento == null || !ids.contains(departamento.getId())) {
                continue;
            }
            totalPorMesEDepartamento
                    .computeIfAbsent(mes, chave -> new HashMap<>())
                    .merge(departamento.getId(), custo.getValor(), Double::sum);
        }

        YearMonth mesAtual = YearMonth.from(referencia);
        double totalMesAtual = totalPorMes.getOrDefault(mesAtual, 0.0);

        List<TotaisMes> meses = new ArrayList<>();
        for (int deslocamento = 2; deslocamento >= 0; deslocamento--) {
            YearMonth mes = mesAtual.minusMonths(deslocamento);
            Map<Integer, Double> porDepartamento =
                    totalPorMesEDepartamento.getOrDefault(mes, new HashMap<>());

            List<TotalDepartamento> totais = new ArrayList<>();
            for (Departamento departamento : ordenados) {
                double total = porDepartamento.getOrDefault(departamento.getId(), 0.0);
                totais.add(new TotalDepartamento(
                        departamento.getId(), departamento.getNome(), total));
            }
            meses.add(new TotaisMes(mes.getYear(), mes.getMonthValue(), totais));
        }

        List<RankingFuncionario> ranking = calcularRanking(custos);

        return new ResumoDashboard(totalMesAtual, meses, ranking);
    }

    public List<RankingFuncionario> calcularRanking(List<Custo> custos) {
        if (custos == null) {
            throw new IllegalArgumentException("A lista de custos é obrigatória.");
        }

        Map<Integer, Funcionario> funcionariosPorId = new HashMap<>();
        Map<Integer, Double> totalPorFuncionario = new HashMap<>();

        for (Custo c : custos) {
            if (c == null || c.getFuncionario() == null) {
                continue;
            }
            Funcionario f = c.getFuncionario();
            funcionariosPorId.putIfAbsent(f.getId(), f);
            totalPorFuncionario.merge(f.getId(), c.getValor(), Double::sum);
        }

        List<RankingFuncionario> ranking = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : totalPorFuncionario.entrySet()) {
            ranking.add(new RankingFuncionario(funcionariosPorId.get(entry.getKey()), entry.getValue()));
        }

        ranking.sort((r1, r2) -> {
            int comp = Double.compare(r2.getTotal(), r1.getTotal());
            if (comp != 0) {
                return comp;
            }
            return Integer.compare(r1.getFuncionario().getId(), r2.getFuncionario().getId());
        });

        if (ranking.size() > 3) {
            return new ArrayList<>(ranking.subList(0, 3));
        }
        return ranking;
    }

    private YearMonth interpretarMes(String data) {
        if (data == null) {
            return null;
        }
        try {
            return YearMonth.from(LocalDate.parse(data));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
