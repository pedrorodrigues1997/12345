import arduino.Arduino;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Engine {
    private static Hmac hmac;
    private static byte[] hmacKey = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private static byte[] hmacKey2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private static byte[] secretKeyToSend = {4, 1, 6, 9, 4, 5, 3,9, 8, 9, 12, 11, 12, 53, 34, 45,
            4, 1, 6, 9, 4, 5, 3,9, 8, 9, 12, 11, 12, 53, 34, 45};

    public static byte[] multiplyValues(byte[] array, int multiplier) {

        byte[] newArray = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = (byte) (array[i] * multiplier);
        }
        return newArray;
    }
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
    public static String bytesToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString();
    }

    public static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    private static boolean sendDataFirst(Arduino port){
        int ts2 = 2;
        byte[] hmacToSend = Hmac.encodeHmac(hmacKey, multiplyValues(hmacKey2, ts2));
        System.out.println("Server sends:");
        System.out.println("HMAC :"
                + bytesToHexString(hmacToSend));
        System.out.println("Timestamp :"
                + ts2);
        System.out.println("--------------------------------------------------------");
        port.serialWrite("Timestamp:" + (char) (ts2+'0'));
        port.serialWrite("HMAC:");
        port.serialWrite(bytesToHexString(hmacToSend));

        return true;

    }
    private static boolean handleData(String readPort){
        System.out.println("---------------------------Server-----------------------");
        int IdIndex = readPort.indexOf("Id:");
        int TsIndex = readPort.indexOf("Timestamp:");
        int HmacIndex = readPort.indexOf("HMAC:");
        long Id = Long.parseLong(readPort.substring(IdIndex +3, IdIndex + 8));
        long Ts = Long.parseLong(readPort.substring(TsIndex + 10, TsIndex + 11));
        String HMAC = readPort.substring(HmacIndex + 5, HmacIndex + 69);
        Long message = Id*Ts;

        byte[] bytearray1 = message.toString().getBytes(StandardCharsets.UTF_8);

        if(hmac.compareHmac(hmacKey, bytearray1, HMAC)){
            System.out.println("HMACs from Arduino matches the calculated one --------------- Moving for the next Step");
            return true;
        }

        return false;

    }

    private static boolean handleDataLast(String readPort){
        System.out.println("---------------------------Server-----------------------");
        int TsIndex = readPort.indexOf("Timestamp:");
        int HmacIndex = readPort.indexOf("HMAC:");
        int ts3 = Integer.parseInt(readPort.substring(TsIndex + 10, TsIndex + 11));
        String HMAC = readPort.substring(HmacIndex + 5, HmacIndex + 69);
        byte[] bytearray2 = multiplyValues(hmacKey2, ts3);
        if(hmac.compareHmac2(hmacKey, bytearray2, HMAC)){
            System.out.println("HMACs from Arduino Calculated match --------------- Moving for the next Step");
            return true;
        }

        return false;

    }

    private static void encryptAndSend(Arduino port) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Aes encryption = new Aes();
        String encryptedKey = encryption.encrypt();
        int ts4 = 4;
        byte[] hmacToSend2 = Hmac.encodeHmac(hmacKey, encryptedKey.getBytes(StandardCharsets.UTF_8));
        System.out.println("Server Sends:");
        System.out.println("HMAC :"+ bytesToHexString(hmacToSend2).toUpperCase(Locale.ROOT));
        System.out.println("TimeStamp :"+ ts4);
        System.out.println("Cipher:"+ encryptedKey);



        port.serialWrite("CIPHER:" + encryptedKey);
        port.serialWrite("Timestamp:" + (char) (ts4+'0'));
        port.serialWrite("HMAC:" + bytesToHexString(hmacToSend2));
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Arduino port = new Arduino("COM4", 115200);
        hmac = new Hmac();
        port.openConnection();

       String readPort = getReadPort(port);
        System.out.println(readPort);
        if(handleData(readPort)){
            sendDataFirst(port);
            String readPort2 = getReadPort(port);
            System.out.println(readPort2);
            String readPort3 = getReadPort(port);
            System.out.println(readPort3);
            if(handleDataLast(readPort3)){
                encryptAndSend(port);
            }
            String readPort4 = getReadPort(port);
            System.out.println(readPort4);
            String readPort5 = getReadPort(port);
            System.out.println(readPort5);


        }else{
            System.out.println("HMAC from Arduino didn't match with the correct one ----------------------- Aborting");
        }

    }

    private static String getReadPort(Arduino port) {
        String readPort = port.serialRead();
        while(readPort.isEmpty()){
            readPort = port.serialRead();
        }
        return readPort;
    }
}
