package dev.awn.service.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Matcher;
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

    public String visualizeStructure(String input) {
        String sql = minify(input);
        if (!sql.toLowerCase(Locale.ROOT).startsWith("select ")) {
            throw new IllegalArgumentException("SQL Visualizer currently supports SELECT queries.");
        }

        String lower = sql.toLowerCase(Locale.ROOT);
        String select = section(sql, lower, "select", "from");
        String fromAndJoins = section(sql, lower, "from", "where", "group by", "having", "order by", "limit");
        String where = section(sql, lower, "where", "group by", "having", "order by", "limit");
        String groupBy = section(sql, lower, "group by", "having", "order by", "limit");
        String having = section(sql, lower, "having", "order by", "limit");
        String orderBy = section(sql, lower, "order by", "limit");
        String limit = section(sql, lower, "limit");

        StringBuilder out = new StringBuilder();
        out.append("SQL Visualizer\n\n");
        out.append("[FROM]\n");
        out.append("  table: ").append(baseTable(fromAndJoins)).append('\n');

        List<String> joins = joins(fromAndJoins);
        for (String join : joins) {
            out.append("    |\n");
            out.append("    v\n");
            out.append("[JOIN]\n");
            out.append("  ").append(join).append('\n');
        }

        if (!where.isBlank()) {
            appendNode(out, "WHERE", "filter rows: " + where);
        }
        if (!groupBy.isBlank()) {
            appendNode(out, "GROUP BY", "group rows: " + groupBy);
        }
        if (!having.isBlank()) {
            appendNode(out, "HAVING", "filter groups: " + having);
        }
        appendNode(out, "SELECT", "return data: " + select);
        if (!orderBy.isBlank()) {
            appendNode(out, "ORDER BY", "sort result: " + orderBy);
        }
        if (!limit.isBlank()) {
            appendNode(out, "LIMIT", "limit result: " + limit);
        }
        out.append("\nThis is a query structure visualizer, not a database execution plan.");
        return out.toString();
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

    private String section(String sql, String lower, String start, String... ends) {
        int startIndex = lower.indexOf(start);
        if (startIndex < 0) return "";
        int contentStart = startIndex + start.length();
        int endIndex = sql.length();
        for (String end : ends) {
            int candidate = lower.indexOf(end, contentStart);
            if (candidate >= 0 && candidate < endIndex) {
                endIndex = candidate;
            }
        }
        return sql.substring(contentStart, endIndex).trim();
    }

    private String baseTable(String fromAndJoins) {
        if (fromAndJoins == null || fromAndJoins.isBlank()) return "unknown source";
        String[] parts = fromAndJoins.split("(?i)\\b(left|right|inner|full|cross)?\\s*join\\b", 2);
        return parts[0].trim();
    }

    private List<String> joins(String fromAndJoins) {
        List<String> joins = new ArrayList<>();
        Matcher matcher = Pattern.compile("(?i)\\b((?:left|right|inner|full|cross)\\s+join|join)\\s+(.+?)(?=\\b(?:left|right|inner|full|cross)?\\s*join\\b|$)")
                .matcher(fromAndJoins);
        while (matcher.find()) {
            String type = matcher.group(1).toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
            String body = matcher.group(2).trim();
            String[] parts = body.split("(?i)\\s+on\\s+", 2);
            if (parts.length == 2) {
                joins.add(type + " " + parts[0].trim() + " ON " + parts[1].trim());
            } else {
                joins.add(type + " " + body);
            }
        }
        return joins;
    }

    private void appendNode(StringBuilder out, String title, String detail) {
        out.append("    |\n");
        out.append("    v\n");
        out.append('[').append(title).append("]\n");
        out.append("  ").append(detail).append('\n');
    }
}
