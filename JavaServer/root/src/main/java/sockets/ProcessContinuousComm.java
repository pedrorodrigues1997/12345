package sockets;

import crypto.Aes256;
import security.Hmac;
import utils.Keys;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ProcessContinuousComm {
    private String SessionKey;
    private OutputStream out;
    private DataInputStream input;
    private int i = 0;
    private ProcessesReceive receiveData;
    private Hmac hmac = new Hmac();
    ArrayList<String> messages = new ArrayList<String>();

    public ProcessContinuousComm(OutputStream out, String SecretKey, DataInputStream input) {
        this.out = out;
        this.input = input;
        SessionKey = SecretKey;
        messages.add("Hi!");
        messages.add("Heres Data!");
        messages.add("More data!");
        messages.add("Tired already?!");
        messages.add("Bye Bye!");
        messages.add("Over");
    }

    public void run() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException {
    String message = "";
        while (!message.contains("Over")) {
            message = Recv();
            System.out.println("Decrypted Message: " + message);


        }

    }

    protected String Recv() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encmessage = null;
        byte[] message = null;
        for (int i = 0; i < 2; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            baos.write(buffer, 0, input.read(buffer));
            if (i == 0) {
                encmessage = baos.toString(Charset.defaultCharset());
                System.out.println("Received from client the encrypted message : " + encmessage);
            }
            if (i == 1) {
                message = baos.toByteArray();
                System.out.println("Received from client the HMAC : " + String.format("%032x", new BigInteger(1, message)));
            }
        }
        Hmac hmac = new Hmac();
        if (hmac.compareHmac(Keys.KEY, encmessage.getBytes(Charset.defaultCharset()), message)) {
            System.out.println("Hmac checks up, advancing...");
            return Aes256.decrypt(SessionKey,
                    encmessage);

        }
        return "Over";
    }

}
