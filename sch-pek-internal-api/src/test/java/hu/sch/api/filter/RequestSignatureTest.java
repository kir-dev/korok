package hu.sch.api.filter;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.TimeZone;
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
    private static final byte[] BODY = "body".getBytes(StandardCharsets.UTF_8);
    private Calendar cal;
    private final Signature signature = new Signature(SECRET);
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
        final long timestamp = createTimestamp();
        RequestSignature sig = new RequestSignature(PATH, BODY, signature.calculateHex(PATH, timestamp, BODY), timestamp, SECRET);
        assertEquals(RequestSignatureResult.OK, sig.checkSignature());
    }

    @Test
    public void validSignatureWithoutBody() {
        final long timestamp = createTimestamp();
        final byte[] emptyBody = "".getBytes(StandardCharsets.UTF_8);
        RequestSignature sig1 = new RequestSignature(PATH, null, signature.calculateHex(PATH, timestamp, null), timestamp, SECRET);
        RequestSignature sig2 = new RequestSignature(PATH, emptyBody, signature.calculateHex(PATH, timestamp, emptyBody), timestamp, SECRET);

        assertEquals(RequestSignatureResult.OK, sig1.checkSignature());
        assertEquals(RequestSignatureResult.OK, sig2.checkSignature());
    }

    private long createTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}
