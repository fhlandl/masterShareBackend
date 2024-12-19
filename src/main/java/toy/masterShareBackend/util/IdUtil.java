package toy.masterShareBackend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class IdUtil {

    // ToDo: 트래픽이 많아졌을 때 유일성 보장에 대한 고민 필요
    public static String generateUniqueId() {
        String uuid = UUID.randomUUID().toString();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(uuid.getBytes(StandardCharsets.UTF_8));
            String base64Encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
            return base64Encoded.substring(0, 20);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occurred during generating unique id");
        }
    }
}
