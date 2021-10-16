package sockets;

import javafx.util.Pair;
import security.Hmac;
import utils.Keys;
import utils.utils1;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class ProcessesReceive {

    private Socket socket;
    private DataInputStream input;
    private OutputStream out;
    private ProcessesSend pSend;
    int Id = 0;

    public ProcessesReceive(Socket socket, DataInputStream input, OutputStream out) {
        this.socket = socket;
        this.input = input;
        this.out = out;
        pSend = new ProcessesSend(socket, out, input);
    }

    public void run1() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException {
        if(receiveData1()){
            pSend.run1(Id);
            if(receiveData2()){
                pSend.run2();

                ProcessContinuousComm comm = new ProcessContinuousComm(out, pSend.SessionKey, input);
                comm.run();
            }
        }else{
            System.out.println("First Hmac did not match the expected one...");
            System.out.println("Terminating");
            return;
        }
    }

    private boolean receiveData1() throws IOException {
        byte[] idFromSocket;
        byte[] message = null;
        for(int i = 0; i < 2; i++){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            baos.write(buffer, 0 , input.read(buffer));
            if(i == 0){
                idFromSocket = baos.toByteArray();
                Id = (int) utils1.bytesToLong(idFromSocket);
                System.out.println("Received from client the Id : " + Id);
            }
            if(i == 1){
                message = baos.toByteArray();
                System.out.println("Received from client the message : " + String.format("%032x", new BigInteger(1, message)));
            }
        }
        Hmac hmac = new Hmac();
        if(hmac.compareHmac(Keys.KEY, utils1.longToBytes(Id), message)){
            System.out.println("First Hmac checks up, advancing towards second message");
            return true;
        }
    return false;
    }

    private boolean receiveData2() throws IOException {

        byte[] message = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            baos.write(buffer, 0 , input.read(buffer));
                message = baos.toByteArray();
                System.out.println("Received from client the message : " + String.format("%032x", new BigInteger(1, message)));


        Hmac hmac = new Hmac();
        Pair<byte[], byte[]> pairOfKeys = Keys.MapKeys.get(Id);
        if(hmac.compareHmac(Keys.KEY, pairOfKeys.getValue(), message)){
            System.out.println("Second Hmac checks up, advancing towards second message");
            return true;
        }
        return false;
    }




}
