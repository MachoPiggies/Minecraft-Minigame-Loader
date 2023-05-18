package com.machopiggies.gameloaderapi.kit;

import org.bukkit.entity.Player;

/**
 * Runnable for checking if a player can use a kit
 */
public interface GameKitOwnershipCheck {
    /**
     * Provides functionality to do more than permission checking for kits
     * @param player the player to test
     * @return if the player can use the kit
     */
    boolean has(Player player);
}
