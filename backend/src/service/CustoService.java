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
        return new ArrayList<>(custos);
    }

    public List<Custo> pesquisarCustos(String descricao, Integer categoriaId, String data, Integer departamentoId) {
        List<Custo> resultado = new ArrayList<>();
        String descFiltro = (descricao != null) ? descricao.trim().toLowerCase() : null;
        String dataFiltro = (data != null) ? data.trim() : null;

        for (Custo c : custos) {
            if (descFiltro != null && !descFiltro.isEmpty()) {
                if (c.getDescricao() == null || !c.getDescricao().toLowerCase().contains(descFiltro)) {
                    continue;
                }
            }
            if (categoriaId != null) {
                if (c.getCategoria() == null || c.getCategoria().getId() != categoriaId) {
                    continue;
                }
            }
            if (dataFiltro != null && !dataFiltro.isEmpty()) {
                if (c.getData() == null || !c.getData().trim().equals(dataFiltro)) {
                    continue;
                }
            }
            if (departamentoId != null) {
                if (c.getDepartamento() == null || c.getDepartamento().getId() != departamentoId) {
                    continue;
                }
            }
            resultado.add(c);
        }
        return resultado;
    }

    public List<Custo> pesquisarCustos(String descricao, Categoria categoria, String data, Departamento departamento) {
        Integer catId = (categoria != null) ? categoria.getId() : null;
        Integer depId = (departamento != null) ? departamento.getId() : null;
        return pesquisarCustos(descricao, catId, data, depId);
    }

    public List<Custo> pesquisarPorDescricao(String descricao) {
        return pesquisarCustos(descricao, (Integer) null, null, null);
    }

    public List<Custo> pesquisarPorCategoria(Categoria categoria) {
        return pesquisarCustos(null, categoria, null, null);
    }

    public List<Custo> pesquisarPorData(String data) {
        return pesquisarCustos(null, (Integer) null, data, null);
    }

    public List<Custo> pesquisarPorDepartamento(Departamento departamento) {
        return pesquisarCustos(null, null, null, departamento);
    }
}