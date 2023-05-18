package com.machopiggies.gameloaderapi.scoreboard;

import com.machopiggies.gameloaderapi.event.tick.TickEvent;
import com.machopiggies.gameloaderapi.event.tick.TickType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Deprecated
public abstract class ScoreboardManager implements Listener {
    private final Map<UUID, MainScoreboard> scoreboards;
    protected final Plugin plugin;

    private int tick = 0;

    public ScoreboardManager(Plugin plugin) {
        scoreboards = new HashMap<>();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        MainScoreboard scoreboard = new MainScoreboard(event.getPlayer(), plugin);

        this.scoreboards.put(event.getPlayer().getUniqueId(), scoreboard);

        setup(scoreboard);
        draw(scoreboard);

        event.getPlayer().setScoreboard(scoreboard.getHandle());

        handlePlayerJoin(event.getPlayer());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        this.scoreboards.remove(event.getPlayer().getUniqueId());
        handlePlayerQuit(event.getPlayer());
    }

    public MainScoreboard get(Player player) {
        return scoreboards.get(player.getUniqueId());
    }

    @EventHandler
    public void UpdateScoreboard(TickEvent event) {
        if (event.getType() != TickType.TICK) return;

        tick = (tick + 1) % 3;

        if (tick != 0) return;

        scoreboards.values().forEach(this::draw);
    }

    public abstract void handlePlayerJoin(Player player);

    public abstract void handlePlayerQuit(Player player);

    public abstract void setup(MainScoreboard scoreboard);

    public abstract void draw(MainScoreboard scoreboard);

    public Map<UUID, MainScoreboard> getScoreboards()
    {
        return Collections.unmodifiableMap(this.scoreboards);
    }
}
