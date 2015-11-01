package behnen.julia.makeyourownadventure.model;

import java.io.UnsupportedEncodingException;

/**
 * Created by Julia on 10/31/2015.
 */
public class Helper {

    private static final int HASH_MULTIPLIER = 37;
    private static final int HASH_MOD = 128;

    public static String hashPassword(String password) {
        byte[] bytes = password.getBytes();
        if (bytes.length > 0) {
            bytes[0] *= (HASH_MULTIPLIER*127);
            bytes[0] %= HASH_MOD;
            for (int i = 1; i < bytes.length; i++) {
                bytes[i] *= (HASH_MULTIPLIER * bytes[i-1]);
                bytes[i] %= HASH_MOD;
            }
        }
        String output;
        try {
            output = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            output = "";
        }
        return output;
    }

}
