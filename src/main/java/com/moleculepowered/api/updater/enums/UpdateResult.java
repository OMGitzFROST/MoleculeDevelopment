package com.moleculepowered.api.updater.enums;

public enum UpdateResult
{
    /**
     * <p>When this value is returned, it means that a new update was found in
     * our remote location.</p>
     *
     * <p>Please ensure that you do not return this value as a default because
     * if this value is returned and no update was found, our updater service
     * will notify all permitted players of a non-existant update.</p>
     */
    AVAILABLE,
    /**
     * When this value is returned, it means that this updater has been disabled,
     * either by an update to the configuration key, or by command.
     */
    DISABLED,
    /**
     * When this value is returned, it means that our updater was unable to contact our
     * remote location, this can be due to an invalid url, or an invalid internet
     * connection
     */
    FAIL_CONNECTION,
    /**
     * When this value is returned, it means that the updater successfully retrieved the
     * latest version, but the version number was mis-configured and is not readable by
     * this updater.
     */
    FAIL_VERSION,
    /**
     * <p>When this value is returned, it means that no new updates were found in
     * our remote location.</p>
     *
     * <p>As a result, this value tests our update service to no preform any additional
     * tasks since no update was found.</p>
     */
    LATEST,
    /**
     * When this value is returned, it means that the result of this updater is unknown.
     * We were unable to classify this result  with any of the other {@link UpdateResult}'s
     * available.
     */
    UNKNOWN
}
