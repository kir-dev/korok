package hu.sch.api.filter;

/**
 *
 * @author tomi
 */
public enum RequestSignatureResult {
    /**
     * Missing signature.
     */
    MISSING,

    /**
     * Invalid signature.
     */
    INVALID,

    /**
     * Accpeted signature.
     */
    OK,

    /**
     * Signature is too old.
     */
    STALE
}
