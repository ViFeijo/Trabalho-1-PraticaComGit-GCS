import java.util.ArrayList;
import java.util.List;

public class ResumoDashboard {

    private double totalMesAtual;
    private List<TotaisMes> meses;
    private List<RankingFuncionario> ranking;

    public ResumoDashboard(double totalMesAtual, List<TotaisMes> meses, List<RankingFuncionario> ranking) {
        this.totalMesAtual = totalMesAtual;
        this.meses = new ArrayList<>(meses);
        this.ranking = ranking != null ? new ArrayList<>(ranking) : new ArrayList<>();
    }

    public ResumoDashboard(double totalMesAtual, List<TotaisMes> meses) {
        this(totalMesAtual, meses, new ArrayList<>());
    }

    public double getTotalMesAtual() {
        return totalMesAtual;
    }

    public List<TotaisMes> getMeses() {
        return new ArrayList<>(meses);
    }

    public List<RankingFuncionario> getRanking() {
        return new ArrayList<>(ranking);
    }
}
