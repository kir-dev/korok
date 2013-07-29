package hu.sch.domain.user;

/**
 * It holds some information about the image that's being uploaded as a profile
 * image.
 *
 * @author tomi
 * @since 2.6
 */
public class ProfileImage {
    private String mimeType;
    private byte[] data;
    private long size;

    public ProfileImage(String mimeType, byte[] data, long size) {
        this.mimeType = mimeType;
        this.data = data;
        this.size = size;
    }

    /**
     * Gets the mime type of the file.
     *
     * @return
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets the raw data of the file.
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gets the size of the file in bytes.
     *
     * @return
     */
    public long getSize() {
        return size;
    }
}
