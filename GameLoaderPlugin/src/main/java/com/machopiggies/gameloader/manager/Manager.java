package com.machopiggies.gameloader.manager;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Manager implements Listener {
    private boolean enabled;
    private boolean aborted;
    protected Plugin plugin;
    private static final Map<Class<? extends Manager>, Manager> managers = new HashMap<>();

    public void enable(Plugin plugin) {
        if (!enabled) {
            this.plugin = plugin;
            long startTime = System.nanoTime();
            Bukkit.getLogger().info("Enabling " + getClass().getSimpleName());
            aborted = false;
            enabled = true;
            try {
                onEnable();
            } catch (Exception e) {
                abort();
                e.printStackTrace();
            }

            if (aborted) {
                Bukkit.getLogger().warning("Aborted " + this.getClass().getSimpleName());
                disable();
                return;
            }

            Bukkit.getPluginManager().registerEvents(this, plugin);
            managers.put(this.getClass(), this);
            double timeTaken = (System.nanoTime() - startTime) / 1e6;
            timeTaken = Math.round(timeTaken * 100) / 100.0;
            Bukkit.getLogger().info("Enabled " + this.getClass().getSimpleName() + " in " + timeTaken + " ms.");
        }
    }

    public void disable() {
        if (enabled) {
            Bukkit.getLogger().info("Disabling " + this.getClass().getSimpleName());
            HandlerList.unregisterAll(this);
            try {
                onDisable();
            } catch (Exception e) {
                e.printStackTrace();
            }
            managers.remove(this.getClass());
            enabled = false;
            Bukkit.getLogger().info("Disabled " + this.getClass().getSimpleName());
        }
    }

    protected void abort() {
        aborted = true;
    }

    protected void onEnable() { }

    protected void onDisable() { }

    public boolean isEnabled() {
        return enabled;
    }

    public static void enableManagers(Plugin plugin, Manager... managers) {
        enableManagers(plugin, Arrays.asList(managers));
    }

    public static void enableManagers(Plugin plugin, List<Manager> managers) {
        for (Manager manager : managers) {
            manager.enable(plugin);
        }
    }

    public static void disableManagers(Manager... managers) {
        disableManagers(Arrays.asList(managers));
    }

    public static void disableManagers(List<Manager> managers) {
        for (Manager manager : Lists.reverse(managers)) {
            manager.disable();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T require(Class<T> managerClass, Plugin plugin) {
        return (T) managers.computeIfAbsent((Class<? extends Manager>) managerClass, key -> {
            try {
                Manager manager = (Manager) managerClass.getDeclaredConstructor().newInstance();
                manager.enable(plugin);
                return manager;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
