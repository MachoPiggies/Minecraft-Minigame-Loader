package com.machopiggies.gameloaderapi.game;

import com.machopiggies.gameloaderapi.team.GameTeam;
import com.machopiggies.gameloaderapi.team.TeamDistributor;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * The {@link Game} object that all games need to superclass to be able to be loaded
 */
public abstract class Game {
    private boolean enabled;
    private GameInfo info;
    private TeamDistributor teamDistributor;
    private GameReplicator replicator;

    private Plugin plugin;

    /**
     * Fired when the loader first loads a game into memory, ready to send it live
     */
    public void onEnable() { }

    /**
     * Fired when the plugin starts shutting down, shuts down all the games first
     */
    public void onDisable() { }

    /**
     * Fired when the game is loaded which loads game kits, maps, countdown etc
     */
    public void onLoad() { }

    /**
     * Fired after a game is finished or a game is changed from the current one
     */
    public void onUnload() { }

    /**
     * Fired when the countdown to enter the game ends and players are placed in the map
     */
    public void onPreStart() { }

    /**
     * Fired when the game countdown ends and players are given the ability to play
     */
    public void onStart() { }

    /**
     * Fired when the game ends
     */
    public void onStop() { }

    /**
     * Argumentless superclass constructor as no data should be being shared into the Game
     */
    public Game() {
        enabled = false;
    }

    /**
     * Gets the data for the game collected from its game.yml
     * @return the {@link GameInfo} object which contains all loadable data about the game
     */
    public GameInfo getInfo() {
        return info;
    }

    /**
     * Internal method that sets the {@link GameInfo} object for the game, this should not be being fired outside the plugin
     * @param info the deserialized {@link GameInfo} object
     */
    public void setInfo(GameInfo info) {
        this.info = info;
    }

    /**
     * Checks to see if the game is enabled yet
     * @return if the game is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the games enabled status, this should not be being fired from outside the plugin
     * @param enabled if the game should be enabled or not
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Adds a team to the game through the replicator
     * @param name the name of the team
     * @param displayName the display name of the team
     * @param color the color the team should be
     * @return the {@link GameTeam} object pertaining to the team
     */
    public GameTeam addTeam(String name, String displayName, ChatColor color) {
        return replicator.createTeam(name, displayName, color);
    }

    /**
     * Removes a team from a game
     * @param team the {@link GameTeam} to remove
     */
    public void removeTeam(GameTeam team) {
        replicator.removeTeam(team);
    }

    /**
     * Gets the team distributor for the game which controls how players are teamed automatically
     * @return the {@link TeamDistributor} object
     */
    public TeamDistributor getTeamDistributor() {
        return teamDistributor;
    }

    /**
     * Sets the team distributor for the game, this should not be being accessed from outside the plugin
     * @param teamDistributor the {@link TeamDistributor} object to set
     */
    public void setTeamDistributor(TeamDistributor teamDistributor) {
        this.teamDistributor = teamDistributor;
    }

    /**
     * Gets the replicator which allows a plugin access to the game loaders data, as well as the games settings
     * @return the {@link GameReplicator} object for the game
     */
    public GameReplicator getReplicator() {
        return replicator;
    }

    /**
     * Sets the replicator, this should not be being set outside the plugin
     * @param replicator the {@link GameReplicator} object
     */
    public void setReplicator(GameReplicator replicator) {
        this.replicator = replicator;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
