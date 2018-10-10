package cloud.nativ.javaee.config;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * A Secret implementation to decrypt symmetric AES encrypted secrets.
 */
public class Secret {

    private final String encrypted;

    private SecretKeySpec secretKey;
    private Cipher cipher;

    Secret(String encrypted) {
        if (!encrypted.startsWith("{AES:")) {
            throw new IllegalArgumentException("Invalid ");
        }

        this.encrypted = encrypted.substring(5, encrypted.length() - 1);
        this.secretKey = new SecretKeySpec("MySecr3tPassw0rd".getBytes(), "AES");

        try {
            this.cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (GeneralSecurityException e) {
            throw new SecurityException(e);
        }

    }

    @Override
    public String toString() {
        return decrypt();
    }

    public String decrypt() {
        try {
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(bytes);
        } catch (GeneralSecurityException e) {
            throw new SecurityException(e);
        }
    }

}
