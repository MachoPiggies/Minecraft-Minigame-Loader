package com.machopiggies.gameloader.game;

import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloaderapi.event.tick.TickEvent;
import com.machopiggies.gameloaderapi.event.tick.TickType;
import com.machopiggies.gameloaderapi.game.*;
import com.machopiggies.gameloaderapi.kit.GameKit;
import com.machopiggies.gameloaderapi.scoreboard.GameScoreboard;
import com.machopiggies.gameloaderapi.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ServerGameRunner implements GameRunner, Runnable, Listener {

    GameManager gm;
    Game game;
    GameStage stage;
    GameState state;
    Set<Player> spectators;
    Set<GameTeam> registeredTeams;
    Map<Player, GameTeam> playerTeams;
    Set<GameKit> registeredKits;
    Map<Player, GameKit> playerKits;

    GameScoreboard scoreboard;
    int countdown;
    boolean doCountdown;

    public ServerGameRunner(Game game, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        gm = Manager.require(ServerGameManager.class, plugin);
        this.game = game;
        stage = GameStage.LOBBY;
        state = GameState.PRELOAD;
        spectators = new HashSet<>();
        registeredTeams = new HashSet<>();
        playerTeams = new HashMap<>();
        registeredKits = new HashSet<>();
        playerKits = new HashMap<>();
        countdown = 30;
        doCountdown = false;
    }

    @Override
    public void run() {
        if (game != null) {
            try {
                game.onLoad();
            } catch (Exception e) {
                try {
                    game.onUnload();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            if (gm.getSettings().isAutoStart()) {
                startCountdown();
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void startCountdown() {
        doCountdown = true;
    }

    @Override
    public void stopCountdown() {
        doCountdown = false;
        countdown = 30;
    }

    @EventHandler
    private void onTick(TickEvent event) {
        if (event.getType() != TickType.SEC) return;
        if (!doCountdown) return;
        if (countdown > 0) countdown--;
        if (countdown <= 0) start();
    }

    private void start() {
        doCountdown = false;
        state = GameState.PREPARING;
        try {
            game.onPreStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Game getGame() {
        return game;
    }

    public GameStage getStage() {
        return stage;
    }

    public GameState getState() {
        return state;
    }

    public Set<Player> getSpectators() {
        return spectators;
    }

    public Set<GameTeam> getTeams() {
        return registeredTeams;
    }

    public GameTeam getTeam(Player player) {
        return playerTeams.get(player);
    }

    @Override
    public boolean isEliminated(Player player) {
        return false;
    }

    public boolean isAlive(Player player) {
        return !spectators.contains(player);
    }

    public GameScoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.removeAll(spectators);
        return players;
    }

    public List<Player> getPlayingPlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.removeAll(spectators);
        return players;
    }

    public int getCountdown() {
        return countdown;
    }

    public GameKit getKit(Player player) {
        return new GameKit() {

            @Override
            public String getName() {
                return "testkit";
            }

            @Override
            public void setName(String name) {

            }

            @Override
            public String getDisplayName() {
                return "Test Kit";
            }

            @Override
            public void setDisplayName(String displayName) {

            }

            @Override
            public String[] getDescription() {
                return new String[0];
            }

            @Override
            public void setDescription(String... description) {

            }

            @Override
            public void setDescription(List<String> description) {

            }

            @Override
            public ItemStack getIcon() {
                return null;
            }

            @Override
            public void setIcon(ItemStack icon) {

            }

            @Override
            public boolean has(Player player) {
                return false;
            }

            @Override
            public void apply(Player player) {

            }

            @Override
            public void select(Player player) {

            }

            @Override
            public void deselect(Player player) {

            }
        };
    }

    @Override
    public void setKit(Player player, GameKit kit) {
        GameKit cKit = playerKits.get(player);
        if (cKit != null) {
            cKit.deselect(player);
        }
        playerKits.put(player, kit);
        kit.select(player);
    }

    public boolean isCountdown() {
        return doCountdown;
    }
}
