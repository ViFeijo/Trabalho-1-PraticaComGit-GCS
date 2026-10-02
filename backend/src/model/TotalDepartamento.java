public class TotalDepartamento {

    private int departamentoId;
    private String departamentoNome;
    private double total;

    public TotalDepartamento(int departamentoId, String departamentoNome, double total) {
        this.departamentoId = departamentoId;
        this.departamentoNome = departamentoNome;
        this.total = total;
    }

    public int getDepartamentoId() {
        return departamentoId;
    }

    public String getDepartamentoNome() {
        return departamentoNome;
    }

    public double getTotal() {
        return total;
    }
}
