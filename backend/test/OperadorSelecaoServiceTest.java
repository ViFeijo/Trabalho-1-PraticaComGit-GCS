public class OperadorSelecaoServiceTest {

    public static void main(String[] args) {
        FuncionarioService funcionarioService = new FuncionarioService(
                DadosIniciais.criarFuncionarios(), DadosIniciais.criarDepartamentos());
        OperadorSelecaoService operadorSelecaoService =
                new OperadorSelecaoService(funcionarioService);

        verificar(operadorSelecaoService.getOperadorAtual() == null,
                "O serviço deve começar sem operador selecionado.");
        verificar(operadorSelecaoService.getOperadorAtualId() == null,
                "O ID inicial do operador deve ser null.");
        verificar(!operadorSelecaoService.temOperadorSelecionado(),
                "O serviço inicialmente não deve indicar operador selecionado.");

        Funcionario primeiro = operadorSelecaoService.selecionar(1);
        verificar(primeiro.getNome().equals("Ana Souza"),
                "A seleção deve retornar o funcionário escolhido.");
        verificar(operadorSelecaoService.getOperadorAtualId() == 1,
                "A seleção deve manter o ID do operador atual.");
        verificar(operadorSelecaoService.temOperadorSelecionado(),
                "A seleção deve indicar que existe operador atual.");

        Funcionario segundo = operadorSelecaoService.selecionar(4);
        verificar(segundo.getNome().equals("Diego Alves"),
                "Uma nova seleção deve trocar o operador atual.");
        verificar(operadorSelecaoService.getOperadorAtualId() == 4,
                "A troca deve atualizar o ID do operador atual.");

        esperarErro(() -> operadorSelecaoService.selecionar(999),
                "Um funcionário inexistente deve ser rejeitado.");
        verificar(operadorSelecaoService.getOperadorAtualId() == 4,
                "Uma seleção inválida não deve apagar a seleção anterior.");

        verificar(operadorSelecaoService.selecionar(null) == null,
                "Selecionar null deve limpar o operador atual.");
        verificar(!operadorSelecaoService.temOperadorSelecionado(),
                "Após selecionar null não deve existir operador atual.");

        operadorSelecaoService.selecionar(2);
        operadorSelecaoService.limpar();
        verificar(operadorSelecaoService.getOperadorAtual() == null,
                "limpar deve remover o operador atual.");

        esperarErro(() -> new OperadorSelecaoService(null),
                "O serviço de funcionários nulo deve ser rejeitado.");
        System.out.println("OperadorSelecaoServiceTest: OK");
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void esperarErro(Runnable acao, String mensagem) {
        try {
            acao.run();
            throw new AssertionError(mensagem);
        } catch (IllegalArgumentException esperado) {
            // Comportamento esperado.
        }
    }
}
