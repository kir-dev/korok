package hu.sch.util;

/**
 *
 * @author tomi
 */
public class Strings {

    protected static final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Creates a hex string representation of the byte array.
     *
     * In the hex a byte is always two characters and it is lowercase.
     *
     * @param raw
     * @return
     */
    public static String hex(byte[] raw) {
        char[] hexChars = new char[raw.length * 2];
        int v;
        for (int j = 0; j < raw.length; j++) {
            v = raw[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
