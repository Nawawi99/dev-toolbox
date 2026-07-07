package dev.awn.service.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Pattern;

public class SqlService {
    private static final List<String> KEYWORDS = List.of("select", "from", "where", "join", "left join", "right join",
            "inner join", "outer join", "group by", "order by", "having", "insert", "into", "values", "update",
            "set", "delete", "create", "table", "and", "or", "on", "limit");
    private final ObjectMapper mapper = new ObjectMapper();

    public String format(String input) {
        String sql = capitalize(input).replaceAll("\\s+", " ").trim();
        for (String keyword : List.of("FROM", "WHERE", "JOIN", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "GROUP BY",
                "ORDER BY", "HAVING", "VALUES", "SET", "LIMIT")) {
            sql = sql.replaceAll("(?i)\\s+" + Pattern.quote(keyword) + "\\s+", "\n" + keyword + " ");
        }
        return sql.replace(",", ",\n    ");
    }

    public String minify(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }

    public String capitalize(String input) {
        String result = input;
        for (String keyword : KEYWORDS.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList()) {
            result = result.replaceAll("(?i)\\b" + Pattern.quote(keyword) + "\\b", keyword.toUpperCase(Locale.ROOT));
        }
        return result;
    }

    public String toJavaString(String sql) {
        return "\"" + sql.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n\" +\n\"") + "\";";
    }

    public String toTextBlock(String sql) {
        return "\"\"\"\n" + sql.replace("\"\"\"", "\\\"\\\"\\\"") + "\n\"\"\";";
    }

    public String insertsFromJson(String table, String jsonArray) throws Exception {
        if (table == null || table.isBlank()) {
            throw new IllegalArgumentException("Enter a table name.");
        }
        JsonNode rows = mapper.readTree(jsonArray);
        if (!rows.isArray()) {
            throw new IllegalArgumentException("Input must be a JSON array of objects.");
        }
        StringBuilder out = new StringBuilder();
        for (JsonNode row : rows) {
            if (!row.isObject()) {
                throw new IllegalArgumentException("Every array item must be an object.");
            }
            List<String> columns = new ArrayList<>();
            row.fieldNames().forEachRemaining(columns::add);
            String values = columns.stream().map(c -> sqlLiteral(row.get(c))).reduce((a, b) -> a + ", " + b).orElse("");
            out.append("INSERT INTO ").append(table.trim()).append(" (")
                    .append(String.join(", ", columns)).append(") VALUES (").append(values).append(");\n");
        }
        return out.toString();
    }

    private String sqlLiteral(JsonNode node) {
        if (node == null || node.isNull()) return "NULL";
        if (node.isNumber() || node.isBoolean()) return node.asText();
        return "'" + node.asText().replace("'", "''") + "'";
    }
}
