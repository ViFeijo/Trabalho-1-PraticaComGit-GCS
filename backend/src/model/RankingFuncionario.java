public class RankingFuncionario {

    private Funcionario funcionario;
    private double total;

    public RankingFuncionario(Funcionario funcionario, double total) {
        this.funcionario = funcionario;
        this.total = total;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public double getTotal() {
        return total;
    }
}
