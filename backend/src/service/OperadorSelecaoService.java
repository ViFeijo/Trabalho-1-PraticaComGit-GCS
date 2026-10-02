public class OperadorSelecaoService {

    private final FuncionarioService funcionarioService;
    private Funcionario operadorAtual;

    public OperadorSelecaoService(FuncionarioService funcionarioService) {
        if (funcionarioService == null) {
            throw new IllegalArgumentException("O serviço de funcionários é obrigatório.");
        }
        this.funcionarioService = funcionarioService;
    }

    /**
     * Seleciona um funcionário como operador atual.
     *
     * @param funcionarioId identificador do funcionário; null limpa a seleção
     * @return funcionário selecionado ou null quando a seleção foi limpa
     */
    public Funcionario selecionar(Integer funcionarioId) {
        if (funcionarioId == null) {
            return limpar();
        }

        Funcionario funcionario = funcionarioService.buscarPorMatricula(funcionarioId);
        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado: " + funcionarioId + ".");
        }

        operadorAtual = funcionario;
        return operadorAtual;
    }

    /**
     * Remove o operador atual.
     *
     * @return null, pois não há operador após a limpeza
     */
    public Funcionario limpar() {
        operadorAtual = null;
        return operadorAtual;
    }

    public Funcionario getOperadorAtual() {
        return operadorAtual;
    }

    public Integer getOperadorAtualId() {
        return operadorAtual == null ? null : operadorAtual.getId();
    }

    public boolean temOperadorSelecionado() {
        return operadorAtual != null;
    }
}
