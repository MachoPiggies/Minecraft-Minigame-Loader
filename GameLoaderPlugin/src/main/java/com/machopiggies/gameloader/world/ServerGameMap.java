package com.machopiggies.gameloader.world;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloaderapi.game.GameRunner;
import com.machopiggies.gameloaderapi.team.GameTeam;
import com.machopiggies.gameloaderapi.world.GameMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ServerGameMap implements GameMap {

    final World world;
    final Map<Location, Properties> locations;

    public ServerGameMap(World world) {
        this.world = world;
        this.locations = new HashMap<>();
    }

    public void addLocation(Location location, Properties properties) {
        this.locations.put(location, properties);
    }

    public Location findSpawnPoint(Player player) {
        List<Location> possibles = new ArrayList<>();
        for (Map.Entry<Location, Properties> entry : locations.entrySet()) {
            if (!entry.getValue().contains("type")) continue;
            String typeRequirement = entry.getValue().getProperty("type");
            String teamRequirement = entry.getValue().getProperty("team", "neutral");

            if (!typeRequirement.equalsIgnoreCase("spawn")) break;
            GameRunner runner = Core.getGameManager().getGameRunner();
            if (runner == null) continue;

            GameTeam team = runner.getTeam(player);
            if (!(team != null ? team.getName() : "neutral").equalsIgnoreCase(teamRequirement)) continue;
            possibles.add(entry.getKey());
        }
        if (possibles.isEmpty()) return new Location(world, 0.5, 64, 0.5);
        return possibles.get(new Random().nextInt(possibles.size()));
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Map<Location, Properties> getLocations() {
        return locations;
    }
}
