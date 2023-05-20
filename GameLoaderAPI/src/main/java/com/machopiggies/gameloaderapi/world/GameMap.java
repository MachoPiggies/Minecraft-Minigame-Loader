package com.machopiggies.gameloaderapi.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Properties;

public interface GameMap {

    void addLocation(Location location, Properties properties);
    Location findSpawnPoint(Player player);
    World getWorld();
    Map<Location, Properties> getLocations();
}
