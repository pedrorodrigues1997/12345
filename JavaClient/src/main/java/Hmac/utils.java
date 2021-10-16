package Hmac;

import java.nio.ByteBuffer;

public class utils {
    private utils() {
        throw new UnsupportedOperationException();
    }
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    public static long bytesToLong(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            value += ((long) bytes[i] & 0xffL) << (8 * i);
        }
        return value;
    }
}
