package com.machopiggies.gameloaderapi.game;

import com.machopiggies.gameloaderapi.excep.InvalidGameException;
import com.machopiggies.gameloaderapi.player.GameHost;
import com.machopiggies.gameloaderapi.team.GameTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 * Controls the management of game loading, publishing, and playing
 */
public interface GameManager {

    /**
     * Gets the next game that should be queued
     * @return the next game
     */
    Game getNextGame();

    /**
     * Queues the next game from the rotation, or if there is no rotation, picks a new game at random
     */
    void queueGame();

    /**
     * Loads a specific game into the next position in the rotation
     * @param game the game to load
     */
    void queueGame(Game game);

    /**
     * Creates a {@link GameRunner} object to start the next game
     * @param game the {@link Game} to load
     */
    GameRunner createGameRunner(Game game);

    /**
     * Loads an internal game. This is for independent plugins which import this api and want to load multiple games in one repository
     * @param game the game to load
     */
    void loadGame(Game game, GameInfo info) throws InvalidGameException;

    /**
     * Loads a list of games from the directory located in the plugins 'game' directory
     */
    void loadExternalGames() throws InvalidGameException;

    /**
     * Gets a list of loaded games on the plugin
     *
     * @return list of loaded games
     */
    Map<String, Game> getGames();

    /**
     * Gets the current iteration queue for the games in rotation. If the rotation is turned off, the placement of games in this queue will be ignored
     * @return the queue of games
     */
    Queue<Game> getRotation();

    /**
     * Saves a queue for future reference as a .rotation file in the plugins directory, which will be deserialized on loaded
     */
    void saveRotation();

    /**
     * Checks if a game is in the current rotation
     * @param game the game to check
     * @return if the game is in the rotation
     */
    boolean isInRotation(Game game);

    /**
     * Adds a game to the game rotation
     * @param game the game to add
     */
    void addToRotation(Game game);

    /**
     * Removes a game from the game rotation
     * @param game the game to remove
     */
    void removeFromRotation(Game game);

    /**
     * Checks if the rotation is turned on
     * @return if the rotation is on
     */
    boolean isRotationEnabled();

    /**
     * Sets if the game rotation is turned on or not
     * @param rotationEnabled if the rotation is to be turned on
     */
    void setRotationEnabled(boolean rotationEnabled);

    /**
     * Loads the game scoreboard onto the given player
     * @param player the player to load the scoreboard on
     * @param team the team the player is on
     */
    void addPlayerToScoreboards(Player player, GameTeam team);

    /**
     * Checks if there is a game currently ready to be played
     * @return if there is a queued game
     */
    boolean isGameQueued();

    /**
     * Gets the {@link GameRunner} of the current queued game which controls launching it
     * @return the {@link GameRunner}
     */
    GameRunner getGameRunner();

    /**
     * Checks if a player is a game host, anyone with a specified permission node or is a private server owner will be a host
     * @param uuid the uuid of the player
     * @return if the player is a host
     */
    boolean isHost(UUID uuid);

    /**
     * Checks if a player is a game co-host, anyone a host elects as a co-host will be a co-host
     * @param uuid the uuid of the player
     * @return if the player is a co-host
     */
    boolean isCoHost(UUID uuid);

    /**
     * Adds a host to the game server without a player giving it, should be only used when system is giving host access
     * @param uuid the uuid of the new host
     */
    void addHost(UUID uuid);

    /**
     *
     * @param uuid the uuid of the new co-host
     * @param addedBy the uuid of the co-host that added them
     */
    void addCoHost(UUID uuid, UUID addedBy);

    /**
     * Removes a host or co-host
     * @param uuid the host to remove
     */
    void removeHost(UUID uuid);

    /**
     * Gets a list of hosts in a game server
     * @return a map of hosts with their host data
     */
    Map<UUID, GameHost> getHosts();

    /**
     * Gets a list of co-hosts in a game server
     * @return a map of co-hosts with their host data
     */
    Map<UUID, GameHost> getCoHosts();

    /**
     * Gets a list of hosts in a game server but returns players instead of UUIDs
     * @return a map of hosts with their host data
     */
    Map<OfflinePlayer, GameHost> getResolvedHosts();

    /**
     * Gets a list of co-hosts in a game server but returns players instead of UUIDs
     * @return a map of co-hosts with their host data
     */
    Map<OfflinePlayer, GameHost> getResolvedCoHosts();

    /**
     * Gets game server settings
     * @return game server settings
     */
    GameSettings getSettings();
}
