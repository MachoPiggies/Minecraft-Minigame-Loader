package com.machopiggies.gameloader.world;

import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloaderapi.util.FileUtil;
import com.machopiggies.gameloaderapi.util.Message;
import com.machopiggies.gameloaderapi.util.TextUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;

public class WorldManager extends Manager {

    private final String lobbyWorldName = "GameLobby";
    private World lobbyWorld;
    private Location spawnPoint;

    public void deepCopyLobbyWorld() {
        File srcDir = new File(Bukkit.getServer().getWorldContainer(), lobbyWorldName);
        if (!srcDir.exists()) {
            Bukkit.getLogger().warning("World does not exist!");
            return;
        }
        String name;
        File destDir = new File(Bukkit.getServer().getWorldContainer(), name = "arcademap-GameLobby-" + TextUtil.randomString(16));
        try {
            FileUtils.copyDirectory(srcDir, destDir);
            File[] files = destDir.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (!file.isFile()) continue;
                if (!file.getName().equalsIgnoreCase("uid.dat")) continue;
                file.delete();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        lobbyWorld = Bukkit.getServer().createWorld(new WorldCreator(name));

        File yamlFile;
        try {
            yamlFile = new File(srcDir, "lobby.yml");
            if (!yamlFile.exists()) {
                if (yamlFile.createNewFile()) {
                    Bukkit.getLogger().warning("Could not find a lobby.yml in " + srcDir.getPath() + ", creating...");
                } else {
                    Bukkit.getLogger().severe("Could not find a lobby.yml in " + srcDir.getPath() + ", creation of file failed!");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Fatal error whilst trying to find lobby.yml in " + srcDir.getPath());
            return;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(yamlFile);

            double x,y,z;
            config.set("locations.spawn.x", x = config.getDouble("locations.spawn.x", 0.5));
            config.set("locations.spawn.y", y = config.getDouble("locations.spawn.y", 64d));
            config.set("locations.spawn.z", z = config.getDouble("locations.spawn.z", 0.5));
            spawnPoint = new Location(lobbyWorld, x, y, z);
            config.save(yamlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteLobbyWorlds() {
        File[] files = Bukkit.getWorldContainer().listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isDirectory()) continue;
            if (!file.getName().startsWith("arcademap-GameLobby-")) continue;
            new Message("Arcade Startup", "Deleting " + file.getPath() + "...").console(false);
            FileUtil.deleteRecursively(file, Bukkit.getLogger());
            new Message("Arcade Startup", "Deleted " + file.getPath() + "!").console(false);
        }
    }

    public String getLobbyWorldName() {
        return lobbyWorldName;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(spawnPoint);
    }
}
