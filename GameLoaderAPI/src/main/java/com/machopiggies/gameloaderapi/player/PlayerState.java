package com.machopiggies.gameloaderapi.player;

/**
 * Enumerator used to track if a player is eliminated or not
 */
public enum PlayerState {
    IN("In"),
    OUT("Out");

    private final String name;

    /**
     * Holds data o how to present player state to players on the game servers
     * @param name how the state should be presented to the player
     */
    PlayerState(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the state the player is in
     * @return name of the state
     */
    public String GetName() {
        return name;
    }
}
