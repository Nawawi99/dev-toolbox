package dev.awn.service.encoding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncodingServiceTest {
    private final EncodingService service = new EncodingService();

    @Test
    void base64RoundTrip() {
        assertEquals("hello", service.base64Decode(service.base64Encode("hello")));
    }

    @Test
    void hashesSha256() throws Exception {
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                service.hash("SHA-256", "hello"));
    }
}
