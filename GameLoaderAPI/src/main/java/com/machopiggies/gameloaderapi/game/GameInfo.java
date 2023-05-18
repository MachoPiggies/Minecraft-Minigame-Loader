package com.machopiggies.gameloaderapi.game;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

/**
 * The information object that directly holds the data from the game.yml
 */
public interface GameInfo {

    /**
     * Gets the file that is read to deserialize data from the game.yml into the game
     * @return the {@link File} object for game.yml
     */
    File getDataFolder();

    /**
     * Gets the given string path to the main class of a {@link Game} jar.
     *
     * @return path of the main class of a {@link Game} jar
     */
    String getMainClass();

    /**
     * Gets the display name of a {@link Game} object, which is displayed to players.
     *
     * @return the display name of a {@link Game} object
     */
    String getName();

    /**
     * Gets the internal name of a {@link Game} object, which is displayed in debugging environments
     * and console.
     *
     * @return the internal name of a {@link Game} object
     */
    String getInternalName();

    /**
     * Gets the description of a {@link Game} object, which may displayed to players.
     *
     * @return the description of a {@link Game} object
     */
    String getDescription();

    /**
     * Gets the version of a {@link Game} object, which is displayed in debugging environments
     * and console.
     *
     * @return the version of a {@link Game} object
     */
    String getVersion();

    /**
     * Gets a list of authors of a {@link Game} object, which is displayed to players.
     *
     * @return a {@link Collection <  UUID >} of authors for this game
     */
    Collection<UUID> getAuthors();

    /**
     * Gets a list of contributors of a {@link Game} object, which may be displayed to players.
     *
     * @return a {@link Collection<UUID>} of contributors for this game
     */
    Collection<UUID> getContributors();

    /**
     * Gets the maximum amount of players per game allowed
     * @return max players
     */
    int getMaxPlayers();

    /**
     * Gets the {@link ItemStack} icon for the game
     * @return game icon
     */
    ItemStack getItem();
}
