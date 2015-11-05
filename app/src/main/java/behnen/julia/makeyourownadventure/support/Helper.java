package behnen.julia.makeyourownadventure.support;

/**
 * Static helper methods shared between multiple classes.
 *
 * @author Julia Behnen
 * @version November 3, 2015
 */
public class Helper {

    /**
     * Encrypts and returns a string.
     *
     * The string is encrypted the same way each time and uses UTF-8 encoding.
     *
     * @param string The string to be encrypted.
     * @return The encrypted string.
     */
    public static String encryptPassword(String string) {
        final int multiplier = 37;
        final int mod = 128;

        byte[] bytes = string.getBytes();
        if (bytes.length > 0) {
            bytes[0] *= (multiplier *127);
            bytes[0] %= mod;
            for (int i = 1; i < bytes.length; i++) {
                bytes[i] *= (multiplier * bytes[i-1]);
                bytes[i] %= mod;
            }
        }
        return new String(bytes);
    }

}
