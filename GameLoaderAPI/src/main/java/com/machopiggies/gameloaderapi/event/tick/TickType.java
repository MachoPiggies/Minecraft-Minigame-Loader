package com.machopiggies.gameloaderapi.event.tick;

public enum TickType {
    SEC(1000),
    HALF_SEC(500),
    QUARTER_SEC(250),
    EIGHTH_SEC(125),
    TICK(50),
    QUICK(3);

    private final long time;
    private long last;

    /**
     * Establishes the amount of time between each tick value
     *
     * @param time time in the enum
     */
    TickType(long time) {
        this.time = time;
        last = System.currentTimeMillis();
    }

    /**
     * Gets the time from each enum which is used to calculate the biggest enum
     *
     * @return time when the first tick was recorded
     */
    public long getTime() {
        return time;
    }

    /**
     * Checks to see if time criteria has exceeded enum level
     * @return if time difference since last event is more than tick level
     */
    public boolean elapsed() {
        if (System.currentTimeMillis() - last > time) {
            last = System.currentTimeMillis();
            return true;
        }

        return false;
    }
}
