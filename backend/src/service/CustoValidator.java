import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CustoValidator {

    public static void validar(double valor, String descricao, String data,
                               Categoria categoria, Departamento departamento,
                               Funcionario funcionario) {

        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }

        validarData(data);

        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (departamento == null) {
            throw new IllegalArgumentException("O departamento é obrigatório.");
        }
        if (funcionario == null) {
            throw new IllegalArgumentException("O funcionário é obrigatório.");
        }

        if (funcionario.getDepartamento() == null
                || funcionario.getDepartamento().getId() != departamento.getId()) {
            throw new IllegalArgumentException(
                    "O funcionário não pertence ao departamento informado.");
        }
    }

    private static void validarData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        try {
            LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "A data deve estar no formato AAAA-MM-DD e ser uma data válida.");
        }
    }
}