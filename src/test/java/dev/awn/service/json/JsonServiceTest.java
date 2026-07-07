package dev.awn.service.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonServiceTest {
    private final JsonService service = new JsonService();

    @Test
    void minifiesValidJson() throws Exception {
        assertEquals("{\"a\":1}", service.minify("{\n  \"a\": 1\n}"));
    }

    @Test
    void sortsKeysRecursively() throws Exception {
        String sorted = service.sortKeys("{\"b\":1,\"a\":{\"d\":2,\"c\":3}}");
        assertTrue(sorted.indexOf("\"a\"") < sorted.indexOf("\"b\""));
        assertTrue(sorted.indexOf("\"c\"") < sorted.indexOf("\"d\""));
    }
}
