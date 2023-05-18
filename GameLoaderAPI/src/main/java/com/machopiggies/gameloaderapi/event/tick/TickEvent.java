package com.machopiggies.gameloaderapi.event.tick;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An internal ticker event that fires once every tick so repeating functions don't need an individual
 * scheduled thread each improving memory usage.
 */
public class TickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final TickType type;

    /**
     * The only constructor for the event to run, needs a calculation to take which tick type is being sent
     *
     * @param type the biggest tick difference sent with the event
     */
    public TickEvent(TickType type) {
        this.type = type;
    }

    /**
     * Returns the tick calculated for the event
     *
     * @return the tick event for this event
     */
    public TickType getType() {
        return type;
    }

    /**
     * Internal handler list for Bukkit events to work
     *
     * @return the {@link HandlerList} for this event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Internal handler list for Bukkit events to work
     *
     * @return the {@link HandlerList} for this event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
