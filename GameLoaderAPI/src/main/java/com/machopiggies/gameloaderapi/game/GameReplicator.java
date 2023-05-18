package com.machopiggies.gameloaderapi.game;

import com.machopiggies.gameloaderapi.team.GameTeam;
import org.bukkit.ChatColor;

/**
 * This class is loaded into {@link Game} objects and acts as a bridge to allow Games to access the api
 */
public interface GameReplicator {
    /**
     * Creates a team for a game
     * @param name the name for the team
     * @param displayName the display name for the team
     * @param color the color for the new team
     * @return the new team
     */
    GameTeam createTeam(String name, String displayName, ChatColor color);

    /**
     * Removes a team from a game
     * @param team the team to remove
     */
    void removeTeam(GameTeam team);

    /**
     * Accesses the game server settings
     * @return the game server settings
     */
    GameSettings getSettings();
}
