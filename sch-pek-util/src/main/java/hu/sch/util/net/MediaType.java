package hu.sch.util.net;

import java.util.Objects;
import java.util.StringTokenizer;
import org.apache.commons.lang3.Validate;

/**
 * Simple class to abstract away from pure string media types
 *
 * @author tomi
 */
public final class MediaType {

    private static final String IMAGE_TYPE = "image";
    private static final String TEXT_TYPE = "text";
    public static final MediaType TEXT_PLAIN = new MediaType(TEXT_TYPE, "plain");
    public static final MediaType TEXT_HTML = new MediaType(TEXT_TYPE, "html");
    public static final MediaType IMAGE_JPEG = new MediaType(IMAGE_TYPE, "jpeg");
    public static final MediaType IMAGE_PNG = new MediaType(IMAGE_TYPE, "png");
    public static final MediaType IMAGE_GIF = new MediaType(IMAGE_TYPE, "gif");
    private String type;
    private String subType;

    private MediaType(String type, String subType) {
        this.type = Objects.requireNonNull(type);
        this.subType = Objects.requireNonNull(subType);
    }

    /**
     * Gets the main type. Eg. image or text
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the subtype. Eg. jpeg or plain
     */
    public String getSubType() {
        return subType;
    }

    public String getContentType() {
        return String.format("%s/%s", type, subType);
    }

    public static MediaType parse(String mediaTypeString) {
        if (mediaTypeString == null) {
            throw new IllegalArgumentException("mediaTypeString cannot be null");
        }

        StringTokenizer tokenizer = new StringTokenizer(mediaTypeString, "/");

        if (tokenizer.countTokens() != 2) {
            throw new IllegalArgumentException("Media type has more than 2 parts.");
        }
        String type = tokenizer.nextToken();
        String subType = tokenizer.nextToken();

        return new MediaType(type, subType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MediaType) {
            final MediaType other = (MediaType) obj;
            return this.type.equals(other.type)
                    && this.subType.equals(other.subType);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + Objects.hashCode(this.subType);
        return hash;
    }

    /**
     * Determines if the type is any of the given media types.
     *
     * @param mediaTypes
     * @return
     */
    public boolean isAny(MediaType... mediaTypes) {
        for (MediaType mediaType : mediaTypes) {
            if (this.equals(mediaType)) {
                return true;
            }
        }
        return false;
    }
}
