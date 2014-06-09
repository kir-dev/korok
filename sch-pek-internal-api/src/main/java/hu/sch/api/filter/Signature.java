package hu.sch.api.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Signature {

    private static final Logger logger = LoggerFactory.getLogger(Signature.class);
    private static final String HMAC_SHA1_ALGO = "HmacSHA1";
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private final byte[] secret;

    public Signature(String secret) {
        this.secret = secret.getBytes(ENCODING);
    }

    public byte[] calculate(String url, long timestamp, byte[] body) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret, HMAC_SHA1_ALGO);
            Mac hmac = Mac.getInstance(HMAC_SHA1_ALGO);
            hmac.init(key);
            return hmac.doFinal(createSignatureBase(url, timestamp, body));
        } catch (NoSuchAlgorithmException ex) {
            logger.error("HmacSHA1 is not supported.", ex);
        } catch (InvalidKeyException ex) {
            logger.error("Invalid key.", ex);
        }

        return new byte[0];
    }

    public String calculateHex(String url, long timestamp, byte[] body) {
        byte[] bytes = calculate(url, timestamp, body);
        return Hex.encodeHexString(bytes);
    }

    /**
     * Assembles the signature base.
     *
     * Format: request url + body + timestamp + secret
     *
     * @return
     * @throws IOException
     */
    private byte[] createSignatureBase(String url, long timestamp, byte[] body) {
        byte[] urlBytes = url.getBytes(ENCODING);
        byte[] timeStampBytes = Long.toString(timestamp).getBytes(ENCODING);

        // concatenate
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(urlBytes);
            stream.write(body);
            stream.write(timeStampBytes);
            stream.write(secret);
        } catch (IOException ex) {
            logger.error("Could not create signature base", ex);
            return new byte[0];
        }

        return stream.toByteArray();
    }

}
