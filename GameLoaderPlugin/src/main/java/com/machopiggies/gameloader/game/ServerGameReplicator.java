package com.machopiggies.gameloader.game;

import com.machopiggies.gameloader.team.ServerGameTeam;
import com.machopiggies.gameloaderapi.game.GameManager;
import com.machopiggies.gameloaderapi.game.GameReplicator;
import com.machopiggies.gameloaderapi.game.GameRunner;
import com.machopiggies.gameloaderapi.game.GameSettings;
import com.machopiggies.gameloaderapi.kit.GameKit;
import com.machopiggies.gameloaderapi.team.GameTeam;
import org.bukkit.ChatColor;

public class ServerGameReplicator implements GameReplicator {
    GameManager gm;

    public ServerGameReplicator(GameManager gm) {
        this.gm = gm;
    }

    @Override
    public GameTeam createTeam(String name, String displayName, ChatColor color) {
        GameTeam team = new ServerGameTeam(name, displayName, color);
        gm.getGameRunner().getTeams().add(team);
        return team;
    }

    @Override
    public GameKit createKit(GameKit kit) {
        return gm.getGameRunner().createKit(kit);
    }

    @Override
    public GameRunner getRunner() {
        return gm.getGameRunner();
    }

    @Override
    public void removeTeam(GameTeam team) {
        gm.getGameRunner().getTeams().remove(team);
    }

    @Override
    public GameSettings getSettings() {
        return gm.getSettings();
    }
}
