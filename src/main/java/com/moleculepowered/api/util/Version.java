package com.moleculepowered.api.util;

import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * A utility class designed to handle all tasks related to version support and nms configuring.
 * This class provides a variety to test whether a version is supported (either the server's or
 * a user's minecraft client). It pairs with libraries such as ProtocolLib, ViaVersion, etc
 * to assist with nms configuration.
 *
 * @author OMGitzFROST
 * @implNote All usable methods in this class should be accessed as static objects.
 */
public final class Version
{
    /*
     * COMPARING VERSIONS
     */

    /**
     * A method used to return whether two versions are equal to each other, if these 2 values are
     * different, this method will return false.
     *
     * @param expected Primary version
     * @param actual   Compared version
     * @return true if both versions are equal
     */
    public static boolean isEqual(Object expected, Object actual) {
        return compare(expected, actual) == 0;
    }

    /**
     * A method used to return whether the main compared value is greater than the compared to value.
     * The compared parameter is the value being tested against the compared to value for example
     * <p>
     * <b>Rationalizing - is compared is greater than comparedTo?</b>
     *
     * @param expected Primary value being tested
     * @param actual   The value being tested against
     * @return true if the primary value is greater
     */
    public static boolean isGreater(Object expected, Object actual) {
        return compare(expected, actual) > 0;
    }

    /**
     * A method used to return whether the main compared value is greater than or equal to the compared to value.
     * The compared parameter is the value being tested against the compared to value for example.
     * <p>
     * <b>Rationalizing - is compared is greater than or equal to comparedTo?</b>
     *
     * @param expected Primary value being tested
     * @param actual   The value being tested against
     * @return true if the primary value is greater or equal
     */
    public static boolean isGreaterOrEqual(Object expected, Object actual) {
        return isGreater(expected, actual) || isEqual(expected, actual);
    }

    /**
     * A method used to return whether the main compared value is less than the compared to value.
     * The compared parameter is the value being tested against the compared to value for example
     * <p>
     * <b>Rationalizing - is compared is less than comparedTo?</b>
     *
     * @param expected Primary value being tested
     * @param actual   The value being tested against
     * @return true if the primary value is less
     */
    public static boolean isLess(Object expected, Object actual) {
        return compare(expected, actual) < 0;
    }

    /**
     * A method used to return whether the main compared value is less than or equal to the compared to value.
     * The compared parameter is the value being tested against the compared to value for example
     * <p>
     * <b>Rationalizing - is compared is less than or equal to comparedTo?</b>
     *
     * @param expected Primary value being tested
     * @param actual   The value being tested against
     * @return true if the primary value is less or equal
     */
    public static boolean isLessOrEqual(Object expected, Object actual) {
        return compare(expected, actual) <= 0;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param expected the expected version
     * @param actual   the actual version
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    public static int compare(Object expected, Object actual) {
        ComparableVersion d1 = new ComparableVersion(String.valueOf(expected));
        ComparableVersion d2 = new ComparableVersion(String.valueOf(actual));
        return d1.compareTo(d2);
    }
}