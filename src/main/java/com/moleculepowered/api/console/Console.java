package com.moleculepowered.api.console;

import com.moleculepowered.api.console.enums.ConsoleColor;
import com.moleculepowered.api.messaging.Translatable;
import com.moleculepowered.api.util.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.logging.Level;

public final class Console
{
    private static Console instance;
    private final Plugin plugin;
    private Translatable i18n;

    // CONSOLE SETTINGS
    private ConsoleColor success;
    private ConsoleColor warn;
    private ConsoleColor error;
    private boolean prettyPrint = true;

    /*
    CONSTRUCTOR
     */

    /**
     * A private constructor used to instantiate the required objects needed by this class.
     *
     * @param plugin Parent plugin
     */
    @Contract(pure = true)
    private Console(Plugin plugin) { this.plugin = plugin; }

    /*
    SETTER METHODS
     */

    /**
     * Used to initialize this console, This method must be called at least once within your
     * project in-order to start logging with this class.
     *
     * @param plugin Parent plugin
     * @see #getInstance()
     */
    public static void setInstance(@NotNull Plugin plugin) {
        if (instance == null) {
            instance = new Console(plugin);
            setColorScheme(ConsoleColor.GREEN, ConsoleColor.GOLD, ConsoleColor.RED);
        }
    }

    /**
     * Used to toggle whether console colors should be displayed when sending messages, If disabled,
     * all messages sent using this class
     *
     * @param toggle the toggle that enables or disables this feature
     * @see #isPretty()
     */
    public static void setPrettyPrint(boolean toggle)                                  { getInstance().prettyPrint = toggle;        }

    /**
     * Used to set the message that will handle translating this consoles messages
     *
     * @param translator Translation handler
     * @see #isLocalized()
     */
    public static void setTranslator(Translatable translator)                            { getInstance().i18n = translator;           }

    /*
    COLOR SETTINGS
     */

    /**
     * Used to set the color that success messages will be displayed with
     *
     * @param color Success color
     */
    public static void setSuccessColor(ConsoleColor color)                             { getInstance().success = color;             }

    /**
     * Used to set the color that warning messages will be displayed with
     *
     * @param color Warning color
     */
    public static void setWarnColor(ConsoleColor color)                                { getInstance().warn = color;                }

    /**
     * Used to set the color that error messages will be displayed with
     *
     * @param color Error color
     */
    public static void setErrorColor(ConsoleColor color)                               { getInstance().error = color;               }

    /**
     * Used to update the default color scheme that will be used when sending success, warning, and error
     * messages, If you provide a null color within any of the parameters, it will automatically
     * default to {@link com.moleculepowered.api.console.enums.ConsoleColor#RESET}.
     *
     * @param success Success color
     * @param warn Warning color
     * @param error Error color
     */
    public static void setColorScheme(ConsoleColor success, ConsoleColor warn, ConsoleColor error) {
        getInstance().success = success != null ? success : ConsoleColor.RESET;
        getInstance().warn    = warn != null ? warn : ConsoleColor.RESET;
        getInstance().error   = error != null ? error : ConsoleColor.RESET;
    }

    /*
    STANDARD LOGGERS
     */

