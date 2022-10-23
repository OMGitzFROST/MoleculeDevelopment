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

public class GithubProvider extends AbstractProvider
{
    private final String providerVersion, providerAuthor;
    private String downloadLink, remoteVersion, changelogLink;

    public GithubProvider(String repo) {
        super("https://api.github.com/repos/{0}/releases/latest", repo);
        this.providerVersion = "1.0";
        this.providerAuthor = "MoleculePowered";
    }

    /*
    IMPLEMENTATION METHODS
     */

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

        if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            Console.warn("Unable to connect to provider, perhaps the resource doest not exist");
            return false;
        }

        if (conn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
            Console.debugWarn("Unable to connect to {0}: Rate limit reached", getProviderName());
            return false;
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JsonObject resource = new Gson().fromJson(reader, JsonObject.class);
        JsonArray assets = resource.get("assets").getAsJsonArray();

        // SET REMOTE VERSION AND DOWNLOAD LINK
        remoteVersion = resource.get("tag_name").getAsString();
        changelogLink = resource.get("html_url").getAsString();

        if (assets.size() > 0) {
            JsonObject latestResource = assets.get(0).getAsJsonObject();
            downloadLink = latestResource.get("browser_download_url").getAsString();
        }
        return true;
    }

    /*
    GETTER METHODS
     */

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
    public @NotNull String getProviderName() { return "GitHub"; }

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
    public @Nullable String getChangelogLink() {
        return changelogLink;
    }

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
