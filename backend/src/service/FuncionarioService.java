import java.util.ArrayList;
import java.util.List;

public class FuncionarioService {

    private final List<Funcionario> funcionarios;
    private final List<Departamento> departamentos;

    public FuncionarioService(List<Funcionario> funcionariosIniciais,
                              List<Departamento> departamentos) {
        this.funcionarios = new ArrayList<>(funcionariosIniciais);
        this.departamentos = new ArrayList<>(departamentos);
    }

    public Funcionario cadastrar(int matricula, String nome, String cargo, int departamentoId) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("Matrícula deve ser um número positivo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }

        Departamento departamento = departamentos.stream()
            .filter(item -> item.getId() == departamentoId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Departamento não encontrado."));

        Funcionario novo = new Funcionario(matricula, nome.trim(), cargo.trim(), departamento);
        funcionarios.add(novo);
        return novo;
    }
}