    /**
     * Used to log a message to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void log(String key, Object... param) {
        getInstance().plugin.getLogger().log(Level.INFO, getInstance().prettyPrint(key, param));
    }

    /**
     * Used to log a message to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to long a collection of method,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void log(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> log(String.valueOf(c), param));
        else log(String.valueOf(input), param);
    }

    /**
     * Used to log a message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message. Additionally, if a console color is provided, this method will
     * send the message with the target color
     *
     * @param color Target Color
     * @param key Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void log(ConsoleColor color, String key, Object... param) {
        getInstance().plugin.getLogger().log(Level.INFO, getInstance().prettyPrint(color, key, param));
    }

    /**
     * Used to log a message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message. Additionally, if a console color is provided, this method will
     * send each method in the color you gave.
     *
     * @param color Target Color
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void log(ConsoleColor color, Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> log(color, String.valueOf(c), param));
        else log(color, String.valueOf(input), param);
    }

    /**
     * Used to log a collection of messages to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void log(@NotNull Collection<String> keys, Object... param)          { keys.forEach(s -> log(s, param));          }

    /**
     * Used to log a message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void success(String key, Object... param)                            { log(getInstance().success, key, param);       }

    /**
     * Used to log a collection of messages to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void success(@NotNull Collection<String> keys, Object... param)      { keys.forEach(s -> success(s, param));      }

    /**
     * Used to log a message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void success(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> success(String.valueOf(c), param));
        else success(String.valueOf(input), param);
    }

    /**
     * Used to log a message to the console, this message will be classified as a warning message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void warn(String key, Object... param)                               { log(getInstance().warn, key, param);        }

    /**
     * Used to log a collection of messages to the console, this message will be classified as a warning message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void warn(@NotNull Collection<String> keys, Object... param)         { keys.forEach(s -> warn(s, param));         }

    /**
     * Used to log a message to the console, this message will be classified as a warning message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void warn(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> warn(String.valueOf(c), param));
        else warn(String.valueOf(input), param);
    }

    /**
     * Used to log a message to the console, this message will be classified as an error message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void error(String key, Object... param)                              { log(getInstance().error, key, param);         }

    /**
     * Used to log a collection of messages to the console, this message will be classified as an error message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void error(@NotNull Collection<String> keys, Object... param)        { keys.forEach(s -> error(s, param));        }

    /**
     * Used to log a message to the console, this message will be classified as an error message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void error(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> error(String.valueOf(c), param));
        else error(String.valueOf(input), param);
    }

    /*
    DEBUG LOGGERS
     */

    /**
     * Used to log a collection of debug message to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will colorize any color codes within the message
     *
     * @param key Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debug(String key, Object... param) {
        getInstance().plugin.getLogger().log(Level.INFO, "[DEBUG] " + getInstance().prettyPrint(key, param));
    }

    /**
     * Used to log a collection of debug messages to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to long a collection of method,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debug(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> debug(String.valueOf(c), param));
        else debug(String.valueOf(input), param);
    }

    /**
     * Used to log a debug message to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will colorize the whole message according to color provided
     *
     * @param color Target color
     * @param key Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debug(ConsoleColor color, String key, Object... param) {
        getInstance().plugin.getLogger().log(Level.INFO, "[DEBUG] " + getInstance().prettyPrint(color, key, param));
    }

    /**
     * Used to log a debug message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message. Additionally, if a console color is provided, this method will
     * send each method in the color you gave.
     *
     * @param color Target Color
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debug(ConsoleColor color, Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> debug(color, String.valueOf(c), param));
        else debug(color, String.valueOf(input), param);
    }

    /**
     * Used to log a collection of debug message to the console, this message will be classified as a standard message and
     * if pretty print is enabled, this method will colorize any color codes within the message
     *
     * @param keys Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debug(@NotNull Collection<String> keys, Object... param)        { keys.forEach(s -> debug(s, param));        }

    /**
     * Used to log a debug message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugSuccess(String key, Object... param)                       { debug(getInstance().success, key, param);     }

    /**
     * Used to log a debug message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugSuccess(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> debugSuccess(String.valueOf(c), param));
        else debugSuccess(String.valueOf(input), param);
    }

    /**
     * Used to log a collection of debug message to the console, this message will be classified as a success message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugSuccess(@NotNull Collection<String> keys, Object... param) { keys.forEach(s -> debugSuccess(s, param)); }

    /**
     * Used to log a debug message to the console, this message will be classified as a warning message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided message key
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugWarn(String key, Object... param)                          { debug(getInstance().warn, key, param);      }

    /**
     * Used to log a collection of debug message to the console, this message will be classified as a warning message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugWarn(@NotNull Collection<String> keys, Object... param)    { keys.forEach(s -> debugWarn(s, param));    }

    /**
     * Used to log a message to the console, this message will be classified as a warning message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugWarn(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> debugWarn(String.valueOf(c), param));
        else debugWarn(String.valueOf(input), param);
    }

    /**
     * Used to log a debug message to the console, this message will be classified as an error message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param key Provided message key
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugError(String key, Object... param)                         { debug(getInstance().error, key, param);       }

    /**
     * Used to log a collection of debug message to the console, this message will be classified as an error message and
     * if pretty print is enabled, this method will also print a color coded message.
     *
     * @param keys Provided message keys
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugError(@NotNull Collection<String> keys, Object... param)   { keys.forEach(s -> debugError(s, param));   }

    /**
     * Used to log a message to the console, this message will be classified as an error message and
     * if pretty print is enabled, this method will also print a color coded message. With this method, it accepts
     * any object as the input parameter, and it will determine whether it needs to log a collection of messages,
     * or if it will log a single message.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     * @see #setPrettyPrint(boolean)
     */
    public static void debugError(Object input, Object... param) {
        if (input instanceof Collection) ((Collection<?>) input).forEach(c -> error(String.valueOf(c), param));
        else error(String.valueOf(input), param);
    }

