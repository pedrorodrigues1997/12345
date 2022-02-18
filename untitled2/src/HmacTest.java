import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class HmacTest {
    Hmac hmac;

    private static final byte[] key = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private static final byte[] CDRIVES = new byte[]{(byte) 0x68, (byte) 0x65, (byte) 0x6c, (byte) 0x6c
            , (byte) 0x6f, (byte) 0x69, (byte) 0x61, (byte) 0x6d, (byte) 0x6b, (byte) 0x65, (byte) 0x78};

    private static final byte[] test = new byte[]{(byte) 0x68, (byte) 0x65, (byte) 0x6c, (byte) 0x6c
            , (byte) 0x6f, (byte) 0x69, (byte) 0x61, (byte) 0x6d, (byte) 0x6b, (byte) 0x65, (byte) 0x79};


    @Before
    public void setup() {
        hmac = new Hmac();
    }

    @Test
    public void testHmacEncoder() {
        byte[] hmacSha256 = Hmac.encodeHmac("secret123".getBytes(StandardCharsets.UTF_8), "hello world".getBytes(StandardCharsets.UTF_8));
        System.out.printf("Hex: %032x%n", new BigInteger(1, hmacSha256));
        Assert.assertEquals("57938295649097379cddb382dd6c82d5e0460645a8fd01674a48a76de6142646", String.format("%032x", new BigInteger(1, hmacSha256)));
    }

    @Test
    public void testHmacCompare() {
        byte[] hmacSha256 = Hmac.encodeHmac("helloiamkey".getBytes(StandardCharsets.UTF_8), "helloiamkey".getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(hmac.compareHmac(test, test, hmacSha256));
        Assert.assertFalse(hmac.compareHmac(CDRIVES, test, hmacSha256));
    }

    @Test
    public void testHmacCompare2() {
        byte[] hmacSha256 = Hmac.encodeHmac(key, "12345".getBytes(StandardCharsets.UTF_8));
        System.out.printf("Hex: %032x%n", new BigInteger(1, hmacSha256));
        Assert.assertTrue(hmac.compareHmac(key, "152399025".getBytes(StandardCharsets.UTF_8), hmacSha256));
        Assert.assertFalse(hmac.compareHmac(CDRIVES, test, hmacSha256));
    }

    @Test
    public void newTest() {
        for (int i = 0; i < 16; i++) {
            key[i] = (byte) (key[i] * 2);
        }
        System.out.println(Arrays.toString(key));
        //String str = new String(key, StandardCharsets.UTF_8);
        System.out.println(String.format("%032x", new BigInteger(1, key)));
        System.out.println("Hello World12345".getBytes(StandardCharsets.UTF_8));



    }

}
