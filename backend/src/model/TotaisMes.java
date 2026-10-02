import java.util.ArrayList;
import java.util.List;

public class TotaisMes {

    private int ano;
    private int mes;
    private List<TotalDepartamento> totais;

    public TotaisMes(int ano, int mes, List<TotalDepartamento> totais) {
        this.ano = ano;
        this.mes = mes;
        this.totais = new ArrayList<>(totais);
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }

    public List<TotalDepartamento> getTotais() {
        return new ArrayList<>(totais);
    }
}
