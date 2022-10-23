package com.moleculepowered.api.updater;

import com.moleculepowered.api.Regex;
import com.moleculepowered.api.console.Console;
import com.moleculepowered.api.event.updater.UpdateCompleteEvent;
import com.moleculepowered.api.event.updater.UpdateFailedEvent;
import com.moleculepowered.api.exception.updater.InvalidVersionException;
import com.moleculepowered.api.exception.updater.UpdateFailedException;
import com.moleculepowered.api.updater.abstraction.AbstractProvider;
import com.moleculepowered.api.updater.enums.ReleaseTag;
import com.moleculepowered.api.updater.enums.UpdateResult;
import com.moleculepowered.api.util.Util;
import com.moleculepowered.api.util.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Updater
{
    private final Plugin plugin;
    private RemoteArtifact latestBuild;
    private AbstractProvider provider;
    private UpdateResult result;
    private boolean enabled;
    private boolean unstablePreferred;
    private long interval;
    private String permission;

    // CORE LIST COMPONENTS
    private final ArrayList<AbstractProvider> providerList = new ArrayList<>();
    private final ArrayList<OfflinePlayer> audienceList = new ArrayList<>();

    public Updater(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.enabled = true;
        this.result = UpdateResult.UNKNOWN;
        this.latestBuild = new RemoteArtifact(plugin.getDescription().getVersion());
        this.interval = Util.toBukkitInterval("2h");
        Console.setInstance(plugin);
    }

    /*
    IMPLEMENTATION (SCHEDULING)
     */

    /**
     * The main method called by the scheduling methods that will handle the update checks. With this
     * method you can decide whether you want the updater to execute this task async or not.
     *
     * @param async whether task will run async or not
     */
    private void initialize(boolean async) {
        Validate.noNullElements(providerList, "You must provide at least one update provider for this updater");

        if (audienceList.isEmpty()) Console.warn("You have not provided an audience for the updater");

        try {
            for (AbstractProvider provider : providerList) {
                this.provider = provider;
                provider.initialize();

                latestBuild = new RemoteArtifact(provider);
                if (Version.isLess(plugin.getDescription().getVersion(), latestBuild.getVersion())) break;
            }

            // CHECK TO SEE IF VERSIONS ARE EQUAL
            if (Version.isEqual(plugin.getDescription().getVersion(), latestBuild.getVersion())) {
                result = UpdateResult.LATEST;
                return;
            }

            // CHECK TO UNSTABLE BUILDS ARE ENABLED
            if (!isUnstablePreferred() && !ReleaseTag.equals(ReleaseTag.RELEASE, latestBuild.getVersionType())) return;

            result = UpdateResult.AVAILABLE;
            plugin.getServer().getPluginManager().callEvent(new UpdateCompleteEvent(async, this, latestBuild));
        }
        catch (SocketException | UnknownHostException ex) {
            result = UpdateResult.FAIL_CONNECTION;
            plugin.getServer().getPluginManager().callEvent(new UpdateFailedEvent(async, this));
        }
        catch (InvalidVersionException ex) {
            result = UpdateResult.FAIL_VERSION;
            plugin.getServer().getPluginManager().callEvent(new UpdateFailedEvent(async, this));
        }
        catch (IOException ex) {
            throw new UpdateFailedException("The updater failed to execute its task", ex);
        }
    }

    /**
     * The final method called within the updater chain that is tasked with gathering all the settings
     * provided and scheduling the updater for its updater checks.
     * <p>
     * Please note that this method is NOT async, therefore the task will block the main server thread
     * until its tasks are complete, its recommended to schedule the updater using the async method
     *
     * @see #scheduleAsync()
     */
    public void schedule() {
        if (isEnabled()) {
            plugin.getServer().getScheduler().runTaskTimer(plugin, () -> initialize(false), 0, getInterval());
        }
        else {
            result = UpdateResult.DISABLED;
        }
    }

    /**
     * The final method called within the updater chain that is tasked with gathering all the settings
     * provided and scheduling the updater for its updater checks.
     * <p>
     * Please note that this method
     * will run as an async method meaning that it will delegate the update tasks to its own thread
     * on the server, by doing it this way it can improve server performance since accessing the internet
     * will not be done on the main thread.
     *
     * @see #schedule()
     */
    public void scheduleAsync() {
        if (isEnabled()) {
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> initialize(true), 0, getInterval());
        }
        else {
            result = UpdateResult.DISABLED;
        }
    }

    /*
    CHAIN OPTIONS
     */

    /**
     * Used to add a single provider to our provider list, Please note that you
     * cannot add a null provider or this method will throw an exception.
     *
     * @param provider Target provider
     * @return An instance of this updater chain
     * @see #getProviderList()
     * @see #getProvider()
     */
    public Updater addProvider(AbstractProvider provider) {
        Validate.notNull(provider, "An error occurred whilst trying to add a null provider");
        this.providerList.add(provider);
        return this;
    }

    /**
     * Used to add a player to the audience list, Please note that although
     * this method will not filter player at the time they were added, this updater
     * will later validate that this player can receive notifications by checking for
     * their online status and checking their permissions
     *
     * @param member Target audience member
     * @return An instance of this updater chain
     * @see #addAudience(org.bukkit.OfflinePlayer[])
     * @see #addAudience(java.util.Collection)
     * @see #getAudienceList()
     */
    public Updater addAudienceMember(@NotNull OfflinePlayer member) {
        if (!this.audienceList.contains(member)) this.audienceList.add(member);
        return this;
    }

    /**
     * Used to add a collection of players to the audience list, Please note that although
     * this method will not filter player's at the time they are added, this updater
     * will later validate that these players can receive notifications by checking for
     * their online status and checking their permissions
     *
     * @param audience Target audience
     * @return An instance of this updater chain
     * @see #addAudience(org.bukkit.OfflinePlayer[])
     * @see #addAudienceMember(org.bukkit.OfflinePlayer)
     * @see #getAudienceList()
     */
    public <T extends OfflinePlayer> Updater addAudience(@NotNull Collection<T> audience) {
        audience.forEach(this::addAudienceMember);
        return this;
    }

    /**
     * Used to add an array of players to the audience list, Please note that although
     * this method will not filter player's at the time they are added, this updater
     * will later validate that these players can receive notifications by checking for
     * their online status and checking their permissions
     *
     * @param audience Target audience
     * @return An instance of this updater chain
     * @see #addAudience(java.util.Collection)
     * @see #addAudienceMember(org.bukkit.OfflinePlayer)
     * @see #getAudienceList()
     */
    public <T extends OfflinePlayer> Updater addAudience(@NotNull T[] audience) {
        Arrays.stream(audience).forEach(this::addAudienceMember);
        return this;
    }

    /**
     * Used to toggle whether this updater is enabled or disabled, Please note that By default,
     * this updater will always be enabled.
     *
     * @param toggle determines whether this updater is enabled
     * @return An instance of this updater chain
     * @see #isEnabled()
     */
    public Updater setEnabled(boolean toggle) {
        this.enabled = toggle;
        return this;
    }

    /**
     * Used to set whether this updater will accept unstable versions as valid releases.
     * <p>
     * By default, this updater will only trigger our notification events if the providers
     * return as a {@link com.moleculepowered.api.updater.enums.ReleaseTag#RELEASE} version, but
     * by setting this methods parameter to true it will allow all release tags to trigger
     * the notification event and therefore notifying users of new most recent updates including
     * developmental builds.
     *
     * @param toggle determines whether unstable builds are preferred.
     * @return An instance of this updater chain.
     * @see #isUnstablePreferred()
     */
    public Updater setUnstablePreferred(boolean toggle) {
        this.unstablePreferred = toggle;
        return this;
    }

    /**
     * Used to set the cooldown interval for this updater, essentially this setting
     * will determine the frequency that our update task will execute throughout the day.
     * For examole, if you want the updater to search for updates every 2 hours, you will need
     * to set the interval to match that frequency, there are a few formats you can use to achieve
     * this, "2h", "2 h", "2 hour", "2 hours" are some examples of valid intervals. Please do note that
     * this method is not string when it comes to letter casing.
     *
     * @param interval Target interval
     * @return An instance of this updater chain
     * @see #getInterval()
     */
    public Updater setInterval(String interval) {
        this.interval = Util.toBukkitInterval(interval);
        return this;
    }

    /**
     * Used to set the permission that will be required by player's in-order to
     * receive update notifications.
     *
     * @param node Target permission node
     * @return An instance of this updater chain
     * @see #getPermission()
     */
    public Updater setPermission(String node) {
        this.permission = node;
        return this;
    }

    /*
    GETTER METHODS
     */

    /**
     * Used to return the final result that this updater produced, please note that by default
     * this method will return {@link com.moleculepowered.api.updater.enums.UpdateResult#UNKNOWN}
     * if the updater could not find a cause for this failure
     *
     * @return The final update result
     */
    public @NotNull UpdateResult getResult() { return result; }

    /**
     * Used to return the interval that this updater will use between update checks
     * 
     * @return The cooldown interval between checks
     * @see #setInterval(String) 
     */
    public long getInterval() { return interval; }

    /**
     * Used to return the permission required by player's in-order to receive
     * notifications.
     * 
     * @return The prerequisite permission
     * @see #setPermission(String) 
     */
    public @Nullable String getPermission() { return permission; }

    /**
     * Used to return a complete list of providers that will be used for by this updater 
     * 
     * @return The provider list
     * @see #addProvider(com.moleculepowered.api.updater.abstraction.AbstractProvider)
     * @see #getProvider() 
     */
    public @NotNull ArrayList<AbstractProvider> getProviderList() { return providerList; }

    /**
     * Used to return the audience list that could potentially receive notifications
     * from this updater and its components
     * 
     * @return The target audience list
     */
    public @NotNull ArrayList<Player> getAudienceList() {
        return audienceList.stream()
                           .filter(this::isNotifiable)
                           .map(OfflinePlayer::getPlayer)
                           .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Used to return the parent plugin assigned to this updater
     *
     * @return Parent plugin
     */
    public @NotNull Plugin getPlugin() { return plugin; }

    /**
     * Used to return the latest artifact created by this updater, Please note
     * that by default, this method will return an artifact for the version
     * locally installed on your server.
     *
     * @return The latest artifact
     */
    public @NotNull RemoteArtifact getLatestArtifact() { return latestBuild; }

    /**
     * Used to return the provider that contains the latest release, If all the providers
     * within the {@link #getProviderList()} do not contain a new update, this method
     * will return the last provider within the provider list.
     *
     * @return The provider containing the latest release
     * @see #addProvider(com.moleculepowered.api.updater.abstraction.AbstractProvider)
     * @see #getProviderList() 
     */
    public @Nullable AbstractProvider getProvider() { return provider; }

    /*
    BOOLEAN METHODS
     */

    /**
     * Used to return whether this updater is currently enabled.
     *
     * @return true if enabled
     * @see #setEnabled(boolean) 
     */
    public boolean isEnabled() { return enabled; }

    /**
     * Used to return whether unstable builds will trigger the notification system.
     * <p>
     * If this method return's true, it means that this updater will trigger the notification
     * system for all version types such as BETA, ALPHA etc. Otherwise, if false, this updater
     * will only for Release builds.
     *
     * @return true if unstable builds are preferred
     * @see #setUnstablePreferred(boolean) 
     */
    public boolean isUnstablePreferred() { return unstablePreferred; }

    /*
    UTILITY METHODS
     */

    /**
     * A utility method used to return whether an offline player is notifiable, this is determined
     * by checking if the player is currently online on the server and if they have the permission.
     *
     * @param player Target player
     * @return true if player can receive notifications
     * @see #getPermission()
     */
    private boolean isNotifiable(@NotNull OfflinePlayer player) {
        return player.isOnline() && (permission == null || player.getPlayer().hasPermission(getPermission()));
    }

    /*
    INNER CLASSES
     */

    /**
     * A class used to represent a remote artifact used by this updater, this class is tasked
     * with taking a version number and splitting it into 2 main components, the version number
     * and the release tag.
     */
    public static class RemoteArtifact
    {
        private String version;
        private String versionType;

        /*
        CONSTRUCTORS
         */

        public RemoteArtifact(@NotNull AbstractProvider provider) {
            this.version = provider.getRemoteVersion();
            configure();
        }

        public RemoteArtifact(String version) {
            this.version = version;
            configure();
        }

        /**
         * Used to parse the provided version and split it into 2 parts, a valid version and a valid
         * release type.
         */
        private void configure() {
            if (version == null) return;

            Matcher versionMatcher = Pattern.compile(Regex.VERSION).matcher(version);
            Matcher versionTypeMatcher = Pattern.compile(Regex.VERSION_TYPE).matcher(version);

            versionType = versionTypeMatcher.find() ? ReleaseTag.parse(versionTypeMatcher.group()).name() : ReleaseTag.RELEASE.name();

            if (versionMatcher.find()) version = versionMatcher.group();
            else throw new InvalidVersionException();
        }

        /*
        GETTER METHODS
         */

        /**
         * <p>Used to return the release type of the remote version</p>
         *
         * @return ReleaseTag type
         */
        public String getVersionType() { return versionType; }

        /**
         * Used to return the reformatted version that only includes the version number
         *
         * @return the reformatted version
         */
        public String getVersion()     { return version;     }
    }
}
