import java.util.ArrayList;
import java.util.List;

public class CustoService {

    private final List<Custo> custos;
    private int proximoId;

    public CustoService() {
        this.custos = new ArrayList<>(DadosIniciais.criarCustos());
        this.proximoId = 1;
        for (Custo c : custos) {
            if (c.getId() >= proximoId) {
                proximoId = c.getId() + 1;
            }
        }
    }

    public Custo registrarCusto(double valor, String descricao, String data,
            Categoria categoria, Departamento departamento,
            Funcionario funcionario) {
        CustoValidator.validar(valor, descricao, data, categoria, departamento, funcionario);
        Custo custo = new Custo(proximoId, valor, descricao, data,
                categoria, departamento, funcionario);
        custos.add(custo);
        proximoId++;
        return custo;
    }

    public List<Custo> getCustos() {
        List<Custo> lista = new ArrayList<>(custos);
        ordenarPorMaisRecente(lista);
        return lista;
    }

    public static void ordenarPorMaisRecente(List<Custo> lista) {
        if (lista == null) {
            return;
        }
        lista.sort((c1, c2) -> {
            if (c1 == null && c2 == null)
                return 0;
            if (c1 == null)
                return 1;
            if (c2 == null)
                return -1;
            if (c1.getData() == null && c2.getData() == null) {
                return Integer.compare(c2.getId(), c1.getId());
            }
            if (c1.getData() == null)
                return 1;
            if (c2.getData() == null)
                return -1;
            int dataComparison = c2.getData().compareTo(c1.getData());
            if (dataComparison != 0) {
                return dataComparison;
            }
            return Integer.compare(c2.getId(), c1.getId());
        });
    }
}