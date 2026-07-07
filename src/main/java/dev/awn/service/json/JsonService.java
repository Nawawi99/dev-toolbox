package dev.awn.service.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class JsonService {
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final ObjectMapper compactMapper = new ObjectMapper();

    public String pretty(String input) throws JsonProcessingException {
        return mapper.writeValueAsString(compactMapper.readTree(input));
    }

    public String minify(String input) throws JsonProcessingException {
        return compactMapper.writeValueAsString(compactMapper.readTree(input));
    }

    public void validate(String input) throws JsonProcessingException {
        compactMapper.readTree(input);
    }

    public String sortKeys(String input) throws JsonProcessingException {
        JsonNode sorted = sort(compactMapper.readTree(input));
        return mapper.writeValueAsString(sorted);
    }

    public String escapeString(String input) throws JsonProcessingException {
        return compactMapper.writeValueAsString(input);
    }

    public String unescapeString(String input) throws JsonProcessingException {
        return compactMapper.readValue(input, String.class);
    }

    public String diff(String left, String right) throws JsonProcessingException {
        Map<String, String> l = flatten(compactMapper.readTree(left));
        Map<String, String> r = flatten(compactMapper.readTree(right));
        TreeSet<String> paths = new TreeSet<>();
        paths.addAll(l.keySet());
        paths.addAll(r.keySet());
        StringBuilder out = new StringBuilder();
        for (String path : paths) {
            String lv = l.get(path);
            String rv = r.get(path);
            if (!Objects.equals(lv, rv)) {
                if (lv == null) out.append("+ ").append(path).append(": ").append(rv).append('\n');
                else if (rv == null) out.append("- ").append(path).append(": ").append(lv).append('\n');
                else out.append("~ ").append(path).append(": ").append(lv).append(" -> ").append(rv).append('\n');
            }
        }
        return out.isEmpty() ? "No differences." : out.toString();
    }

    private JsonNode sort(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sorted = mapper.createObjectNode();
            List<String> names = new ArrayList<>();
            node.fieldNames().forEachRemaining(names::add);
            Collections.sort(names);
            for (String name : names) {
                sorted.set(name, sort(node.get(name)));
            }
            return sorted;
        }
        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                ((com.fasterxml.jackson.databind.node.ArrayNode) node).set(i, sort(node.get(i)));
            }
        }
        return node;
    }

    private Map<String, String> flatten(JsonNode node) {
        Map<String, String> result = new TreeMap<>();
        flatten("$", node, result);
        return result;
    }

    private void flatten(String path, JsonNode node, Map<String, String> out) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> flatten(path + "." + entry.getKey(), entry.getValue(), out));
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                flatten(path + "[" + i + "]", node.get(i), out);
            }
        } else {
            out.put(path, node.toString());
        }
    }
}
