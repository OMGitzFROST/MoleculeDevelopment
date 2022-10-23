package com.moleculepowered.api.event.updater;

import com.moleculepowered.api.event.AbstractEvent;
import com.moleculepowered.api.updater.Updater;
import com.moleculepowered.api.updater.enums.UpdateResult;

public class UpdateFailedEvent extends AbstractEvent
{
    // CLASS OBJECTS
    private final Updater updater;

    /*
    CONSTRUCTOR
     */

    public UpdateFailedEvent(boolean async, Updater updater) {
        super(async);
        this.updater = updater;
    }

    public UpdateFailedEvent(Updater updater) {
        super(false);
        this.updater = updater;
    }

    /**
     * Used to return the final result that the updater produced, please note that by default
     * this method will return {@link com.moleculepowered.api.updater.enums.UpdateResult#UNKNOWN}
     * if the update could not find a cause for this failure event
     *
     * @return The final update result
     */
    public UpdateResult getResult() { return updater.getResult(); }
}
