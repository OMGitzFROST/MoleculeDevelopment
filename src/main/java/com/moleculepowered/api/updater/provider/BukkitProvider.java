package com.moleculepowered.api.updater.provider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moleculepowered.api.console.Console;
import com.moleculepowered.api.updater.abstraction.AbstractProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * A default provider used to access resources through the Bukkit network
 *
 * @author Molecule
 */
public class BukkitProvider extends AbstractProvider
{
    private final String providerVersion, providerAuthor;
    private String downloadLink, remoteVersion;

    /**
     * The primary constructor used to initialize all required objects before accessing the remote server.
     *
     * @param resourceID Resource ID assigned by the provider
     */
    public BukkitProvider(int resourceID) {
        super("https://api.curseforge.com/servermods/files?projectids={0}", String.valueOf(resourceID));
        this.providerVersion = "1.0";
        this.providerAuthor = "MoleculePowered";
    }

    /**
     * <p>Used to fetch the latest version from the remote server.</p>
     *
     * <p>The main function of this method should be to simple access the remote server
     * and initialize the values necessary for this provider</p>
     */
    @Override
    public boolean initialize() throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) getRemoteURL().openConnection();
        conn.addRequestProperty("User-Agent", getUserAgent());
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);
        conn.connect();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JsonArray resource = new Gson().fromJson(reader, JsonArray.class).getAsJsonArray();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            Console.warn("Unable to connect to provider, perhaps the resource doest not exist");
            return false;
        }

        if (resource.size() == 0) {
            Console.warn("There are no files yet for this resource");
            return false;
        }

        JsonObject latest = resource.get(resource.size() - 1).getAsJsonObject();

        remoteVersion = latest.get("name").getAsString();
        downloadLink = latest.get("downloadUrl").getAsString();
        return true;
    }

    /**
     * <p>Used to return the unique name for this update provider.</p>
     *
     * <p>Ideally this method should return a name that will uniquely identify
     * this provider, it can be a name of a service such as github or spigot, or
     * it can be a name with a version number, etc.</p>
     *
     * @return This providers unique name
     * @throws NullPointerException thrown when this value return's null
     */
    @Override
    public @NotNull String getProviderName() { return "Bukkit"; }

    /**
     * <p>Used to return the download link the notified user's will see when a new
     * update is available.</p>
     *
     * <p>If not method does not return a null value, it will be the link used
     * when notifying all permitted users about the update</p>
     *
     * <p>NOTE: This link is not intended to be the same link that will access the remote server,
     * its should be more tailored more to a location where the user's can download the new
     * update directly or to point them to the website where they can download it from</p>
     *
     * @return The update's download link
     */
    @Override
    public @Nullable String getDownloadLink() { return downloadLink; }

    /**
     * Used to return the link pointing to the update's changelog
     *
     * @return the url pointing to the changelog
     */
    @Override
    public @Nullable String getChangelogLink() { return null; }

    /**
     * <p>Used to return the remote version retrieved by this provider.</p>
     *
     * <p>NOTE: This method allows the possibility of return a null
     * version, the reason for this is to prevent errors being thrown when
     * this provider encounters adverse outcomes when accessing the remote server
     * such as a failed internet connection</p>
     *
     * @return Remote version
     */
    @Override
    public @Nullable String getRemoteVersion() { return remoteVersion; }

    /**
     * Gets the author of this provider, As many developers create their own custom
     * providers, we need a way to differentiate between each provider.
     *
     * @return The provider's author
     */
    @Override
    public @NotNull String getProviderAuthor() { return providerAuthor; }

    /**
     * Gets the current build version for this provider, please note that this method WILL
     * NOT return the version fetched but the remove server. As developers upgrade their methods
     * for fetching update information, this method will provide a way to differentiate between
     * each upgrade, allowing debugging to be easier to follow.
     *
     * @return The provider's build version
     */
    @Override
    public @NotNull String getProviderVersion() { return providerVersion; }
}
