package com.moleculepowered.api;

import java.util.regex.Pattern;

public final class Regex
{
    public static final String VERSION      = "(\\d+).(\\d+)(.(\\d+))?(.(\\d+))?";
    public static final String VERSION_TYPE = "((?i)(BETA|ALPHA|PRE|SNAPSHOT|RELEASE|B|A|R))";
    public static final String TIME_UNIT    = "((?i)(second(s)?|minute(s)?|hour(s)?|day(s)?|week(s)?|month(s)?|year(s)?|s|m|h|d|wk|mo|y))";
    public static final Pattern COLOR_HEX   = Pattern.compile("&#(\\w{5}[0-9a-f])");
}
