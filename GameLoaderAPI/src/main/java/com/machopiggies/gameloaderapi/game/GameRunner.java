package com.machopiggies.gameloaderapi.game;

import com.machopiggies.gameloaderapi.kit.GameKit;
import com.machopiggies.gameloaderapi.scoreboard.GameScoreboard;
import com.machopiggies.gameloaderapi.scoreboard.MainScoreboard;
import com.machopiggies.gameloaderapi.team.GameTeam;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Handles the running of a {@link Game} object, will be paired to it which can be accessed through the game
 */
public interface GameRunner extends Runnable {

    /**
     * Starts the countdown for a game
     */
    void startCountdown();

    /**
     * Stops the countdown for a game
     */
    void stopCountdown();

    /**
     * Stops a game
     */
    void stop();

    void selectMap();

    void selectMap(File mapFile);

    void loadMap();

    /**
     * Gets the game that this game runner is made to work alongside
     * @return the {@link Game} object for this game runner
     */
    Game getGame();

    /**
     * Gets the games stage in lifetime
     * @return the games load stage
     */
    GameStage getStage();

    /**
     * Gets the games loading state
     * @return the games load state
     */
    GameState getState();

    /**
     * Gets a set of players spectating the game which removes them from all teams, kits and game requirements
     * @return a set of players spectating the game
     */
    Set<Player> getSpectators();

    /**
     * Gets a set of teams loaded into a game
     * @return games teams objects
     */
    Set<GameTeam> getTeams();

    /**
     * Gets the team of a specific player in the game
     * @param player the player
     * @return the team the player is on
     */
    GameTeam getTeam(Player player);

    /**
     * Checks if the player is eliminated or not
     * @param player the player
     * @return if the player is eliminated
     */
    boolean isEliminated(Player player);

    boolean isMapChosen();

    String getMapName();

    /**
     * Gets the scoreboard manager for this game instance
     * @return the scoreboard manager which can edit player scoreboards
     */
    GameScoreboard getScoreboard();

    /**
     * Gets the players in a game, excludes players spectating
     * @return list of game players
     */
    List<Player> getPlayers();

    /**
     * Gets the current stage in a countdowns progression
     * @return the number the countdown is currently at
     */
    int getCountdown();

    /**
     * Checks to see if the countdown is currently counting down
     * @return if the countdown is counting
     */
    boolean isCountdown();

    /**
     * Gets the kit a player is using
     * @param player the player
     * @return the kit the player is using
     */
    GameKit getKit(Player player);

    /**
     * Sets the kit for a player
     * @param player the player
     * @param kit the kit the player is going to use
     */
    void setKit(Player player, GameKit kit);

    GameKit createKit(GameKit kit);
}
