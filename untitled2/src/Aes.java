import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Aes {
    private static byte[] encryptionKey = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    SecretKey key;
    byte[] encodedCipher;
    public Aes()  {
        this.key = new SecretKeySpec(encryptionKey, "AES");
    }


    public  String encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher encryptionCipher = Cipher.getInstance("AES/ECB/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        String strToEncrypt = "Hello World12345";
                encodedCipher = encryptionCipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder()
                .encodeToString(encodedCipher);

    }

}
