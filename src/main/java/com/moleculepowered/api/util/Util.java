package com.moleculepowered.api.util;

import com.moleculepowered.api.Regex;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public class Util
{
    /**
     * <p>Used to include the provided parameters into the provided input, please note that the input must include
     * bracketed numbers such as "{0}" to indicate the parameter at each location, 0 being the first object in the array.</p>
     *
     * <p>Example Usage:
     * <strong>format("Hello {0} my name is {1}", "Ron", "Jeff")</strong></p>
     *
     * <p>In the example provided above, the input has included to bracketed numbers, "{0}" and "{1}", each number indicates
     * the location that its assigned parameter will be in, therefore, for "{0}", the assigned value is going to be "Ron", and for
     * {1}, the assigned value is going to be "Jeff"</p>
     *
     * <p>And therefore the final result returned from this method will be <strong>"Hello Ron, my name is Jeff"</strong></p>
     *
     * @param input Provided input
     * @param param Optional Parameters
     * @return a formatted string
     */
    @Contract(pure = true)
    public static @NotNull String format(String input, Object... param) {
        return MessageFormat.format(input, param);
    }

    /**
     * Used to colorize the provided input, this method also includes a string formatter
     * that will include any provided parameters into the message using the bracketed number
     * format.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @return a colorized string
     */
    public static @NotNull String color(String input, Object... param) {
        input = input.replace("§", "&");
        input = input.replace("\\u00A7", "&");
        return format(ChatColor.translateAlternateColorCodes('&', input), param);
    }

    /**
     * Used to colorize the provided input, this method also includes a string formatter
     * that will include any provided parameters into the message using the bracketed number
     * format.
     *
     * @param input Provided input
     * @param param Optional parameters
     * @return a colorized string
     */
    @Contract("_, _ -> new")
    public static @NotNull TextComponent colorComponent(String input, Object... param) {
        input = input.replace("§", "&");
        input = input.replace("\\u00A7", "&");
        return new TextComponent(format(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', input), param));
    }

    /**
     * <p>A utility method used to convert the user input provided in our into a valid interval,
     * it will attempt to take the string and split it into our 2 required parts, the
     * "quantity" and the "interval type"</p>
     *
     * <p>The quantity is essentially the amount that the interval will assume, for example
     * in the interval "1 hour", the quantity in this example would be the 1 as it will assume
     * 1 of the quantity type "hour"</p>
     *
     * <p>The interval type is the type of duration the interval will assume, for example, using our
     * "1 hour" example, the interval type would be the word "hour"</p>
     *
     * <p><b>Default Interval is "7200000" (2 hours)</b></p>
     *
     * @param interval Target interval
     * @return a converted interval
     */
    @Contract(pure = true)
    public static long toInterval(@NotNull String interval) {
        try {
            int quantity = Integer.parseInt(interval.split(Regex.TIME_UNIT)[0].trim());
            String unitType = interval.split("\\d+")[1].trim();

            switch (unitType.toLowerCase()) {
                case "s":
                case "second":
                case "seconds":
                    return 1000L * quantity;
                case "m":
                case "minute":
                case "minutes":
                    return 60000L * quantity;
                case "h":
                case "hour":
                case "hours":
                    return 3600000L * quantity;
                case "d":
                case "day":
                case "days":
                    return 86400000L * quantity;
                case "wk":
                case "week":
                case "weeks":
                    return 604800000L * quantity;
                case "mo":
                case "month":
                case "months":
                    return 2629746000L * quantity;
                case "y":
                case "year":
                case "years":
                    return 31556952000L * quantity;
                default:
                    return 7200000L; // DEFAULT 2 HOURS
            }
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid interval provided: " + interval);
        }
    }

    /**
     * <p>A utility method used to convert the user input provided in our into a valid interval,
     * it will attempt to take the string and split it into our 2 required parts, the
     * "quantity" and the "interval type"</p>
     *
     * <p>The quantity is essentially the amount that the interval will assume, for example
     * in the interval "1 hour", the quantity in this example would be the 1 as it will assume
     * 1 of the quantity type "hour"</p>
     *
     * <p>The interval type is the type of duration the interval will assume, for example, using our
     * "1 hour" example, the interval type would be the word "hour"</p>
     *
     * <p><b>Default Interval is "72000" (2 hours)</b></p>
     *
     * @param interval Target interval
     * @return a converted interval
     * @throws java.lang.NumberFormatException Thrown when an invalid interval is provided
     */
    @Contract(pure = true)
    public static long toBukkitInterval(@NotNull String interval) { return toInterval(interval) / 50; }

    /**
     * Used to repeat the provided character up to the provided limit, this method
     * is typically used to create header borders, etc.
     *
     * @param character Target character
     * @param limit     Repeat limit
     * @return A repeating string
     */
    public static @NotNull String repeat(char character, int limit) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) builder.append(character);
        return builder.toString();
    }

    /**
     * Creates a randomized string at the max length provided, it will produce
     * a mixture of numbers and letters. A perfect use for this method is
     * for creating a secret key or passphrase.
     *
     * @param length Max length for the string
     * @return A randomized string
     */
    public static @NotNull String randomizedString(int length) {
        return RandomStringUtils.random(length, true, true);
    }
}
