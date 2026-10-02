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
        if (buscarPorMatricula(matricula) != null) {
            throw new IllegalArgumentException("Já existe funcionário com a matrícula " + matricula + ".");
        }

        Departamento departamento = buscarDepartamento(departamentoId);
        if (departamento == null) {
            throw new IllegalArgumentException("Departamento inexistente: " + departamentoId);
        }

        Funcionario novo = new Funcionario(matricula, nome.trim(), cargo.trim(), departamento);
        funcionarios.add(novo);
        return novo;
    }

    private Departamento buscarDepartamento(int id) {
        for (Departamento d : departamentos) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public Funcionario buscarPorMatricula(int matricula) {
        for (Funcionario f : funcionarios) {
            if (f.getId() == matricula) {
                return f;
            }
        }
        return null;
    }
}