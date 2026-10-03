import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;

public class ApiServer {

    private int port;
    private HttpServer server;
    private java.util.concurrent.ExecutorService executor;

    private final List<Departamento> departamentos;
    private final List<Categoria> categorias;
    private final List<Funcionario> funcionarios;

    private final FuncionarioService funcionarioService;
    private final OperadorListaService operadorListaService;
    private final OperadorSelecaoService operadorSelecaoService;
    private final CustoService custoService;
    private final DashboardService dashboardService;
    private final BudgetLimitService budgetLimitService;
    private final CsvExportService csvExportService;

    public ApiServer(int port) {
        this.port = port;
        this.departamentos = DadosIniciais.criarDepartamentos();
        this.categorias = DadosIniciais.criarCategorias();
        this.funcionarios = new ArrayList<>(DadosIniciais.criarFuncionarios());

        this.funcionarioService = new FuncionarioService(this.funcionarios, this.departamentos);
        this.operadorListaService = new OperadorListaService(this.funcionarioService);
        this.operadorSelecaoService = new OperadorSelecaoService(this.funcionarioService);
        this.custoService = new CustoService();
        this.dashboardService = new DashboardService();
        this.budgetLimitService = new BudgetLimitService();
        this.csvExportService = new CsvExportService();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        server.setExecutor(this.executor);
        server.createContext("/api", new MainApiHandler());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public int getPort() {
        return server != null ? server.getAddress().getPort() : port;
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else if (System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        ApiServer apiServer = new ApiServer(port);
        apiServer.start();
        System.out.println("Servidor Backend iniciado na porta " + apiServer.getPort());
    }

    // Services getters for testing or extensions
    public FuncionarioService getFuncionarioService() { return funcionarioService; }
    public OperadorSelecaoService getOperadorSelecaoService() { return operadorSelecaoService; }
    public CustoService getCustoService() { return custoService; }
    public BudgetLimitService getBudgetLimitService() { return budgetLimitService; }

    private class MainApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Set CORS headers
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS, PUT");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");

                String method = exchange.getRequestMethod().toUpperCase();
                if ("OPTIONS".equals(method)) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                String path = exchange.getRequestURI().getPath();
                if (path.endsWith("/") && path.length() > 1) {
                    path = path.substring(0, path.length() - 1);
                }

                Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());

