package hu.sch.api.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
    private static final String HMAC_SHA1_ALGO = "HmacSHA1";
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
            byte[] sig = calculateSignature();

            if (MessageDigest.isEqual(sig, incomingSig)) {
                return RequestSignatureResult.OK;
            }
        } catch (DecoderException ex) {
            logger.warn("Could not decode signautre.", ex);
        }


        return RequestSignatureResult.INVALID;
    }

    private byte[] calculateSignature() {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGO);
            Mac hmac = Mac.getInstance(HMAC_SHA1_ALGO);
            hmac.init(key);
            return hmac.doFinal(createSignatureBase());
        } catch (NoSuchAlgorithmException ex) {
            logger.error("HmacSHA1 is not supported.", ex);
        } catch (InvalidKeyException ex) {
            logger.error("Invalid key.", ex);
        }

        return new byte[0];
    }

    /**
     * Assembles the signature base.
     *
     * Format: request url + body + timestamp + secret
     *
     * @return
     * @throws IOException
     */
    private byte[] createSignatureBase() {
        final Charset utf8 = StandardCharsets.UTF_8;
        byte[] urlBytes = url.getBytes(utf8);
        byte[] timeStampBytes = Long.toString(timestamp).getBytes(utf8);
        byte[] secretBytes = secret.getBytes(utf8);

        // concatenate
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(urlBytes);
            stream.write(body);
            stream.write(timeStampBytes);
            stream.write(secretBytes);
        } catch (IOException ex){
            logger.error("Could not create signature base", ex);
            return new byte[0];
        }

        return stream.toByteArray();
    }

    private boolean isTimestampValid() {
        long diff = (System.currentTimeMillis() / 1000L) - timestamp;
        return diff >= 0 && diff < MAX_AGE;
    }
}
