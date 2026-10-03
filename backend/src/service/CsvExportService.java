import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/** Serializa exatamente os registros recebidos, preservando filtros e ordenação do chamador. */
public final class CsvExportService {
    public byte[] exportar(List<Custo> custos) {
        Objects.requireNonNull(custos, "Informe os custos a exportar.");
        StringBuilder csv = new StringBuilder("\uFEFF");
        linha(csv, "ID", "Data", "Descrição", "Valor", "Categoria", "Departamento", "Funcionário");
        for (Custo custo : custos) {
            Objects.requireNonNull(custo, "Custo inválido.");
            if (!Double.isFinite(custo.getValor())) throw new IllegalArgumentException("Valor inválido.");
            String valor = BigDecimal.valueOf(custo.getValor()).setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
            linha(csv, Integer.toString(custo.getId()), custo.getData(), custo.getDescricao(), valor,
                    custo.getCategoria().getNome(), custo.getDepartamento().getNome(), custo.getFuncionario().getNome());
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void linha(StringBuilder csv, String... valores) {
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) csv.append(';');
            csv.append(celula(valores[i]));
        }
        csv.append("\r\n");
    }

    private String celula(String valor) {
        String texto = Objects.requireNonNull(valor, "Campo CSV inválido.");
        int index = 0;
        while (index < texto.length() && (Character.isWhitespace(texto.charAt(index)) || Character.isSpaceChar(texto.charAt(index)) || texto.charAt(index) <= 31 || texto.charAt(index) == '\uFEFF')) index++;
        if (index < texto.length() && "=+@-".indexOf(texto.charAt(index)) >= 0) texto = "'" + texto;
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
