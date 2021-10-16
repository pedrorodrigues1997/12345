package Sockets;

import Encryption.Aes256;
import Files.Keys;
import Hmac.Hmac;

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
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import Hmac.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ProcessesRecv {

    private Socket socket;
    private DataInputStream input;
    private Date time = new Date();
    public static String SessionKey = null;
    int Id = 0;

    public ProcessesRecv(Socket socket, DataInputStream input) {
        this.socket = socket;
        this.input = input;
    }

    public boolean run1(int Id) throws IOException {
        this.Id = Id;
        return firstStepRecv();
    }
    public boolean run2() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return secondStepRecv();
    }

    protected boolean firstStepRecv() throws IOException {
        byte[] message = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            baos.write(buffer, 0 , input.read(buffer));
                message = baos.toByteArray();
                System.out.println("Received from Server the message : " + String.format("%032x", new BigInteger(1, message)));


        Hmac hmac = new Hmac();
        if(hmac.compareHmac(Keys.KEY, Keys.MapIdChallenge.get(Id), message)){
            System.out.println("HMAC from server matches the expected");
            System.out.println("Advancing...");
            return true;
        }else{
            System.out.println("HMAC from server did not match");
            System.out.println("Terminating");
            return false;
        }
    }

    protected boolean secondStepRecv() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encmessage = null;
        byte[] message = null;
        String algorithm = "AES/CBC/PKCS5Padding";
        SecretKey originalKeyToUse = new SecretKeySpec(Keys.KEY, 0, Keys.KEY.length, "AES");
        for(int i = 0; i < 2; i++){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            baos.write(buffer, 0 , input.read(buffer));
            if(i == 0){
                encmessage = baos.toString(Charset.defaultCharset());
                System.out.println("Received from client the encrypted message : " + encmessage);
            }
            if(i == 1){
                message = baos.toByteArray();
                System.out.println("Received from client the HMAC : " + String.format("%032x", new BigInteger(1, message)));
            }
        }
        Hmac hmac = new Hmac();
        if(hmac.compareHmac(Keys.KEY, encmessage.getBytes(Charset.defaultCharset()), message)){
            System.out.println("Second Hmac checks up, advancing...");
            SessionKey = Aes256.decrypt(Aes256.convertSecretKeyToString(originalKeyToUse),
                    encmessage);

            return true;
        }
        return false;
    }
    public String getSessionKey(){
        return SessionKey;
    }
}
