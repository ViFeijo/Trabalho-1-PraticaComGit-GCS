import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvExportServiceTest {
    private static void check(boolean ok) { if (!ok) throw new AssertionError(); }
    public static void main(String[] args) {
        CsvExportService service = new CsvExportService();
        List<Custo> custos = DadosIniciais.criarCustos();
        byte[] bytes = service.exportar(List.of(custos.get(4), custos.get(1)));
        check((bytes[0] & 255) == 239 && (bytes[1] & 255) == 187 && (bytes[2] & 255) == 191);
        String csv = new String(bytes, StandardCharsets.UTF_8);
        check(csv.contains("\"180,50\""));
        check(csv.indexOf("\"5\";") < csv.indexOf("\"2\";"));
        check(!csv.contains("Compra de equipamentos"));
        check(csv.split("\r\n").length == 3);
        Custo custo = custos.get(0);
        custo.setDescricao("A; \"B\"\nC");
        csv = new String(service.exportar(List.of(custo)), StandardCharsets.UTF_8);
        check(csv.contains("\"A; \"\"B\"\"\nC\""));
        for (String formula : List.of("=1+1", "+2", "-3", "@SUM(A1)", "  =1", "\t=1", "\u0001=1", "\u00a0=1")) {
            custo.setDescricao(formula);
            csv = new String(service.exportar(List.of(custo)), StandardCharsets.UTF_8);
            check(csv.contains("\"'" + formula + "\""));
        }
        custo.getFuncionario().setNome("=name");
        check(new String(service.exportar(List.of(custo)), StandardCharsets.UTF_8).contains("\"'=name\""));
        check(new String(service.exportar(List.of()), StandardCharsets.UTF_8).split("\r\n").length == 1);
        System.out.println("CsvExportService: OK");
    }
}
