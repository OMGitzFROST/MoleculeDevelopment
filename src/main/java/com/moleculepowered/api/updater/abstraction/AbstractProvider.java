package com.moleculepowered.api.updater.abstraction;

import com.moleculepowered.api.console.Console;
import com.moleculepowered.api.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractProvider
{
    // CLASS OBJECTS
    private final URL fetchURL;

    /*
    CONSTRUCTORS
     */

    /**
     * Creates a new instance for this provider
     * <p>
     * With this constructor, you need to provide the url that fetches the latest release
     * data, it consists of a string representation for the url along with any parameters
     * that will be included into the link.
     * <p>
     * Please note that the parameters follow the bracketed number formatting system. For example,
     * if you provided an input such as <code>Hello {0}, nice to see you</code>, the {0} is
     * the number format, and it will point to the first object inside the object parameters list.
     * {1} will point to the second object and so on.
     *
     * @param url Target url
     * @param param Optional Parameter
     */
    public AbstractProvider(String url, Object... param){ fetchURL = createURL(url, param); }

    /**
     * Used to fetch the latest version from the remote server.
     * <p>
     * The main function of this method should be to simple access the remote server
     * and initialize the values necessary for this provider
     */
    public abstract boolean initialize() throws IOException;

    /*
    PROVIDER DETAILS
     */

    /**
     * Used to return the unique name for this update provider.
     * <p>
     * Ideally this method should return a name that will uniquely identify
     * this provider, it can be a name of a service such as github or spigot, or
     * it can be a name with a version number, etc.
     *
     * @return This providers unique name
     * @throws NullPointerException thrown when this value return's null
     */
    public abstract @NotNull String getProviderName();

    /**
     * Gets the author of this provider, As many developers create their own custom
     * providers, we need a way to differentiate between each provider.
     *
     * @return The provider's author
     */
    public abstract @NotNull String getProviderAuthor();

    /**
     * Gets the current build version for this provider, please note that this method WILL
     * NOT return the version fetched but the remove server. As developers upgrade their methods
     * for fetching update information, this method will provide a way to differentiate between
     * each upgrade, allowing debugging to be easier to follow.
     *
     * @return The provider's build version
     */
    public abstract @NotNull String getProviderVersion();

    /*
     * ARTIFACT DETAILS
     */

    /**
     * Used to return the download link the notified user's will see when a new
     * update is available.
     * <p>
     * If not method does not return a null value, it will be the link used
     * when notifying all permitted users about the update
     * <p>
     * NOTE: This link is not intended to be the same link that will access the remote server,
     * its should be more tailored more to a location where the user's can download the new
     * update directly or to point them to the website where they can download it from
     *
     * @return The update's download link
     */
    public abstract @Nullable String getDownloadLink();

    /**
     * Used to return the link pointing to the update's changelog
     *
     * @return the url pointing to the changelog
     */
    public abstract @Nullable String getChangelogLink();

    /**
     * Used to return the remote version retrieved by this provider.
     * <p>
     * NOTE: This method allows the possibility of return a null
     * version, the reason for this is to prevent errors being thrown when
     * this provider encounters adverse outcomes when accessing the remote server
     * such as a failed internet connection
     *
     * @return Remote version
     */
    public abstract @Nullable String getRemoteVersion();

    /*
    UTILITY
     */

    /**
     * Ensures that the provided url contains a protocol such as "https", if one
     * does not exist, this method will detect and add a default protocol to
     * your input, otherwise this method will just return the original input.
     *
     * @param url Target url
     * @return A url wrapped with a protocol
     */
    private static String protocolWrapper(@NotNull String url) {
        Matcher protocol = Pattern.compile("(?i)(HTTP|HTTPS)(://)").matcher(url);
        return !protocol.find() ? "https://" + url : url;
    }

    /**
     * Creates a new url using the provided input, additionally this method accepts optional
     * parameters that will be included within the input using a number format, for example "{0}"
     *
     * @param url   Target url
     * @param param Optional parameters
     * @return a valid url
     */
    @Contract("_, _ -> new")
    protected static @NotNull URL createURL(@NotNull String url, Object... param) {
        String finalUrl = Util.format(protocolWrapper(url), param);

        try {
            return new URL(finalUrl);
        }
        catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Unable to create url: " + finalUrl, ex);
        }
    }

    /**
     * Gets the url that will access the remote server, please note that this url is not
     * the same as the download url or changelog link.
     *
     * @return the remote server url
     */
    protected URL getRemoteURL() { return fetchURL; }

    /**
     * A utility method used to test the connection to the remote server, as long as the url is
     * not invalid, this method will log the response code returned by the url. Otherwise,
     * a silent exception will be thrown and details about the error will be printed to the
     * console.
     */
    public void testConnection() {
        try {
            HttpURLConnection conn = (HttpURLConnection) getRemoteURL().openConnection();
            conn.addRequestProperty("User-Agent", getUserAgent());
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.connect();

            Console.log("Connection URLResponse: " + conn.getResponseCode());
            conn.disconnect();
        }
        catch (IOException ex) {
            Console.log("&4Connection test failed: Unable to connect to remote server");
            Console.log("&6Error: &e{0}", ex.getClass().getSimpleName());
            Console.log("&6Message: &e{0}", ex.getMessage());
        }
    }

    /**
     * Used to generate and return a user agent.
     * <p>
     * This agent follows the following format: BasicAPI/{PROVIDER_NAME} ({CURRENT_DATE})
     *
     * @return A generated user agent
     */
    protected @NotNull String getUserAgent() {
        return Util.format("BasicAPI/{0} ({1})", getClass().getSimpleName(), new Date());
    }
}