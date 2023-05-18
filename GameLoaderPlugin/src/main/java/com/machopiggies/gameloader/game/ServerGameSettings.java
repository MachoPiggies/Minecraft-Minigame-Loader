package com.machopiggies.gameloader.game;

import com.machopiggies.gameloaderapi.game.GameSettings;

public class ServerGameSettings implements GameSettings {

    boolean teamBalancing;
    boolean autoStart;
    boolean gameTimeout;
    boolean kickInactive;
    boolean kitsUnlocked;
    boolean doRewards;

    public ServerGameSettings() {
        teamBalancing = true;
        autoStart = true;
        gameTimeout = true;
        kickInactive = true;
        kitsUnlocked = false;
        doRewards = true;
    }

    /**
     * Returns if the server is using automatic team balancing
     *
     * @return if the server is using balancing
     */
    @Override
    public boolean useTeamBalancing() {
        return teamBalancing;
    }

    /**
     * Sets the team balancing preference
     *
     * @param teamBalancing if team balancing should be on or not
     */
    @Override
    public void setTeamBalancing(boolean teamBalancing) {
        this.teamBalancing = teamBalancing;
    }

    /**
     * Returns if the server should auto start games
     *
     * @return if the server is auto starting games
     */
    @Override
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Sets the game auto start preference
     *
     * @param autoStart if games should auto start
     */
    @Override
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Checks if the game should auto timeout when it's gone on for too long
     *
     * @return if game timeout is on or not
     */
    @Override
    public boolean doGameTimeout() {
        return gameTimeout;
    }

    /**
     * Sets if the game should auto timeout or not when it's gone on for too long
     *
     * @param gameTimeout if game timeout should be enabled or not
     */
    @Override
    public void setGameTimeout(boolean gameTimeout) {
        this.gameTimeout = gameTimeout;
    }

    /**
     * Checks if the game should kick people that are AFK
     *
     * @return if the game should kick AFK players
     */
    @Override
    public boolean doKickInactive() {
        return kickInactive;
    }

    /**
     * Sets if the game should kick players that have been AFK for too long who don't have a bypass permission
     *
     * @param kickInactive if the game should kick players or not
     */
    @Override
    public void setKickInactive(boolean kickInactive) {
        this.kickInactive = kickInactive;
    }

    /**
     * Checks if the game should be unlocking all kits for all players
     *
     * @return if the game has all kits unlocked by default
     */
    @Override
    public boolean isKitsUnlocked() {
        return kitsUnlocked;
    }

    /**
     * Sets if the game should have all kits unlocked for all players
     *
     * @param kitsUnlocked if the game should have all kits unlocked or not
     */
    @Override
    public void setKitsUnlocked(boolean kitsUnlocked) {
        this.kitsUnlocked = kitsUnlocked;
    }

    /**
     * Checks if the game should be giving rewards for players participating in games, does not inherently do this itself, this is here to help developers
     *
     * @return if the game should be giving rewards
     */
    @Override
    public boolean doRewards() {
        return doRewards;
    }

    /**
     * Sets if the game should be giving rewards for participation
     *
     * @param giveRewards if the game should be giving rewards or not
     */
    @Override
    public void setDoRewards(boolean giveRewards) {
        this.doRewards = giveRewards;
    }
}
