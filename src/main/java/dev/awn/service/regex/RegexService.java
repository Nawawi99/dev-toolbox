package dev.awn.service.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexService {
    public String matches(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuilder out = new StringBuilder();
        int count = 0;
        while (matcher.find()) {
            count++;
            out.append("Match ").append(count).append(" [").append(matcher.start()).append(", ")
                    .append(matcher.end()).append("): ").append(matcher.group()).append('\n');
            for (int i = 1; i <= matcher.groupCount(); i++) {
                out.append("  group ").append(i).append(": ").append(matcher.group(i)).append('\n');
            }
        }
        return count == 0 ? "No matches." : out.toString();
    }

    public String replacementPreview(String regex, String replacement, String text) {
        return Pattern.compile(regex).matcher(text).replaceAll(replacement);
    }

    public String javaString(String regex) {
        return "\"" + regex.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
