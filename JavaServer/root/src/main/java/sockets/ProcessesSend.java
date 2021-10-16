package sockets;

import crypto.Aes256;
import security.Hmac;
import utils.Keys;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ProcessesSend {
    private OutputStream out;
    private static int Id = 1;
    private Hmac hmac = new Hmac();
    public String SessionKey = null;

    public ProcessesSend(Socket socket, OutputStream out, DataInputStream input) {
        this.out = out;
    }

    public void run1(int Id) throws IOException {
        ProcessesSend.Id = Id;
        firstStepSend(Keys.KEY, Id);
    }

    public void run2() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        secondStepSend(Keys.KEY);
    }


    protected byte[] firstStepSend(byte[] secretKeyPk, int Id) throws IOException {
        byte[] challenge = Keys.MapIdChallenge.get(Id);

        byte[] hmacToSend = Hmac.encodeHmac(secretKeyPk, challenge);
        out.write(hmacToSend);
        System.out.println("Step 1 Server --- Sent: " + hmac.convertHmacToHexString(hmacToSend));
        return hmacToSend;
    }

    protected byte[] secondStepSend(byte[] secretKeyPk) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKey encKeyToSend = Aes256.generateKey(128);
        SecretKey originalKeyToUse = new SecretKeySpec(secretKeyPk, 0, secretKeyPk.length, "AES");
        SessionKey = Aes256.convertSecretKeyToString(encKeyToSend);

        String encStr = Aes256.encrypt(Aes256.convertSecretKeyToString(originalKeyToUse), Aes256.convertSecretKeyToString(encKeyToSend));
        byte[] hmacToSend = Hmac.encodeHmac(secretKeyPk, encStr.getBytes(StandardCharsets.UTF_8));

        out.write(encStr.getBytes(StandardCharsets.UTF_8));
        out.write(hmacToSend);
        System.out.println("Step 1 Server --- Sent HMAC: " + hmac.convertHmacToHexString(hmacToSend));
        System.out.println("Step 1 Server --- Sent Encrypted Message: " + encStr);

        return hmacToSend;
    }
}
