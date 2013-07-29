package hu.sch.util.hash;

/**
 *
 * @author tomi
 */
public final class Hashing {

    public static Hash sha1(byte[] input) {
        return new SHA1Hash(input);
    }

    private Hashing() {
    }
}
