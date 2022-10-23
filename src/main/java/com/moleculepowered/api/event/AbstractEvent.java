package com.moleculepowered.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;

public abstract class AbstractEvent extends Event
{
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public AbstractEvent(boolean async) {
        super(async);
    }

    /**
     * Used to return a list of classes that handle this event
     *
     * @return This event's Handler list
     */
    @Override
    public HandlerList getHandlers() { return HANDLERS_LIST; }

    /**
     * Used to return a list of classes that handle this event
     *
     * @return This event's Handler list
     */
    @Contract(pure = true)
    public static HandlerList getHandlerList() { return HANDLERS_LIST; }
}
