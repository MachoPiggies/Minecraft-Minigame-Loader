package com.machopiggies.gameloader.game;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloaderapi.event.tick.TickEvent;
import com.machopiggies.gameloaderapi.event.tick.TickType;
import com.machopiggies.gameloaderapi.game.*;
import com.machopiggies.gameloaderapi.kit.GameKit;
import com.machopiggies.gameloaderapi.scoreboard.GameScoreboard;
import com.machopiggies.gameloaderapi.team.GameTeam;
import com.machopiggies.gameloaderapi.world.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
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
    File mapFile;
    File loadedMap;
    GameMap map;

    GameScoreboard scoreboard;
    int countdown;
    boolean doCountdown;
    boolean mapLoading = false;

    public ServerGameRunner(Game game, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        gm = Core.getGameManager();
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
                state = GameState.LOADING;
                game.onLoad();
                state = GameState.RECRUITING;
            } catch (Exception e) {
                try {
                    game.onUnload();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            selectMap();
            if (mapFile != null && gm.getSettings().isAutoStart()) {
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
        if (countdown == 10) loadMap();
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

    @Override
    public void selectMap() {
        File[] maps = game.getInfo().getMapDirectory().listFiles();
        Bukkit.getLogger().info(Arrays.toString(maps));
        Bukkit.getLogger().info(game.getInfo().getMapDirectory().getPath());
        if (maps == null || maps.length == 0) {
            mapFile = null;
            return;
        }
        selectMap(maps[new Random().nextInt(maps.length)]);
    }

    @Override
    public void selectMap(File mapFile) {
        this.mapFile = mapFile;
    }

    @Override
    public void loadMap() {
        // Won't do this on another thread because it's honestly easier to do it here and track it
        if (mapFile == null) return;
        mapLoading = true;
        loadedMap = mapFile;

        map = Core.getWorldManager().deepCopyGameWorld(mapFile);

        mapLoading = false;
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

    @Override
    public boolean isMapChosen() {
        return mapFile != null;
    }

    @Override
    public String getMapName() {
        return mapFile.getName();
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
        return playerKits.get(player);
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

    @Override
    public GameKit createKit(GameKit kit) {
        registeredKits.add(kit);
        return kit;
    }

    public boolean isCountdown() {
        return doCountdown;
    }
}
