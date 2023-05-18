package com.machopiggies.gameloaderapi.player;

import java.util.UUID;

/**
 * Holds data for each game server host so how they got it can be tracked etc
 */
public interface GameHost {
    /**
     * The UUID of the host
     * @return the UUID of a player given host access
     */
    UUID getUUID();

    /**
     * Gets when the player was given host access
     * @return the UNIX timestamp for when a player was given host access
     */
    long getAddedAt();

    /**
     * Gets the player who gave the player host access if applicable
     * @return the player who gave the player host access
     */
    UUID getAddedBy();
}
