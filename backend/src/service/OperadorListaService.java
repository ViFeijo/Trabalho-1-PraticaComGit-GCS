import java.util.ArrayList;
import java.util.List;

public class OperadorListaService {

    private final FuncionarioService funcionarioService;

    public OperadorListaService(FuncionarioService funcionarioService) {
        if (funcionarioService == null) {
            throw new IllegalArgumentException("O serviço de funcionários é obrigatório.");
        }
        this.funcionarioService = funcionarioService;
    }

    /**
     * Lista os funcionários que podem ser escolhidos como operador.
     *
     * @return cópia independente da lista ordenada por nome
     */
    public List<Funcionario> listarOperadores() {
        return new ArrayList<>(funcionarioService.listarTodos());
    }
}
