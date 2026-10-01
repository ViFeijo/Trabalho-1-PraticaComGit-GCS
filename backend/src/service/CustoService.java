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
}