package com.machopiggies.gameloader.util;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloaderapi.event.tick.TickEvent;
import com.machopiggies.gameloaderapi.event.tick.TickType;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Ticker implements Runnable {

    BukkitTask task;

    public Ticker() {
        task = Bukkit.getScheduler().runTaskTimer(Core.getSelf(), this, 0L, 1L);
    }

    @Override
    public void run() {
        for (TickType updateType : TickType.values()) {
            if (updateType.elapsed()) {
                Bukkit.getPluginManager().callEvent(new TickEvent(updateType));
            }
        }
    }

    public void shutdown() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
