package Sockets;

import Files.Keys;
import Hmac.Hmac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import Hmac.utils;
import javafx.util.Pair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ProcessesSend {
    private String SessionKey = null;
    private DataOutputStream out;
    private DataInputStream input;
    private int Id = 1;
    private ProcessesRecv receiveData;
    private Hmac hmac = new Hmac();

    public ProcessesSend(Socket socket, DataOutputStream out, DataInputStream input) {
        this.out = out;
        this.input = input;
        receiveData = new ProcessesRecv(socket, input);
    }

    public void run() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException {

        firstStepSend(Keys.KEY);
        if(receiveData.run1(Id)){ //First time Client Receives Data
            secondStepSend(Keys.KEY);
            if(receiveData.run2()){
                System.out.println("Server and Client Authenticated");
                SessionKey = receiveData.getSessionKey();
                ProcessContinuousComm comm = new ProcessContinuousComm(out, SessionKey, input);
                comm.run();
            }
        }else{
            System.out.println("Failed to Receive data from server");
        }

    }


    protected byte[] firstStepSend(byte[] secretKeyPk) throws IOException {
        //Criar HMAC

        byte[] hmac1 = Hmac.encodeHmac(secretKeyPk, utils.longToBytes(Id));
        out.write(Id);
        out.write(hmac1);
        System.out.println("Sent: " + hmac.convertHmacToHexString(hmac1));
        return hmac1;
    }

    protected byte[] secondStepSend(byte[] secretKeyPk) throws IOException {
        Pair<byte[], byte[]> pairOfKeys = Keys.MapKeys.get(Id);
        byte[] hmac1 = Hmac.encodeHmac(secretKeyPk, pairOfKeys.getValue());
        out.write(hmac1);
        System.out.println("Sent to server: " + hmac.convertHmacToHexString(hmac1));
        return hmac1;
    }



}
