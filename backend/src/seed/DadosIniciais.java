import java.util.Arrays;
import java.util.List;

public class DadosIniciais {

    public static List<Departamento> criarDepartamentos() {
        return Arrays.asList(
                new Departamento(1, "Administrativo"),
                new Departamento(2, "Financeiro"),
                new Departamento(3, "Recursos Humanos"),
                new Departamento(4, "Tecnologia")
        );
    }

    public static List<Categoria> criarCategorias() {
        return Arrays.asList(
                new Categoria(1, "Material de escritório"),
                new Categoria(2, "Transporte"),
                new Categoria(3, "Alimentação"),
                new Categoria(4, "Tecnologia"),
                new Categoria(5, "Serviços")
        );
    }

    public static List<Funcionario> criarFuncionarios() {
        List<Departamento> departamentos = criarDepartamentos();

        return Arrays.asList(
                new Funcionario(1, "Ana Souza", "Analista Administrativo", departamentos.get(0)),
                new Funcionario(2, "Bruno Lima", "Analista Financeiro", departamentos.get(1)),
                new Funcionario(3, "Carla Mendes", "Analista de RH", departamentos.get(2)),
                new Funcionario(4, "Diego Alves", "Desenvolvedor", departamentos.get(3)),
                new Funcionario(5, "Fernanda Costa", "Coordenadora Financeira", departamentos.get(1))
        );
    }

    public static List<Custo> criarCustos() {
        List<Departamento> departamentos = criarDepartamentos();
        List<Categoria> categorias = criarCategorias();
        List<Funcionario> funcionarios = criarFuncionarios();

        return Arrays.asList(
                new Custo(
                        1, 350.00, "Compra de material de escritório", "2026-09-10",
                        categorias.get(0), departamentos.get(0), funcionarios.get(0)
                ),
                new Custo(
                        2, 180.50, "Deslocamento para reunião", "2026-09-12",
                        categorias.get(1), departamentos.get(1), funcionarios.get(1)
                ),
                new Custo(
                        3, 95.00, "Almoço de reunião", "2026-09-15",
                        categorias.get(2), departamentos.get(2), funcionarios.get(2)
                ),
                new Custo(
                        4, 1250.00, "Compra de equipamentos de informática", "2026-09-18",
                        categorias.get(3), departamentos.get(3), funcionarios.get(3)
                ),
                new Custo(
                        5, 420.00, "Serviço de manutenção", "2026-09-20",
                        categorias.get(4), departamentos.get(1), funcionarios.get(4)
                ),
                new Custo(
                        6, 275.75, "Material para treinamento", "2026-09-22",
                        categorias.get(0), departamentos.get(2), funcionarios.get(2)
                )
        );
    }
}