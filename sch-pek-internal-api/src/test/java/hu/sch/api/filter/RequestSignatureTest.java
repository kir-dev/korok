package hu.sch.api.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author tomi
 */
public class RequestSignatureTest {

    private static final String PATH = "/foo";
    private static final String SECRET = "secret";
    private static final String BODY = "body";
    private Calendar cal;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void secretCannotBeEmpty() {
        thrown.expect(IllegalArgumentException.class);
        new RequestSignature(null, null, null, 0L, null);
    }

    @Test
    public void nullSignatureYieldsMissingResult() {
        RequestSignature sig = new RequestSignature(null, null, null, 0L, SECRET);
        assertEquals(RequestSignatureResult.MISSING, sig.checkSignature());
    }

    @Test
    public void emptySignatureYieldsMissingResult() {
        RequestSignature sig = new RequestSignature(null, null, "", 0L, SECRET);
        assertEquals(RequestSignatureResult.MISSING, sig.checkSignature());
    }

    @Test
    public void timestampMustNotBeOlderThan5Seconds() {
        cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 6 seconds ago
        cal.add(Calendar.SECOND, -6);
        long timestamp = cal.getTimeInMillis() / 1000L;

        RequestSignature sig = new RequestSignature(null, null, "dummy", timestamp, SECRET);
        assertEquals(RequestSignatureResult.STALE, sig.checkSignature());
    }

    @Test
    public void rejectTimestampsFromTheFuture() {
        cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 6 seconds from now
        cal.add(Calendar.SECOND, 6);
        long timestamp = cal.getTimeInMillis() / 1000L;

        RequestSignature sig = new RequestSignature(null, null, "dummy", timestamp, SECRET);
        assertEquals(RequestSignatureResult.STALE, sig.checkSignature());
    }

    @Test
    public void invalidSignature() {
        RequestSignature sig = new RequestSignature(PATH, null, "dummy signature", createTimestamp(), SECRET);
        assertEquals(RequestSignatureResult.INVALID, sig.checkSignature());
    }

    @Test
    public void validSignature() {
        RequestSignature sig = new RequestSignature(PATH, BODY.getBytes(StandardCharsets.UTF_8), createSignature(PATH, BODY), createTimestamp(), SECRET);
        assertEquals(RequestSignatureResult.OK, sig.checkSignature());
    }

    @Test
    public void validSignatureWithoutBody() {
        RequestSignature sig1 = new RequestSignature(PATH, null, createSignature(PATH, null), createTimestamp(), SECRET);
        RequestSignature sig2 = new RequestSignature(PATH, new byte[0], createSignature(PATH, ""), createTimestamp(), SECRET);

        assertEquals(RequestSignatureResult.OK, sig1.checkSignature());
        assertEquals(RequestSignatureResult.OK, sig2.checkSignature());
    }

    private String createSignature(String url, String body) {
        // TODO: it would be nice if the signature calculation's implementation does not leaked out here
        // maybe we need to introduce a new class for this?
        try {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            s.write(url.getBytes(StandardCharsets.UTF_8));
            if (body != null) {
                s.write(body.getBytes(StandardCharsets.UTF_8));
            } else {
                s.write(new byte[0]);
            }
            s.write(String.valueOf(System.currentTimeMillis() / 1000L).getBytes(StandardCharsets.UTF_8));
            s.write(SECRET.getBytes(StandardCharsets.UTF_8));

            final String algo = "HmacSHA1";
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), algo);
            Mac hmac = Mac.getInstance(algo);
            hmac.init(key);
            return Hex.encodeHexString(hmac.doFinal(s.toByteArray()));
        } catch (Exception ex) {
        }
        return "";
    }

    private long createTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}
