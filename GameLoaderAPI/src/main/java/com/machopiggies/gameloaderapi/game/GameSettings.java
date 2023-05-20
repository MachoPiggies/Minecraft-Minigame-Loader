package com.machopiggies.gameloaderapi.game;

/**
 * The game settings for the game server
 */
public interface GameSettings {
    /**
     * Returns if the server is using automatic team balancing
     * @return if the server is using balancing
     */
    boolean useTeamBalancing();

    /**
     * Sets the team balancing preference
     * @param teamBalancing if team balancing should be on or not
     */
    void setTeamBalancing(boolean teamBalancing);

    /**
     * Returns if the server should auto start games
     * @return if the server is auto starting games
     */
    boolean isAutoStart();

    /**
     * Sets the game auto start preference
     * @param autoStart if games should auto start
     */
    void setAutoStart(boolean autoStart);

    /**
     * Checks if the game should auto timeout when it's gone on for too long
     * @return if game timeout is on or not
     */
    boolean doGameTimeout();

    /**
     * Sets if the game should auto timeout or not when it's gone on for too long
     * @param gameTimeout if game timeout should be enabled or not
     */
    void setGameTimeout(boolean gameTimeout);

    /**
     * Checks if the game should kick people that are AFK
     * @return if the game should kick AFK players
     */
    boolean doKickInactive();

    /**
     * Sets if the game should kick players that have been AFK for too long who don't have a bypass permission
     * @param kickInactive if the game should kick players or not
     */
    void setKickInactive(boolean kickInactive);

    /**
     * Checks if the game should be unlocking all kits for all players
     * @return if the game has all kits unlocked by default
     */
    boolean isKitsUnlocked();

    /**
     * Sets if the game should have all kits unlocked for all players
     * @param kitsUnlocked if the game should have all kits unlocked or not
     */
    void setKitsUnlocked(boolean kitsUnlocked);

    /**
     * Checks if the game should be giving rewards for players participating in games, does not inherently do this itself, this is here to help developers
     * @return if the game should be giving rewards
     */
    boolean doRewards();

    /**
     * Sets if the game should be giving rewards for participation
     * @param giveRewards if the game should be giving rewards or not
     */
    void setDoRewards(boolean giveRewards);

    /**
     * Checks if the game should be looking for new games when a game ends
     *
     * @return if the game should be rotating
     */
    boolean doGameRotation();

    /**
     * Sets if the game should be rotating on game end
     *
     * @param doGameRotation if game rotation is on or not
     */
    void setGameRotation(boolean doGameRotation);

    void saveSettings();
}
