package com.moleculepowered.api.console.enums;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public enum ConsoleColor
{
    // STANDARD COLORS
    DARK_RED("\u001b[31m", ChatColor.DARK_RED, "#AA0000"),
    RED("\u001b[31;1m", ChatColor.RED, "#FF5555"),
    GOLD("\u001b[33m", ChatColor.GOLD, "#FFAA00"),
    YELLOW("\u001b[33;1m", ChatColor.YELLOW, "#FFFF55"),
    DARK_GREEN("\u001b[32m", ChatColor.DARK_GREEN, "#00AA00"),
    GREEN("\u001b[32;1m", ChatColor.GREEN, "#55FF55"),
    AQUA("\u001b[36;1m", ChatColor.AQUA, "#55FFFF"),
    DARK_AQUA("\u001b[36m", ChatColor.DARK_AQUA, "#00AAAA"),
    DARK_BLUE("\u001b[34m", ChatColor.DARK_BLUE, "#0000AA"),
    BLUE("\u001b[34;1m", ChatColor.BLUE, "#5555FF"),
    LIGHT_PURPLE("\u001b[35;1m", ChatColor.LIGHT_PURPLE, "#FF55FF"),
    DARK_PURPLE("\u001b[35m", ChatColor.DARK_PURPLE, "#AA00AA"),
    WHITE("\u001b[37m", ChatColor.WHITE, "#FFFFFF"),
    GRAY("\u001b[30", ChatColor.GRAY, "#AAAAAA"),
    DARK_GRAY("\u001b[30;1m", ChatColor.DARK_GRAY, "#555555"),
    BLACK("\u001b[30m", ChatColor.BLACK, "#000000"),

    // DECORATIONS
    RESET("\u001b[0m", ChatColor.RESET),
    BOLD("\u001b[1m", ChatColor.BOLD),
    OBFUSCATED(null, ChatColor.MAGIC),
    UNDERLINE("\u001b[4m", ChatColor.UNDERLINE),
    STRIKETHROUGH("\u001b[29m", ChatColor.STRIKETHROUGH);

    // COLOR CODE TYPES
    private final String consoleCode;
    private final String hexCode;
    private final ChatColor bukkitColor;

    /*
    CONSTRUCTORS
     */

    @Contract(pure = true)
    ConsoleColor(String consoleCode, ChatColor bukkitColor) {
        this.consoleCode = consoleCode;
        this.bukkitColor = bukkitColor;
        this.hexCode = null;
    }

    @Contract(pure = true)
    ConsoleColor(String consoleCode, ChatColor bukkitColor, String hexCode) {
        this.consoleCode = consoleCode;
        this.bukkitColor = bukkitColor;
        this.hexCode = hexCode;
    }

    /*
    CODE GETTERS
     */

    /**
     * Used to return the ASCII code assigned to this {@link ConsoleColor}.
     *
     * @return the assigned ASCII color code.
     */
    @Contract(pure = true)
    public String toConsoleColor() { return consoleCode; }

    /**
     * Used to return the bukkit color code assigned to this {@link ConsoleColor}.
     *
     * @return the assigned bukkit color code.
     */
    @Contract(pure = true)
    private String toBukkitCode() { return bukkitColor.toString(); }

    /**
     * Used to return the hex color code assigned to this {@link ConsoleColor}.
     *
     * @return the assigned hex color code.
     */
    @Contract(pure = true)
    private String toHexCode() { return hexCode; }

    /*
    GLOBAL UTILITIES
     */

    /**
     * <p>Used to translate and replace bukkit color codes into valid {@link ConsoleColor}'s.</p>
     *
     * @param input Provided input
     * @return A reformatted input that includes {@link ConsoleColor}'s
     */
    public static @NotNull String translateColorCodes(String input) {
        if (input == null) return "";

        // NORMALIZE POTENTIAL COLORS
        input = input.replace("&", "§");
        input = input.replace("\\u00A7", "§");

        for (ConsoleColor color : values()) {
            if (isConvertable(color.toBukkitCode(), input)) input = input.replace(color.toBukkitCode(), color.toConsoleColor());
            if (isConvertable(color.toHexCode(), input)) input = input.replace(color.toHexCode(), color.toConsoleColor());
        }
        return input + ConsoleColor.RESET.toConsoleColor();
    }

    /**
     * Used to translate and replace bukkit color codes into valid {@link ConsoleColor}'s. for a
     * collection of strings
     *
     * @param input Provided input
     * @return A reformatted input that includes {@link ConsoleColor}'s
     */
    public static @NotNull Collection<String> translateColorCodes(@NotNull Collection<String> input) {
        return input.stream().map(ConsoleColor::translateColorCodes).collect(Collectors.toList());
    }

    /**
     * <p>Used to wrap an input with the provided color, this method by default will add the
     * reset color at the end of the input to prevent color leakage (where the color extends
     * to the rest of the output).</p>
     *
     * <p>Please note that this method will not translate any color codes provided within the input,
     * in order to translate codes dynamically, please consider using the
     * {@link #translateColorCodes(String)} method.</p>
     *
     * @param color Target color
     * @param input Provided input
     * @return An input wrapped with the target color
     * @see ConsoleColor
     */
    public static @NotNull String wrap(@NotNull ConsoleColor color, @NotNull String input) {
        if (color.toConsoleColor() == null) return input;
        return translateColorCodes(color.toConsoleColor() + input.replace(" ", " " + color.toConsoleColor()) + RESET.toConsoleColor());
    }

    /**
     * <p>Used to return whether the provided input contains the provided color code.</p>
     *
     * <p>This method features a null check to prevent unnecessary exceptions and it checks
     * to see if the provided input contains the code, please note that both of these
     * conditions must be met (return true) in-order for this method to pass its check.</p>
     *
     * @param code  Target color code
     * @param input Provided input
     * @return true if the input is convertable.
     */
    @Contract(value = "null, _ -> false", pure = true)
    private static boolean isConvertable(String code, @NotNull String input) { return code != null && input.contains(code); }
}
