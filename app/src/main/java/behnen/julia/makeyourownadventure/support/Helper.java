package behnen.julia.makeyourownadventure.support;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Julia on 10/31/2015.
 */
public class Helper {

    private static final int HASH_MULTIPLIER = 37;
    private static final int HASH_MOD = 128;

    public static String hashPassword(String password) {
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 0) {
            bytes[0] *= (HASH_MULTIPLIER*127);
            bytes[0] %= HASH_MOD;
            for (int i = 1; i < bytes.length; i++) {
                bytes[i] *= (HASH_MULTIPLIER * bytes[i-1]);
                bytes[i] %= HASH_MOD;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
