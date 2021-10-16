package Sockets;

import Encryption.Aes256;
import Files.Keys;
import Hmac.Hmac;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class ProcessContinuousComm {
    private String SessionKey;
    private DataOutputStream out;
    private DataInputStream input;
    private int i = 0;
    private ProcessesRecv receiveData;
    private Hmac hmac = new Hmac();
    ArrayList<String> messages = new ArrayList<String>();

    public ProcessContinuousComm(DataOutputStream out, String SecretKey, DataInputStream input) {
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
        Scanner scan= new Scanner(System.in);
        while (!message.contains("Over")) {

            message= scan.nextLine();
            Send(Keys.KEY, message);

        }

    }

    protected void Send(byte[] secretKeyPk, String input) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

            String encStr = Aes256.encrypt(SessionKey, input);

        byte[] hmacToSend = Hmac.encodeHmac(secretKeyPk, encStr.getBytes(StandardCharsets.UTF_8));

        out.write(encStr.getBytes(StandardCharsets.UTF_8));
        out.write(hmacToSend);
        System.out.println(" Sent HMAC: " + hmac.convertHmacToHexString(hmacToSend));
        System.out.println(" Sent Encrypted Message: " + encStr);
    }

}
