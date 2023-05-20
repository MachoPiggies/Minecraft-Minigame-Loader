package com.machopiggies.gameloader;

import com.machopiggies.gameloader.commands.CommandManager;
import com.machopiggies.gameloader.game.ServerGameManager;
import com.machopiggies.gameloader.gui.GuiManager;
import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloader.util.Ticker;
import com.machopiggies.gameloader.world.WorldManager;
import com.machopiggies.gameloaderapi.excep.InvalidGameException;
import com.machopiggies.gameloaderapi.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Core extends JavaPlugin {

    static Core self;
    static ExecutorService executors;
    List<Manager> managers;
    Ticker ticker;

    @Override
    public void onEnable() {
        self = this;
        long startTime = System.nanoTime();
        getLogger().info("Enabling " + this.getClass().getSimpleName());

        executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        saveDefaultConfig();
        managers = new ArrayList<>();

        makeManagers();

        try {
            gameManager.loadExternalGames();
        } catch (InvalidGameException e) {
            e.printStackTrace();
        }

        ticker = new Ticker();

//        Core.getCommandManager().registerCommand(this, GameCommand.class);
//        Core.getCommandManager().registerCommand(this, GameWhitelistCommand.class);
//        Core.getCommandManager().registerCommand(this, GameBlacklistCommand.class);

        double timeTaken = (System.nanoTime() - startTime) / 1e6;
        timeTaken = Math.round(timeTaken * 100) / 100.0;
        getLogger().info("Enabled " + this.getClass().getSimpleName() + " in " + timeTaken + " ms.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling " + this.getClass().getSimpleName());

        ticker.shutdown();

        killManagers();

        executors.shutdown();
        executors = null;
        self = null;

        getLogger().info("Disabled " + this.getClass().getSimpleName());
    }

    private static GuiManager guiManager;
    private static WorldManager worldManager;
    private static ServerGameManager gameManager;

    public void makeManagers() {
        managers = Arrays.asList(
                guiManager = new GuiManager(),
                worldManager = new WorldManager(),
                gameManager = new ServerGameManager()
        );
        Manager.enableManagers(this, managers);

        CommandManager.activateCmds();
    }

    public void killManagers() {
        Manager.disableManagers(managers);
        gameManager = null;
    }

    public ClassLoader getPluginClassLoader() {
        return super.getClassLoader();
    }

    public static Core getSelf() {
        return self;
    }

    public static GuiManager getGuiManager() {
        return guiManager;
    }

    public static ServerGameManager getGameManager() {
        return gameManager;
    }

    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static ExecutorService getExecutorService() {
        return executors;
    }
}
