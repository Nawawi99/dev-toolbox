package dev.awn.service.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class JwtService {
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public DecodedJwt decode(String token) throws Exception {
        String[] parts = token.trim().split("\\.");
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("JWT must contain header, payload, and optional signature sections.");
        }
        String header = pretty(decodePart(parts[0]));
        String payload = pretty(decodePart(parts[1]));
        String signature = parts.length == 3 ? parts[2] : "";
        JsonNode payloadNode = mapper.readTree(payload);
        String issuedAt = instantField(payloadNode, "iat");
        String expiresAt = instantField(payloadNode, "exp");
        boolean expired = payloadNode.has("exp") && Instant.ofEpochSecond(payloadNode.get("exp").asLong()).isBefore(Instant.now());
        return new DecodedJwt(header, payload, signature, issuedAt, expiresAt, expired);
    }

    private String decodePart(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private String pretty(String json) throws Exception {
        return mapper.writeValueAsString(mapper.readTree(json));
    }

    private String instantField(JsonNode node, String field) {
        if (!node.has(field) || !node.get(field).canConvertToLong()) {
            return "";
        }
        Instant instant = Instant.ofEpochSecond(node.get(field).asLong());
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant.atZone(ZoneId.systemDefault()));
    }

    public record DecodedJwt(String header, String payload, String signature, String issuedAt, String expiresAt,
                             boolean expired) {
    }
}
