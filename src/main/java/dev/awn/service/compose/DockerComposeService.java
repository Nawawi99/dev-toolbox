package dev.awn.service.compose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.*;

public class DockerComposeService {
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

    public String analyze(String input) throws Exception {
        JsonNode root = yaml.readTree(input);
        JsonNode services = root.path("services");
        if (!services.isObject()) {
            throw new IllegalArgumentException("docker-compose YAML must contain a services object.");
        }
        StringBuilder out = new StringBuilder("Services\n");
        Map<String, List<String>> hostPorts = new TreeMap<>();
        Map<String, List<String>> deps = new TreeMap<>();
        services.fields().forEachRemaining(entry -> {
            String name = entry.getKey();
            JsonNode service = entry.getValue();
            out.append("- ").append(name).append('\n');
            List<String> ports = ports(service.path("ports"));
            if (!ports.isEmpty()) {
                out.append("  ports: ").append(String.join(", ", ports)).append('\n');
                for (String port : ports) {
                    String host = port.contains(":") ? port.substring(0, port.indexOf(':')) : "";
                    if (!host.isBlank()) hostPorts.computeIfAbsent(host, ignored -> new ArrayList<>()).add(name);
                }
            }
            List<String> dependsOn = depends(service.path("depends_on"));
            deps.put(name, dependsOn);
            if (!dependsOn.isEmpty()) out.append("  depends_on: ").append(String.join(", ", dependsOn)).append('\n');
        });
        out.append("\nDuplicate host ports\n");
        boolean duplicate = false;
        for (Map.Entry<String, List<String>> entry : hostPorts.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicate = true;
                out.append("- ").append(entry.getKey()).append(": ").append(String.join(", ", entry.getValue())).append('\n');
            }
        }
        if (!duplicate) out.append("None.\n");
        out.append("\nDependency tree\n");
        for (String service : deps.keySet()) {
            renderTree(service, deps, out, "", new HashSet<>());
        }
        return out.toString();
    }

    private List<String> ports(JsonNode node) {
        List<String> ports = new ArrayList<>();
        if (!node.isArray()) return ports;
        for (JsonNode item : node) {
            if (item.isTextual()) ports.add(item.asText());
            else if (item.has("published") && item.has("target")) ports.add(item.get("published").asText() + ":" + item.get("target").asText());
        }
        return ports;
    }

    private List<String> depends(JsonNode node) {
        List<String> deps = new ArrayList<>();
        if (node.isArray()) node.forEach(item -> deps.add(item.asText()));
        else if (node.isObject()) node.fieldNames().forEachRemaining(deps::add);
        return deps;
    }

    private void renderTree(String service, Map<String, List<String>> deps, StringBuilder out, String indent, Set<String> seen) {
        out.append(indent).append("- ").append(service).append('\n');
        if (!seen.add(service)) {
            out.append(indent).append("  circular dependency detected\n");
            return;
        }
        for (String dep : deps.getOrDefault(service, List.of())) {
            renderTree(dep, deps, out, indent + "  ", new HashSet<>(seen));
        }
    }
}
