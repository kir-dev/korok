package hu.sch.api.filter;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

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
        RequestSignature sig = new RequestSignature(PATH, BODY, createSignature(PATH, BODY), createTimestamp(), SECRET);
        assertEquals(RequestSignatureResult.OK, sig.checkSignature());
    }

    @Test
    public void validSignatureWithoutBody() {
        // sig: /foo1399822115secret
        RequestSignature sig1 = new RequestSignature(PATH, null, createSignature(PATH, null), createTimestamp(), SECRET);
        RequestSignature sig2 = new RequestSignature(PATH, "", createSignature(PATH, ""), createTimestamp(), SECRET);

        assertEquals(RequestSignatureResult.OK, sig1.checkSignature());
        assertEquals(RequestSignatureResult.OK, sig2.checkSignature());
    }

    private String createSignature(String url, String body) {
        StringBuilder sb = new StringBuilder(url);
        if (body != null) {
            sb.append(new String(body));
        }
        sb.append(System.currentTimeMillis() / 1000L).append(SECRET);

        final String algo = "HmacSHA1";
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), algo);
            Mac hmac = Mac.getInstance(algo);
            hmac.init(key);
            return Hex.encodeHexString(hmac.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
        }
        return "";
    }

    private long createTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}