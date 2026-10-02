import java.util.ArrayList;
import java.util.List;

public class CustoService {

    private final List<Custo> custos;
    private int proximoId;

    public CustoService() {
        // Copia para um ArrayList, porque Arrays.asList não permite add()
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

    /**
     * Retorna o custo mais recente registrado no sistema.
     * Em ordem cronológica de inserção, o mais recente é o último elemento da lista.
     * @return Custo mais recente ou null se a lista estiver vazia
     */
    public Custo obterCustoMaisRecente() {
        if (custos.isEmpty()) {
            return null;
        }
        return custos.get(custos.size() - 1);
    }

    /**
     * Remove um custo pelo seu ID, permitindo apenas a exclusão do custo mais recente.
     * @param id identificador do custo a ser removido
     * @return true se o custo foi removido com sucesso
     * @throws IllegalStateException se não houver custos cadastrados
     * @throws IllegalArgumentException se o ID não corresponder ao custo mais recente
     */
    public boolean excluirCusto(int id) {
        if (custos.isEmpty()) {
            throw new IllegalStateException("Nenhum custo cadastrado para exclusão.");
        }

        Custo maisRecente = obterCustoMaisRecente();
        if (maisRecente.getId() != id) {
            throw new IllegalArgumentException("Somente o custo mais recente pode ser excluído.");
        }

        return custos.removeIf(c -> c.getId() == id);
    }
}