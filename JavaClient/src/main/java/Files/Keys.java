package Files;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Keys {

    public static final byte[] KEY = new byte[]{(byte) 0x63, (byte) 0x25, (byte) 0xCF, (byte) 0x62
            , (byte) 0x24, (byte) 0xCE, (byte) 0xEA, (byte) 0x61, (byte) 0x27, (byte) 0xCD, (byte) 0xE9
            , (byte) 0x60, (byte) 0x26, (byte) 0xCC, (byte) 0xE8, (byte) 0x67, (byte) 0x21, (byte) 0xCB
            , (byte) 0xEF, (byte) 0x66, (byte) 0x20, (byte) 0xCA, (byte) 0xEE, (byte) 0x65, (byte) 0x23
            , (byte) 0xC9, (byte) 0xED, (byte) 0x64, (byte) 0x22, (byte) 0xC8, (byte) 0xEC, (byte) 0xEC
    };

    public static final byte[] KEY2 = new byte[]{(byte) 0x63, (byte) 0x25, (byte) 0xCF, (byte) 0xEB
            , (byte) 0x62, (byte) 0x24, (byte) 0xCE, (byte) 0xEA, (byte) 0x61, (byte) 0x27, (byte) 0xCD
            , (byte) 0xE9, (byte) 0x60, (byte) 0x26, (byte) 0xCC, (byte) 0xE8, (byte) 0x67, (byte) 0x21
            , (byte) 0xCB, (byte) 0xEF, (byte) 0x66, (byte) 0x20, (byte) 0xCA, (byte) 0xEE, (byte) 0x65
            , (byte) 0x23, (byte) 0xC9, (byte) 0xED, (byte) 0x64, (byte) 0x22, (byte) 0xC8
    };

    public static final byte[] CHALLENGE_ID1= new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x4C, (byte) 0xE3, (byte) 0x87, (byte) 0x0A, (byte) 0xFF
    };

    public static Map<Integer, byte[]> MapIdChallenge;
    static {
        MapIdChallenge = new HashMap<>();
        MapIdChallenge.put(1, CHALLENGE_ID1);
    }

    private static Pair<byte[], byte[]> Keys = new Pair<>(KEY, KEY2);

    public static Map<Integer, Pair> MapKeys;
    static {
        MapKeys = new HashMap<>();
        MapKeys.put(1, Keys);
    }

}





