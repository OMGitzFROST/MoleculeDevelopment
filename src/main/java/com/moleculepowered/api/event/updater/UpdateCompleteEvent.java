package com.moleculepowered.api.event.updater;

import com.moleculepowered.api.event.AbstractEvent;
import com.moleculepowered.api.updater.Updater;
import com.moleculepowered.api.updater.abstraction.AbstractProvider;
import com.moleculepowered.api.updater.enums.UpdateResult;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class UpdateCompleteEvent extends AbstractEvent
{
    private final Updater.RemoteArtifact artifact;
    private final Updater updater;

    public UpdateCompleteEvent(boolean async, Updater updater, Updater.RemoteArtifact artifact) {
        super(async);

        this.updater = updater;
        this.artifact = artifact;
    }

    public UpdateCompleteEvent(Updater updater, Updater.RemoteArtifact artifact) {
        super(false);

        this.updater = updater;
        this.artifact = artifact;
    }

    /**
     * Used to return the final result that the updater produced, please note that by default
     * this method will return {@link com.moleculepowered.api.updater.enums.UpdateResult#UNKNOWN}
     * if the update could not find a cause for this completion event
     *
     * @return The final update result
     */
    public UpdateResult getResult() { return updater.getResult(); }

    /**
     * Used to return the processed version for the latest release, As a processed number, this value
     * will typically only include the version numbers and be free from version tags, etc.
     *
     * @return The latest version number
     */
    public String getVersion() { return artifact.getVersion(); }

    /**
     * Gets the release tag assigned to the processed update, typically if there was not tag
     * within the remote version, this method will always return {@link com.moleculepowered.api.updater.enums.ReleaseTag#RELEASE}
     * as the release tag, otherwise it will return the matched tag.
     *
     * @return the update's assigned release tag
     */
    public String getReleaseTag() { return artifact.getVersionType(); }

    /**
     * Used to return a list of players that this event can notify.
     * <p>
     * Please note that this method doesnt not account for offline players or null players
     * and therefore has a chance of throwing an exception, please check for these conditions
     * prior to editing or preforming tasks on these audience members.
     *
     * @return A list of players
     */
    public ArrayList<Player> getAudienceList() { return updater.getAudienceList(); }

    /**
     * Used to return the provider that contains the latest release
     *
     * @return The provider containing the latest release
     */
    public AbstractProvider getProvider() { return updater.getProvider(); }
}
