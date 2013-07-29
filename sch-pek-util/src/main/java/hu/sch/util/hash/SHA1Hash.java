package hu.sch.util.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
class SHA1Hash extends Hash {

    private static final Logger logger = LoggerFactory.getLogger(SHA1Hash.class);

    public SHA1Hash(byte[] soureValue) {
        super(soureValue);
    }

    public SHA1Hash(String source) {
        super(source);
    }

    @Override
    protected byte[] compute() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return digest.digest(sourceValue);
        } catch (NoSuchAlgorithmException ex) {
            // NOTE: this should not happen on a normal system
            logger.error("Could not find SHA-1 hash algorithm.", ex);
        }

        return null;
    }
}
