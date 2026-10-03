import java.util.List;

public class OperadorListaServiceTest {

    public static void main(String[] args) {
        FuncionarioService funcionarioService = new FuncionarioService(
                DadosIniciais.criarFuncionarios(), DadosIniciais.criarDepartamentos());
        OperadorListaService operadorListaService = new OperadorListaService(funcionarioService);

        List<Funcionario> operadores = operadorListaService.listarOperadores();
        verificar(operadores.size() == 5, "A lista inicial deve conter cinco funcionários.");
        verificar(operadores.get(0).getNome().equals("Ana Souza"),
                "A lista deve manter a ordenação por nome.");
        verificar(operadores.get(4).getNome().equals("Fernanda Costa"),
                "A lista deve conter o último nome em ordem alfabética.");

        operadores.clear();
        verificar(operadorListaService.listarOperadores().size() == 5,
                "Alterar a lista retornada não pode alterar a fonte de dados.");

        funcionarioService.cadastrar(6, "Gustavo Reis", "Analista", 1);
        verificar(operadorListaService.listarOperadores().size() == 6,
                "A listagem deve refletir funcionários cadastrados posteriormente.");

        esperarErro(() -> new OperadorListaService(null),
                "O serviço de funcionários nulo deve ser rejeitado.");
        System.out.println("OperadorListaServiceTest: OK");
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
