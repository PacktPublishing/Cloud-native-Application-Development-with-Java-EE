package cloud.nativ.javaee.config;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Simple CLI to create AES encrypted Strings.
 */
public class SecretGenerator {

    public static void main(String[] args) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec("MySecr3tPassw0rd".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encrypted = cipher.doFinal("a_password_to_be_encryped".getBytes());

        String base64Encrypted = "{AES:" + Base64.getEncoder().encodeToString(encrypted) + "}";
        System.out.println(base64Encrypted);

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String substring = base64Encrypted.substring(5, base64Encrypted.length() - 1);

        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(substring));
        String decoded = new String(bytes);
        System.out.println(decoded);
    }
}
