package dev.awn.service.yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlService {
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory()).enable(SerializationFeature.INDENT_OUTPUT);
    private final ObjectMapper json = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public void validate(String input) throws Exception {
        yaml.readTree(input);
    }

    public String format(String input) throws Exception {
        JsonNode node = yaml.readTree(input);
        return yaml.writeValueAsString(node);
    }

    public String yamlToJson(String input) throws Exception {
        return json.writeValueAsString(yaml.readTree(input));
    }

    public String jsonToYaml(String input) throws Exception {
        return yaml.writeValueAsString(json.readTree(input));
    }
}
