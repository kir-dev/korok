package hu.sch.util.hash;

import org.apache.commons.codec.binary.Base64;
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

    public String toBase64() {
        hashedValue = compute();
        return Base64.encodeBase64String(hashedValue);
    }

    protected abstract byte[] compute();
}
