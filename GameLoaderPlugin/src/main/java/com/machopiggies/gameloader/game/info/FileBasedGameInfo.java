package com.machopiggies.gameloader.game.info;

import com.machopiggies.gameloaderapi.game.GameInfo;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileBasedGameInfo implements GameInfo {
    private File dataFolder;
    private String mainClass, name, internalName, description, version;
    private List<UUID> authors, contributors;
    private int maxPlayers;
    private ItemStack item;
    private Plugin plugin;

    public FileBasedGameInfo(File file, Plugin plugin) {
        Validate.notNull(file, "File cannot be null");

        this.plugin = plugin;
        InputStream inputStream;
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("game.yml");
            Validate.notNull(entry, "game.yml was not found in " + file.getName());

            inputStream = jar.getInputStream(entry);
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            mainClass = yml.getString("main");
            name = yml.getString("name");
            internalName = yml.getString("internalName");
            description = yml.getString("description");
            version = yml.getString("version");
            maxPlayers = yml.getInt("maxPlayers", 20);
            authors = new ArrayList<>();
            contributors = new ArrayList<>();
            for (String str : yml.getStringList("authors")) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(str);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                authors.add(uuid);
            }
            for (String str : yml.getStringList("contributors")) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(str);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                contributors.add(uuid);
            }
            item = new ItemStack(Material.valueOf(yml.getString("item.material", "GRASS")), 1, (byte) yml.getInt("item.data"));
            dataFolder = findDataFolder();
        } catch (IOException | YAMLException ex) {
            ex.printStackTrace();
        }
    }

    private File findDataFolder() {
        File file = new File(plugin.getDataFolder(), "games");
        if (!plugin.getDataFolder().exists()) {
            if (!plugin.getDataFolder().mkdir()) {
                Bukkit.getLogger().info("Attempt at creating plugin datafolder failed!");
            }
        }
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Bukkit.getLogger().info("Attempt at creating games folder failed!");
            }
        }
        File dataFolder = new File(file, internalName);
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                Bukkit.getLogger().info("Attempt at creating datafolder for game " + internalName + " failed!");
            }
        }
        return dataFolder;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
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
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("mainClass", mainClass);
        map.put("name", name);
        map.put("internalName", internalName);
        map.put("description", description);
        map.put("version", version);
        map.put("authors", authors);
        map.put("contributors", contributors);
        map.put("item", item.toString());
        return map.toString();
    }
}
