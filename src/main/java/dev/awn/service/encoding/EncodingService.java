package dev.awn.service.encoding;

import org.apache.commons.text.StringEscapeUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

public class EncodingService {
    public String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    public String base64Decode(String input) {
        return new String(Base64.getDecoder().decode(input.trim()), StandardCharsets.UTF_8);
    }

    public String urlEncode(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    public String urlDecode(String input) {
        return URLDecoder.decode(input, StandardCharsets.UTF_8);
    }

    public String htmlEscape(String input) {
        return StringEscapeUtils.escapeHtml4(input);
    }

    public String htmlUnescape(String input) {
        return StringEscapeUtils.unescapeHtml4(input);
    }

    public String hexEncode(String input) {
        return HexFormat.of().formatHex(input.getBytes(StandardCharsets.UTF_8));
    }

    public String hexDecode(String input) {
        return new String(HexFormat.of().parseHex(input.replaceAll("\\s+", "")), StandardCharsets.UTF_8);
    }

    public String hash(String algorithm, String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    public String bcrypt(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }

    public boolean verifyBcrypt(String input, String hash) {
        return BCrypt.checkpw(input, hash);
    }
}
