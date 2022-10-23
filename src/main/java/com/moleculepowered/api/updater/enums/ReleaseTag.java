package com.moleculepowered.api.updater.enums;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ReleaseTag
{
    /**
     * <p>This value marks this release as a alpha release, typically an alpha
     * release is distributed before its {@link ReleaseTag#BETA} counterpart.</p>
     *
     * <p>Assigning this value to your version tells your users that this build
     * is expected to contain some major bugs and its recommended that they use
     * with precaution</p>
     */
    ALPHA("alpha", "a"),
    /**
     * <p>This value marks this release as the beta release, typically, this release
     * follows after an {@link ReleaseTag#ALPHA} build.</p>
     *
     * <p>Assigning this value to your version tells your user's that this
     * build is closer to the final release and may contain some minor bugs
     * within its programming, though its generally expected that there will
     * be a minimal amount of major bugs.</p>
     */
    BETA("beta", "b"),
    /**
     * <p>This value marks this release as a pre-release, typically this follows after the
     * {@link #BETA} version.</p>
     *
     * <p>Assigning this value to your version tells your user's that this build is in
     * its last lest before a full release. Typically this version can be used to onboard
     * user's to test the update before release.</p>
     */
    PRE("pre-release", "pre", "pr", "p"),
    /**
     * This value marks this release as the final release version, this release follows
     * after its {@link ReleaseTag#BETA} build.
     *
     * <p>Generally, assigning this release flag tells your user's that virtually all
     * bugs previously found in its previous versions, {@link #ALPHA} and {@link #BETA}
     * have been ironed out and fixed. Typically this version is considered the safest
     * version out of the others already mentioned.</p>
     */
    RELEASE("release", "rc", "r");

    // ENUM OBJECTS
    private final List<String> alts;

    @Contract(pure = true)
    ReleaseTag(String... alt) { this.alts = Arrays.stream(alt).collect(Collectors.toList()); }

    /**
     * Used to return a list of available alternate identifiers.
     *
     * @return a list of alternate identifiers
     */
    @Contract(pure = true)
    private List<String> getAlt() { return alts; }

    /**
     * Used to convert the provided string into a valid {@link ReleaseTag}. If a valid
     * release type cannot be parsed from the string, this method will return the default
     * {@link ReleaseTag#RELEASE}.
     *
     * @param type Target type
     * @return A valid release type.
     */
    public static ReleaseTag parse(String type) {
        for (ReleaseTag current : ReleaseTag.values()) {
            if (current.getAlt().contains(type.toLowerCase())) return current;
        }
        return RELEASE;
    }

    /**
     * Used to compare two release tags and return whether they are equal.
     *
     * @param expected Expected release tag
     * @param actual The actual release tag
     * @return true if both objects are equal
     */
    public static boolean equals(ReleaseTag expected, String actual) { return expected == parse(actual); }
}
