package com.machopiggies.gameloader.games;

import com.machopiggies.gameloaderapi.event.tick.TickEvent;
import com.machopiggies.gameloaderapi.event.tick.TickType;
import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.util.Message;
import com.machopiggies.gameloaderapi.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Spleef extends Game implements Listener {

    boolean doBreak = false;
    int countdown = 10;
    boolean doCountdown = false;

    public Spleef() {

    }

    @Override
    public void onPreStart() {
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        for (Player player : getReplicator().getRunner().getPlayers()) {
            Location location = getReplicator().getRunner().getMap().findSpawnPoint(player);
            player.teleport(location);
        }
        doCountdown = true;
    }

    @Override
    public void onStart() {
        doBreak = true;
    }

    @Override
    public void onStop() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void tickEvent(TickEvent event) {
        if (event.getType() == TickType.SEC) {
            if (doCountdown && countdown > 0) {
                countdown--;
                if (countdown == 0) {
                    if (getReplicator().getRunner().getPlayers().size() <= 1) {
                        doBreak = false;
                        Player winner = getReplicator().getRunner().getPlayers().get(0);
                        new Message("Game", Message.HEADER + winner.getName() + Message.DEFAULT + " has won!").broadcast();
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                            getReplicator().getRunner().stop();
                        }, 100);
                        return;
                    }
                    onStart();
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PacketUtil.sendTitle(player, ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + countdown, null, 0, 20, 0);
                    }
                }
            }
        }
        if (event.getType() != TickType.QUARTER_SEC) return;
        if (doBreak) {
            for (Player player : getReplicator().getRunner().getPlayers()) {
                Location location = player.getLocation().clone().add(0, -0.5, 0);
                location.getBlock().setType(Material.AIR);
            }
        }
    }
}
