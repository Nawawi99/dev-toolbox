package dev.awn.service.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Locale;

public class LogService {
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public String filter(String logs, String keyword, String level, boolean prettyJson) {
        StringBuilder out = new StringBuilder();
        StringBuilder stack = new StringBuilder();
        for (String line : logs.split("\\R", -1)) {
            boolean stackLine = line.startsWith("\tat ") || line.matches("^\\s*\\.\\.\\. \\d+ more.*") || line.matches("^Caused by:.*");
            if (stackLine) {
                stack.append(line).append('\n');
                continue;
            }
            flushStack(out, stack);
            if (matches(line, keyword, level)) {
                out.append(prettyJson ? prettyJsonLine(line) : line).append('\n');
            }
        }
        flushStack(out, stack);
        return out.isEmpty() ? "No matching log lines." : out.toString();
    }

    private boolean matches(String line, String keyword, String level) {
        boolean keywordOk = keyword == null || keyword.isBlank() || line.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
        boolean levelOk = level == null || level.isBlank() || "ANY".equals(level) || line.toUpperCase(Locale.ROOT).contains(level);
        return keywordOk && levelOk;
    }

    private String prettyJsonLine(String line) {
        int start = line.indexOf('{');
        if (start < 0) return line;
        try {
            return line.substring(0, start) + mapper.writeValueAsString(mapper.readTree(line.substring(start)));
        } catch (Exception ignored) {
            return line;
        }
    }

    private void flushStack(StringBuilder out, StringBuilder stack) {
        if (!stack.isEmpty()) {
            out.append("\n--- stack trace ---\n").append(stack);
            stack.setLength(0);
        }
    }
}
