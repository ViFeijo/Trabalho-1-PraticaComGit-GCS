import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ApiServerTest {

    private static void check(boolean ok, String msg) {
        if (!ok) throw new AssertionError("Falha no teste: " + msg);
    }

    public static void main(String[] args) throws Exception {
        ApiServer server = new ApiServer(0); // Port 0 binds to any free port
        server.start();
        int port = server.getPort();
        String baseUrl = "http://localhost:" + port;
        HttpClient client = HttpClient.newHttpClient();

        try {
            // 1. CORS Preflight OPTIONS
            HttpRequest optReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/seed"))
                    .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> optResp = client.send(optReq, HttpResponse.BodyHandlers.discarding());
            check(optResp.statusCode() == 204, "OPTIONS deve retornar 204");
            check(optResp.headers().firstValue("Access-Control-Allow-Origin").orElse("").equals("*"), "CORS origin deve ser *");

            // 2. GET /api/seed
            HttpRequest seedReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/seed"))
                    .GET()
                    .build();
            HttpResponse<String> seedResp = client.send(seedReq, HttpResponse.BodyHandlers.ofString());
            check(seedResp.statusCode() == 200, "GET /api/seed deve retornar 200");
            check(seedResp.body().contains("\"departamentos\":[") && seedResp.body().contains("\"categorias\":["),
                    "Seed deve conter departamentos e categorias");

            // 3. GET /api/funcionarios
            HttpRequest funcGetReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/funcionarios"))
                    .GET()
                    .build();
            HttpResponse<String> funcGetResp = client.send(funcGetReq, HttpResponse.BodyHandlers.ofString());
            check(funcGetResp.statusCode() == 200, "GET /api/funcionarios deve retornar 200");
            check(funcGetResp.body().contains("Ana Souza"), "Deve conter Ana Souza");

            // 4. POST /api/funcionarios (Sucesso)
            String novoFuncJson = "{\"nome\":\"João Teste\",\"cargo\":\"Analista\",\"departamentoId\":4}";
            HttpRequest funcPostReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/funcionarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(novoFuncJson))
                    .build();
            HttpResponse<String> funcPostResp = client.send(funcPostReq, HttpResponse.BodyHandlers.ofString());
            check(funcPostResp.statusCode() == 201, "POST /api/funcionarios deve retornar 201");
            check(funcPostResp.body().contains("\"id\":6") && funcPostResp.body().contains("João Teste"),
                    "Novo funcionário deve ter ID 6 e nome João Teste");

            // 5. POST /api/funcionarios (Erro de validação)
            String funcInvalidoJson = "{\"nome\":\"\",\"cargo\":\"Analista\",\"departamentoId\":4}";
            HttpRequest funcInvReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/funcionarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(funcInvalidoJson))
                    .build();
            HttpResponse<String> funcInvResp = client.send(funcInvReq, HttpResponse.BodyHandlers.ofString());
            check(funcInvResp.statusCode() == 400, "POST inválido deve retornar 400");
            check(funcInvResp.body().contains("\"error\":"), "Deve conter mensagem de erro");

            // 6. GET & POST /api/operador
            HttpRequest opGetReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/operador"))
                    .GET()
                    .build();
            HttpResponse<String> opGetResp = client.send(opGetReq, HttpResponse.BodyHandlers.ofString());
            check(opGetResp.statusCode() == 200, "GET /api/operador deve retornar 200");
            check(opGetResp.body().contains("\"operadorId\":null"), "Operador inicial deve ser null");

            String selOpJson = "{\"id\":4}";
            HttpRequest opPostReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/operador"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(selOpJson))
                    .build();
            HttpResponse<String> opPostResp = client.send(opPostReq, HttpResponse.BodyHandlers.ofString());
            check(opPostResp.statusCode() == 200, "POST /api/operador deve retornar 200");
            check(opPostResp.body().contains("\"operadorId\":4"), "Operador atualizado para 4");

            // 7. GET /api/custos
            HttpRequest custosGetReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/custos"))
                    .GET()
                    .build();
            HttpResponse<String> custosGetResp = client.send(custosGetReq, HttpResponse.BodyHandlers.ofString());
            check(custosGetResp.statusCode() == 200, "GET /api/custos deve retornar 200");
            check(custosGetResp.body().contains("Deslocamento para reunião"), "Deve conter custos iniciais");

            // 8. POST /api/custos (com operador selecionado)
            String novoCustoJson = "{\"valor\":250.0,\"descricao\":\"Monitor novo\",\"data\":\"2026-10-01\",\"categoriaId\":4}";
            HttpRequest custoPostReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/custos"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(novoCustoJson))
                    .build();
            HttpResponse<String> custoPostResp = client.send(custoPostReq, HttpResponse.BodyHandlers.ofString());
            check(custoPostResp.statusCode() == 201, "POST /api/custos deve retornar 201");
            check(custoPostResp.body().contains("\"id\":7") && custoPostResp.body().contains("Monitor novo"),
                    "Novo custo deve ter ID 7");

            // 9. DELETE /api/custos (Bloquear exclusão não mais recente)
            HttpRequest delAntigoReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/custos/1"))
                    .DELETE()
                    .build();
            HttpResponse<String> delAntigoResp = client.send(delAntigoReq, HttpResponse.BodyHandlers.ofString());
            check(delAntigoResp.statusCode() == 400, "Excluir custo antigo deve retornar 400");
            check(delAntigoResp.body().contains("Somente o custo mais recente pode ser excluído"),
                    "Deve retornar mensagem sobre exclusão apenas do mais recente");

            // 10. DELETE /api/custos/{id} (Excluir custo mais recente ID 7)
            HttpRequest delRecenteReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/custos/7"))
                    .DELETE()
                    .build();
            HttpResponse<String> delRecenteResp = client.send(delRecenteReq, HttpResponse.BodyHandlers.ofString());
            check(delRecenteResp.statusCode() == 200, "Excluir mais recente deve retornar 200");
            check(delRecenteResp.body().contains("\"success\":true"), "Deve confirmar exclusão");

            // 11. GET /api/dashboard?referencia=2026-09-30
            HttpRequest dashReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/dashboard?referencia=2026-09-30"))
                    .GET()
                    .build();
            HttpResponse<String> dashResp = client.send(dashReq, HttpResponse.BodyHandlers.ofString());
            check(dashResp.statusCode() == 200, "GET /api/dashboard deve retornar 200");
            check(dashResp.body().contains("\"total\":2571.25"), "Total do mês de setembro deve ser 2571.25");
            check(dashResp.body().contains("\"ranking\":["), "Deve conter ranking");

            // 12. POST & GET /api/budget
            String budgetPostJson = "{\"departamentoId\":4,\"mes\":\"2026-09\",\"limite\":1000.00}";
            HttpRequest budgetPostReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/budget"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(budgetPostJson))
                    .build();
            HttpResponse<String> budgetPostResp = client.send(budgetPostReq, HttpResponse.BodyHandlers.ofString());
            check(budgetPostResp.statusCode() == 200, "POST /api/budget deve retornar 200");

            HttpRequest budgetGetReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/budget?mes=2026-09"))
                    .GET()
                    .build();
            HttpResponse<String> budgetGetResp = client.send(budgetGetReq, HttpResponse.BodyHandlers.ofString());
            check(budgetGetResp.statusCode() == 200, "GET /api/budget deve retornar 200");
            check(budgetGetResp.body().contains("\"limite\":1000.00"), "Limite deve constar na resposta");

            // 13. GET /api/csv/export
            HttpRequest csvReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/csv/export"))
                    .GET()
                    .build();
            HttpResponse<byte[]> csvResp = client.send(csvReq, HttpResponse.BodyHandlers.ofByteArray());
            check(csvResp.statusCode() == 200, "GET /api/csv/export deve retornar 200");
            check(csvResp.headers().firstValue("Content-Type").orElse("").contains("text/csv"), "Content-Type deve ser text/csv");
            byte[] csvBytes = csvResp.body();
            check(csvBytes.length > 3, "CSV não deve estar vazio");
            check((csvBytes[0] & 255) == 239 && (csvBytes[1] & 255) == 187 && (csvBytes[2] & 255) == 191, "Deve conter BOM UTF-8");

            System.out.println("ApiServerTest: OK");
        } finally {
            server.stop();
        }
    }
}
