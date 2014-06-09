package hu.sch.api.filter;

import java.security.MessageDigest;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class RequestSignature {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RequestSignature.class);
    private static final int MAX_AGE = 5;
    private final String signature;
    private final long timestamp;
    private final String secret;
    private final byte[] body;
    private final String url;

    public RequestSignature(String url, byte[] body, String signature, long timestamp, String secret) {
        if (StringUtils.isEmpty(secret)) {
            throw new IllegalArgumentException("secret cannot be empty");
        }

        this.url = url;
        this.body = body;
        this.signature = signature;
        this.secret = secret;
        this.timestamp = timestamp;
    }

    public RequestSignatureResult checkSignature() {
        if (StringUtils.isEmpty(signature)) {
            return RequestSignatureResult.MISSING;
        }
        if (!isTimestampValid()) {
            return RequestSignatureResult.STALE;
        }

        try {
            byte[] incomingSig = Hex.decodeHex(signature.toCharArray());
            byte[] sig = new Signature(secret).calculate(url, timestamp, body);

            if (MessageDigest.isEqual(sig, incomingSig)) {
                return RequestSignatureResult.OK;
            }
        } catch (DecoderException ex) {
            logger.warn("Could not decode signautre.", ex);
        }

        return RequestSignatureResult.INVALID;
    }

    private boolean isTimestampValid() {
        long diff = (System.currentTimeMillis() / 1000L) - timestamp;
        return diff >= 0 && diff < MAX_AGE;
    }
}
