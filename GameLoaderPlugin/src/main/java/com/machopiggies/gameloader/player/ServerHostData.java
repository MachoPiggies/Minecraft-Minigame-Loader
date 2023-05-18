package com.machopiggies.gameloader.player;

import com.machopiggies.gameloaderapi.player.GameHost;

import java.util.UUID;

public class ServerHostData implements GameHost {

    private final UUID uuid, addedBy;
    private long addedAt;

    public ServerHostData(UUID uuid) {
        this.uuid = uuid;
        this.addedBy = null;
    }

    public ServerHostData(UUID uuid, UUID addedBy) {
        this.uuid = uuid;
        this.addedBy = addedBy;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public long getAddedAt() {
        return addedAt;
    }

    @Override
    public UUID getAddedBy() {
        return addedBy;
    }
}