                if ("/api/seed".equals(path) && "GET".equals(method)) {
                    handleSeed(exchange);
                } else if ("/api/funcionarios".equals(path)) {
                    if ("GET".equals(method)) {
                        handleGetFuncionarios(exchange, query);
                    } else if ("POST".equals(method)) {
                        handlePostFuncionarios(exchange);
                    } else {
                        sendMethodNotAllowed(exchange);
                    }
                } else if ("/api/operador".equals(path)) {
                    if ("GET".equals(method)) {
                        handleGetOperador(exchange);
                    } else if ("POST".equals(method)) {
                        handlePostOperador(exchange);
                    } else {
                        sendMethodNotAllowed(exchange);
                    }
                } else if ("/api/custos".equals(path)) {
                    if ("GET".equals(method)) {
                        handleGetCustos(exchange, query);
                    } else if ("POST".equals(method)) {
                        handlePostCustos(exchange);
                    } else {
                        sendMethodNotAllowed(exchange);
                    }
                } else if (path.startsWith("/api/custos/") && "DELETE".equals(method)) {
                    handleDeleteCusto(exchange, path);
                } else if ("/api/dashboard".equals(path) && "GET".equals(method)) {
                    handleDashboard(exchange, query);
                } else if ("/api/budget".equals(path)) {
                    if ("GET".equals(method)) {
                        handleGetBudget(exchange, query);
                    } else if ("POST".equals(method)) {
                        handlePostBudget(exchange);
                    } else {
                        sendMethodNotAllowed(exchange);
                    }
                } else if ("/api/csv/export".equals(path) && "GET".equals(method)) {
                    handleExportCsv(exchange, query);
                } else {
                    sendNotFound(exchange);
                }
            } catch (Exception e) {
                sendError(exchange, 500, "Erro interno: " + e.getMessage());
            }
        }
    }

    private void handleSeed(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"departamentos\":[");
        for (int i = 0; i < departamentos.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(SimpleJson.toJson(departamentos.get(i)));
        }
        sb.append("],\"categorias\":[");
        for (int i = 0; i < categorias.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(SimpleJson.toJson(categorias.get(i)));
        }
        sb.append("]}");
        sendJson(exchange, 200, sb.toString());
    }

    private void handleGetFuncionarios(HttpExchange exchange, Map<String, String> query) throws IOException {
        List<Funcionario> lista;
        if (query.containsKey("departamentoId")) {
            int depId = Integer.parseInt(query.get("departamentoId"));
            lista = funcionarioService.listarPorDepartamento(depId);
        } else {
            lista = funcionarioService.listarTodos();
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(SimpleJson.toJson(lista.get(i)));
        }
        sb.append(']');
        sendJson(exchange, 200, sb.toString());
    }

    private void handlePostFuncionarios(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, Object> data = SimpleJson.parseObject(body);

        String nome = (String) data.get("nome");
        String cargo = (String) data.get("cargo");
        Number depIdNum = (Number) data.get("departamentoId");
        Number matNum = (Number) data.get("matricula");
        if (matNum == null) {
            matNum = (Number) data.get("id");
        }

        int matricula;
        if (matNum != null && matNum.intValue() > 0) {
            matricula = matNum.intValue();
        } else {
            matricula = funcionarioService.listarTodos().stream()
                    .mapToInt(Funcionario::getId)
                    .max().orElse(0) + 1;
        }

        int departamentoId = depIdNum != null ? depIdNum.intValue() : 0;

        try {
            Funcionario novo = funcionarioService.cadastrar(matricula, nome, cargo, departamentoId);
            sendJson(exchange, 201, SimpleJson.toJson(novo));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleGetOperador(HttpExchange exchange) throws IOException {
        Funcionario op = operadorSelecaoService.getOperadorAtual();
        List<Funcionario> operadores = operadorListaService.listarOperadores();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"operadorId\":").append(op != null ? op.getId() : "null").append(',');
        sb.append("\"operador\":").append(SimpleJson.toJson(op)).append(',');
        sb.append("\"operadores\":[");
        for (int i = 0; i < operadores.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(SimpleJson.toJson(operadores.get(i)));
        }
        sb.append("]}");
        sendJson(exchange, 200, sb.toString());
    }

    private void handlePostOperador(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, Object> data = SimpleJson.parseObject(body);

        Number idNum = (Number) data.get("id");
        if (idNum == null) {
            idNum = (Number) data.get("operadorId");
        }

        try {
            Funcionario op;
            if (idNum == null) {
                op = operadorSelecaoService.limpar();
            } else {
                op = operadorSelecaoService.selecionar(idNum.intValue());
            }

            StringBuilder sb = new StringBuilder("{");
            sb.append("\"operadorId\":").append(op != null ? op.getId() : "null").append(',');
            sb.append("\"operador\":").append(SimpleJson.toJson(op));
            sb.append("}");
            sendJson(exchange, 200, sb.toString());
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleGetCustos(HttpExchange exchange, Map<String, String> query) throws IOException {
        String descricao = query.get("descricao");
        String catStr = query.get("categoriaId");
        if (catStr == null) catStr = query.get("categoria");
        Integer categoriaId = catStr != null && !catStr.isEmpty() ? Integer.parseInt(catStr) : null;

        String data = query.get("data");
        String depStr = query.get("departamentoId");
        if (depStr == null) depStr = query.get("departamento");
        Integer departamentoId = depStr != null && !depStr.isEmpty() ? Integer.parseInt(depStr) : null;

        List<Custo> lista;
        if (descricao != null || categoriaId != null || data != null || departamentoId != null) {
            lista = custoService.pesquisarCustos(descricao, categoriaId, data, departamentoId);
            CustoService.ordenarPorMaisRecente(lista);
        } else {
            lista = custoService.getCustos();
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(SimpleJson.toJson(lista.get(i)));
        }
        sb.append(']');
        sendJson(exchange, 200, sb.toString());
    }

    private void handlePostCustos(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, Object> data = SimpleJson.parseObject(body);

        Number valorNum = (Number) data.get("valor");
        String descricao = (String) data.get("descricao");
        String dataCusto = (String) data.get("data");
        Number catIdNum = (Number) data.get("categoriaId");
        Number depIdNum = (Number) data.get("departamentoId");
        Number funcIdNum = (Number) data.get("funcionarioId");
        if (funcIdNum == null) {
            funcIdNum = (Number) data.get("operadorId");
        }

        try {
            Funcionario func;
            if (funcIdNum != null) {
                func = funcionarioService.buscarPorMatricula(funcIdNum.intValue());
                if (func == null) {
                    throw new IllegalArgumentException("Funcionário não encontrado: " + funcIdNum);
                }
            } else {
                func = operadorSelecaoService.getOperadorAtual();
                if (func == null) {
                    throw new IllegalArgumentException("Selecione o operador atual antes de cadastrar um custo.");
                }
            }

            Departamento dep;
            if (depIdNum != null) {
                dep = departamentos.stream().filter(d -> d.getId() == depIdNum.intValue()).findFirst().orElse(null);
            } else {
                dep = func.getDepartamento();
            }

            Categoria cat = null;
            if (catIdNum != null) {
                cat = categorias.stream().filter(c -> c.getId() == catIdNum.intValue()).findFirst().orElse(null);
            }

            double valor = valorNum != null ? valorNum.doubleValue() : Double.NaN;

            Custo criado = custoService.registrarCusto(valor, descricao, dataCusto, cat, dep, func);
            sendJson(exchange, 201, SimpleJson.toJson(criado));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleDeleteCusto(HttpExchange exchange, String path) throws IOException {
        String idStr = path.substring("/api/custos/".length());
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(exchange, 400, "ID inválido: " + idStr);
            return;
        }

        try {
            boolean removido = custoService.excluirCusto(id);
            if (removido) {
                sendJson(exchange, 200, "{\"success\":true,\"id\":" + id + "}");
            } else {
                sendError(exchange, 404, "Custo não encontrado: " + id);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleDashboard(HttpExchange exchange, Map<String, String> query) throws IOException {
        String refStr = query.get("referencia");
        LocalDate refDate;
        try {
            refDate = (refStr != null && !refStr.isEmpty()) ? LocalDate.parse(refStr) : LocalDate.now();
        } catch (Exception e) {
            sendError(exchange, 400, "Data de referência inválida (use AAAA-MM-DD): " + refStr);
            return;
        }

        try {
            List<Custo> custos = custoService.getCustos();
            ResumoDashboard resumo = dashboardService.calcular(custos, departamentos, refDate);

            YearMonth ymAtual = YearMonth.from(refDate);
            int count = 0;
            for (Custo c : custos) {
                try {
                    if (YearMonth.from(LocalDate.parse(c.getData())).equals(ymAtual)) {
                        count++;
                    }
                } catch (Exception ignored) {}
            }

            // Build dashboard JSON
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"totalMesAtual\":").append(resumo.getTotalMesAtual()).append(',');
            sb.append("\"total\":").append(resumo.getTotalMesAtual()).append(',');
            sb.append("\"count\":").append(count).append(',');

            // Meses
            List<TotaisMes> meses = resumo.getMeses();
            sb.append("\"meses\":[");
            for (int i = 0; i < meses.size(); i++) {
                if (i > 0) sb.append(',');
                sb.append(SimpleJson.toJson(meses.get(i)));
            }
            sb.append("],");

            // Months array for frontend: [{ key: "2026-07", label: "jul. de 2026" }, ...]
            DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMM. 'de' yyyy", Locale.of("pt", "BR"));
            sb.append("\"months\":[");
            for (int i = 0; i < meses.size(); i++) {
                if (i > 0) sb.append(',');
                TotaisMes tm = meses.get(i);
                YearMonth ym = YearMonth.of(tm.getAno(), tm.getMes());
                String key = ym.toString();
                String label = ym.format(labelFmt);
                sb.append("{\"key\":").append(SimpleJson.escape(key))
                  .append(",\"label\":").append(SimpleJson.escape(label)).append("}");
            }
            sb.append("],");

            // Departments with values
            sb.append("\"departments\":[");
            for (int i = 0; i < departamentos.size(); i++) {
                if (i > 0) sb.append(',');
                Departamento dep = departamentos.get(i);
                sb.append("{\"departamento\":").append(SimpleJson.toJson(dep)).append(",\"values\":[");
                for (int m = 0; m < meses.size(); m++) {
                    if (m > 0) sb.append(',');
                    double val = 0.0;
                    for (TotalDepartamento td : meses.get(m).getTotais()) {
                        if (td.getDepartamentoId() == dep.getId()) {
                            val = td.getTotal();
                            break;
                        }
                    }
                    sb.append(val);
                }
                sb.append("]}");
            }
            sb.append("],");

            // Ranking
            List<RankingFuncionario> ranking = resumo.getRanking();
            sb.append("\"ranking\":[");
            for (int i = 0; i < ranking.size(); i++) {
                if (i > 0) sb.append(',');
                sb.append(SimpleJson.toJson(ranking.get(i)));
            }
            sb.append("]}");

            sendJson(exchange, 200, sb.toString());
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleGetBudget(HttpExchange exchange, Map<String, String> query) throws IOException {
        String mesStr = query.get("mes");
        YearMonth mes;
        try {
            mes = (mesStr != null && !mesStr.isEmpty()) ? YearMonth.parse(mesStr) : YearMonth.now();
        } catch (Exception e) {
            sendError(exchange, 400, "Mês inválido (use AAAA-MM): " + mesStr);
            return;
        }

        String depIdStr = query.get("departamentoId");
        try {
            List<Custo> custos = custoService.getCustos();
            if (depIdStr != null && !depIdStr.isEmpty()) {
                int depId = Integer.parseInt(depIdStr);
                Departamento dep = departamentos.stream().filter(d -> d.getId() == depId).findFirst().orElse(null);
                if (dep == null) {
                    sendError(exchange, 404, "Departamento não encontrado: " + depId);
                    return;
                }
                BudgetLimitService.BudgetConsumption bc = budgetLimitService.consultar(dep, mes, custos);
                sendJson(exchange, 200, formatBudgetConsumption(dep, mes.toString(), bc));
            } else {
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < departamentos.size(); i++) {
                    if (i > 0) sb.append(',');
                    Departamento dep = departamentos.get(i);
                    BudgetLimitService.BudgetConsumption bc = budgetLimitService.consultar(dep, mes, custos);
                    sb.append(formatBudgetConsumption(dep, mes.toString(), bc));
                }
                sb.append(']');
                sendJson(exchange, 200, sb.toString());
            }
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private String formatBudgetConsumption(Departamento dep, String mes, BudgetLimitService.BudgetConsumption bc) {
        BigDecimal limite = bc.getLimite().orElse(null);
        BigDecimal saldo = bc.getSaldo().orElse(null);
        BigDecimal percentual = bc.getPercentual().orElse(null);

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"departamentoId\":").append(dep.getId()).append(',');
        sb.append("\"departamento\":").append(SimpleJson.toJson(dep)).append(',');
        sb.append("\"mes\":").append(SimpleJson.escape(mes)).append(',');
        sb.append("\"limite\":").append(limite != null ? limite.toString() : "null").append(',');
        sb.append("\"consumo\":").append(bc.getConsumo().toString()).append(',');
        sb.append("\"saldo\":").append(saldo != null ? saldo.toString() : "null").append(',');
        sb.append("\"percentual\":").append(percentual != null ? percentual.toString() : "null").append(',');
        sb.append("\"excedido\":").append(bc.isExcedido());
        sb.append("}");
        return sb.toString();
    }

    private void handlePostBudget(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, Object> data = SimpleJson.parseObject(body);

        Number depIdNum = (Number) data.get("departamentoId");
        String mesStr = (String) data.get("mes");
        Number limiteNum = (Number) data.get("limite");

        if (depIdNum == null || mesStr == null || limiteNum == null) {
            sendError(exchange, 400, "departamentoId, mes e limite são obrigatórios.");
            return;
        }

        try {
            int depId = depIdNum.intValue();
            Departamento dep = departamentos.stream().filter(d -> d.getId() == depId).findFirst().orElse(null);
            if (dep == null) {
                sendError(exchange, 400, "Departamento inexistente: " + depId);
                return;
            }

            YearMonth mes = YearMonth.parse(mesStr);
            BigDecimal limite = BigDecimal.valueOf(limiteNum.doubleValue()).setScale(2, RoundingMode.HALF_UP);

            budgetLimitService.configurarLimite(dep, mes, limite);

            StringBuilder sb = new StringBuilder("{");
            sb.append("\"success\":true,");
            sb.append("\"departamentoId\":").append(depId).append(',');
            sb.append("\"mes\":").append(SimpleJson.escape(mesStr)).append(',');
            sb.append("\"limite\":").append(limite.toString());
            sb.append("}");
            sendJson(exchange, 200, sb.toString());
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, 400, "Dados de limite inválidos: " + e.getMessage());
        }
    }

    private void handleExportCsv(HttpExchange exchange, Map<String, String> query) throws IOException {
        String descricao = query.get("descricao");
        String catStr = query.get("categoriaId");
        if (catStr == null) catStr = query.get("categoria");
        Integer categoriaId = catStr != null && !catStr.isEmpty() ? Integer.parseInt(catStr) : null;

        String data = query.get("data");
        String depStr = query.get("departamentoId");
        if (depStr == null) depStr = query.get("departamento");
        Integer departamentoId = depStr != null && !depStr.isEmpty() ? Integer.parseInt(depStr) : null;

        List<Custo> lista;
        if (descricao != null || categoriaId != null || data != null || departamentoId != null) {
            lista = custoService.pesquisarCustos(descricao, categoriaId, data, departamentoId);
            CustoService.ordenarPorMaisRecente(lista);
        } else {
            lista = custoService.getCustos();
        }

        byte[] csvBytes = csvExportService.exportar(lista);

        exchange.getResponseHeaders().set("Content-Type", "text/csv; charset=utf-8");
        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"custos.csv\"");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, csvBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(csvBytes);
        }
    }

    private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJson(exchange, statusCode, SimpleJson.errorJson(message));
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        sendError(exchange, 404, "Endpoint não encontrado.");
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendError(exchange, 405, "Método não permitido.");
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            return result;
        }
        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String val = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                result.put(key, val);
            } else if (!pair.isEmpty()) {
                result.put(URLDecoder.decode(pair, StandardCharsets.UTF_8), "");
            }
        }
        return result;
    }
}
