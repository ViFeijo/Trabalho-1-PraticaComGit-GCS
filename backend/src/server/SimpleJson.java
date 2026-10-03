import java.math.BigDecimal;
import java.util.*;

public class SimpleJson {

    public static Object parse(String json) {
        if (json == null) return null;
        json = json.trim();
        if (json.isEmpty()) return null;
        return new Parser(json).parseValue();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object obj = parse(json);
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return new HashMap<>();
    }

    private static class Parser {
        private final String src;
        private int pos = 0;

        Parser(String src) {
            this.src = src;
        }

        private void skipWhitespace() {
            while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            skipWhitespace();
            if (pos >= src.length()) return '\0';
            return src.charAt(pos);
        }

        private char next() {
            skipWhitespace();
            if (pos >= src.length()) return '\0';
            return src.charAt(pos++);
        }

        Object parseValue() {
            char c = peek();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') return parseNull();
            if (c == '-' || (c >= '0' && c <= '9')) return parseNumber();
            throw new IllegalArgumentException("Unexpected character at pos " + pos + ": " + c);
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            next(); // consume '{'
            skipWhitespace();
            if (peek() == '}') {
                next();
                return map;
            }
            while (true) {
                skipWhitespace();
                if (peek() != '"') throw new IllegalArgumentException("Expected string key at pos " + pos);
                String key = parseString();
                skipWhitespace();
                if (next() != ':') throw new IllegalArgumentException("Expected ':' at pos " + pos);
                Object val = parseValue();
                map.put(key, val);
                skipWhitespace();
                char c = next();
                if (c == '}') break;
                if (c != ',') throw new IllegalArgumentException("Expected ',' or '}' at pos " + pos + " but got " + c);
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            next(); // consume '['
            skipWhitespace();
            if (peek() == ']') {
                next();
                return list;
            }
            while (true) {
                Object val = parseValue();
                list.add(val);
                skipWhitespace();
                char c = next();
                if (c == ']') break;
                if (c != ',') throw new IllegalArgumentException("Expected ',' or ']' at pos " + pos);
            }
            return list;
        }

        String parseString() {
            next(); // consume opening quote
            StringBuilder sb = new StringBuilder();
            while (pos < src.length()) {
                char c = src.charAt(pos++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    if (pos >= src.length()) throw new IllegalArgumentException("Unterminated escape sequence");
                    char esc = src.charAt(pos++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (pos + 4 > src.length()) throw new IllegalArgumentException("Invalid unicode escape");
                            String hex = src.substring(pos, pos + 4);
                            pos += 4;
                            sb.append((char) Integer.parseInt(hex, 16));
                            break;
                        default: sb.append(esc); break;
                    }
                } else {
                    sb.append(c);
                }
            }
            throw new IllegalArgumentException("Unterminated string");
        }

        Boolean parseBoolean() {
            if (src.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            }
            if (src.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Expected boolean at pos " + pos);
        }

        Object parseNull() {
            if (src.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            throw new IllegalArgumentException("Expected null at pos " + pos);
        }

        Number parseNumber() {
            int start = pos;
            if (src.charAt(pos) == '-') pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
            boolean isFloating = false;
            if (pos < src.length() && src.charAt(pos) == '.') {
                isFloating = true;
                pos++;
                while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
            }
            if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
                isFloating = true;
                pos++;
                if (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) pos++;
                while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
            }
            String numStr = src.substring(start, pos);
            if (isFloating) {
                return Double.parseDouble(numStr);
            }
            try {
                long l = Long.parseLong(numStr);
                if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                    return (int) l;
                }
                return l;
            } catch (NumberFormatException e) {
                return Double.parseDouble(numStr);
            }
        }
    }

    public static String escape(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static String toJson(Departamento d) {
        if (d == null) return "null";
        return "{\"id\":" + d.getId() + ",\"nome\":" + escape(d.getNome()) + "}";
    }

    public static String toJson(Categoria c) {
        if (c == null) return "null";
        return "{\"id\":" + c.getId() + ",\"nome\":" + escape(c.getNome()) + "}";
    }

    public static String toJson(Funcionario f) {
        if (f == null) return "null";
        return "{\"id\":" + f.getId()
                + ",\"nome\":" + escape(f.getNome())
                + ",\"cargo\":" + escape(f.getCargo())
                + ",\"departamento\":" + toJson(f.getDepartamento()) + "}";
    }

    public static String toJson(Custo c) {
        if (c == null) return "null";
        return "{\"id\":" + c.getId()
                + ",\"valor\":" + c.getValor()
                + ",\"descricao\":" + escape(c.getDescricao())
                + ",\"data\":" + escape(c.getData())
                + ",\"categoria\":" + toJson(c.getCategoria())
                + ",\"departamento\":" + toJson(c.getDepartamento())
                + ",\"funcionario\":" + toJson(c.getFuncionario()) + "}";
    }

    public static String toJson(TotalDepartamento t) {
        if (t == null) return "null";
        return "{\"departamentoId\":" + t.getDepartamentoId()
                + ",\"departamentoNome\":" + escape(t.getDepartamentoNome())
                + ",\"total\":" + t.getTotal() + "}";
    }

    public static String toJson(TotaisMes m) {
        if (m == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ano\":").append(m.getAno())
          .append(",\"mes\":").append(m.getMes())
          .append(",\"totais\":[");
        List<TotalDepartamento> totais = m.getTotais();
        for (int i = 0; i < totais.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(toJson(totais.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String toJson(RankingFuncionario r) {
        if (r == null) return "null";
        return "{\"funcionario\":" + toJson(r.getFuncionario())
                + ",\"total\":" + r.getTotal() + "}";
    }

    public static String errorJson(String message) {
        return "{\"error\":" + escape(message) + "}";
    }
}
