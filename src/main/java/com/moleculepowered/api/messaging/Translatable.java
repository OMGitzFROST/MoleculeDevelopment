package com.moleculepowered.api.messaging;

import org.jetbrains.annotations.Nullable;

/**
 * A function interface that provides the framework needed to start implementing
 * translatable messages
 */
@FunctionalInterface
public interface Translatable
{
    /**
     * A functional method used to return a localized string, please note that
     * this method accepts null values as its return value so handle accordingly.
     * <p>
     * In this method, only one parameter is required, the key parameter
     * is a string that either points to the path for a translation, or it
     * can also simply reflect the message without being returned
     *
     * @param key Translatable path
     * @param param Optional parameters
     * @return a localized string
     */
    @Nullable String translate(String key, Object... param);
}
