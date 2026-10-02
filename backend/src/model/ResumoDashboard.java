import java.util.ArrayList;
import java.util.List;

public class ResumoDashboard {

    private double totalMesAtual;
    private List<TotaisMes> meses;

    public ResumoDashboard(double totalMesAtual, List<TotaisMes> meses) {
        this.totalMesAtual = totalMesAtual;
        this.meses = new ArrayList<>(meses);
    }

    public double getTotalMesAtual() {
        return totalMesAtual;
    }

    public List<TotaisMes> getMeses() {
        return new ArrayList<>(meses);
    }
}
