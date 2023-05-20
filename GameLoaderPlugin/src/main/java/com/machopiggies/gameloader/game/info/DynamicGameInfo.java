package com.machopiggies.gameloader.game.info;

import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.game.GameInfo;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.function.Function;

public class DynamicGameInfo implements GameInfo {

    private File dataFolder;
    private String mainClass, name, internalName, description, version;
    private List<UUID> authors, contributors;
    private int maxPlayers;
    private ItemStack item;
    private File mapFolder;

    public DynamicGameInfo(String name, String internalName, String description, String version, List<UUID> authors, List<UUID> contributors, int maxPlayers, ItemStack item) {
        this.name = name;
        this.internalName = internalName;
        this.description = description;
        this.version = version;
        this.authors = authors;
        this.contributors = contributors;
        this.maxPlayers = maxPlayers;
        this.item = item;

        this.mainClass = null;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    public void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public String getMainClass() {
        return mainClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Collection<UUID> getAuthors() {
        return authors;
    }

    @Override
    public Collection<UUID> getContributors() {
        return contributors;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public File getMapDirectory() {
        return mapFolder;
    }

    public void setMapFolder(File mapFolder) {
        this.mapFolder = mapFolder;
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("mainClass", mainClass);
        map.put("name", name);
        map.put("internalName", internalName);
        map.put("description", description);
        map.put("version", version);
        map.put("authors", authors);
        map.put("contributors", contributors);
        return map.toString();
    }
}
