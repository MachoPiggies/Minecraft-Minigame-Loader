package com.machopiggies.gameloaderapi.game;

/**
 * The stage the game is at from the servers perspective
 */
public enum GameState {
    PRELOAD(false),
    LOADING(false),
    RECRUITING(false),
    PREPARING(true),
    LIVE(true),
    ENDED(true),
    DEAD(false);

    private final boolean started;

    GameState(boolean started) {
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }
}