    /*
    GETTER METHODS
     */

    /**
     * Return's true if the console is color formatting its messages, otherwise, this method
     * will return false.
     *
     * @return true if color formatted
     * @see #setPrettyPrint(boolean)
     */
    @Contract(pure = true)
    public boolean isPretty()                                                          { return prettyPrint;                        }

    /**
     * Return's true if the console is localizing its messages, otherwise, this method will
     * return false.
     *
     * @return true if localized
     * @see #setTranslator(com.moleculepowered.api.messaging.Translatable)
     */
    @Contract(pure = true)
    public boolean isLocalized()                                                       { return i18n != null;                       }

    /**
     * Used to return an instance of this Console class. If an instance is not defined when this method is called
     * this method will throw an {@link java.lang.IllegalArgumentException}.
     *
     * @return an instance of the Console class
     * @see #setInstance(org.bukkit.plugin.Plugin)
     */
    @Contract(pure = true)
    public static Console getInstance() {
        Validate.notNull(instance, "You must set an instance for the console before you can use it");
        return instance;
    }

    /**
     * A utility method tasked with returning a localized string. Please note that if
     * a translator has not been set at the time this method is called, this method
     * will simply return the original message key with the parameters formatted within
     * the message.
     *
     * @param key Translatable key
     * @param param Optional parameters
     * @return either a translated string or a formatted string
     */
    private String getMessage(String key, Object... param) {
        return i18n != null ? i18n.translate(key, param) : Util.format(String.valueOf(key), param);
    }

    /*
    UTILITY METHODS
     */

    /**
     * A utility method used to configure the message to include or exclude color formatting based
     * on the {@link #prettyPrint} setting, if enabled this method will return a color coded
     * message. Otherwise, it will return the original message provided.
     *
     * @param color Target color
     * @param key Provided input
     * @param param Optional parameters
     * @return a color configured string
     * @see #setPrettyPrint(boolean)
     * @see #isPretty()
     */
    private String prettyPrint(ConsoleColor color, String key, Object... param) {
        return prettyPrint ? ConsoleColor.wrap(color, getMessage(key, param)) : getMessage(key, param);
    }

    /**
     * A utility method used to configure the message to include or exclude color formatting based
     * on the {@link #prettyPrint} setting, if enabled this method will return a color coded
     * message. Otherwise, it will return the original message provided.
     *
     * @param key Provided input
     * @param param Optional parameters
     * @return a color configured string
     * @see #setPrettyPrint(boolean)
     * @see #isPretty()
     */
    private String prettyPrint(String key, Object... param) {
        return prettyPrint ? ConsoleColor.translateColorCodes(getMessage(key, param)) : getMessage(key, param);
    }
}
