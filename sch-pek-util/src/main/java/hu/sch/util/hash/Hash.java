package hu.sch.util.hash;

import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author tomi
 */
public abstract class Hash {

    protected byte[] sourceValue;
    private byte[] hashedValue;

    public Hash(byte[] soureValue) {
        this.sourceValue = soureValue;
    }

    public Hash(String source) {
        this(source.getBytes());
    }

    public String toHex() {
        hashedValue = compute();
        return Hex.encodeHexString(hashedValue);
    }
    protected abstract byte[] compute();
}
