package com.machopiggies.gameloader.game;

import com.google.gson.Gson;
import com.machopiggies.gameloader.game.info.DynamicGameInfo;
import com.machopiggies.gameloader.game.info.FileBasedGameInfo;
import com.machopiggies.gameloader.lobby.LobbyManager;
import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloader.player.ServerHostData;
import com.machopiggies.gameloader.world.WorldManager;
import com.machopiggies.gameloaderapi.excep.InvalidGameException;
import com.machopiggies.gameloaderapi.game.*;
import com.machopiggies.gameloaderapi.kit.GameKit;
import com.machopiggies.gameloaderapi.load.GameClassLoader;
import com.machopiggies.gameloaderapi.player.GameHost;
import com.machopiggies.gameloaderapi.scoreboard.GameScoreboard;
import com.machopiggies.gameloaderapi.scoreboard.MainScoreboard;
import com.machopiggies.gameloaderapi.scoreboard.LoaderScoreboardLine;
import com.machopiggies.gameloaderapi.scoreboard.ScoreboardManager;
import com.machopiggies.gameloaderapi.team.GameTeam;
import com.machopiggies.gameloaderapi.util.CircularQueue;
import com.machopiggies.gameloaderapi.util.Message;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ServerGameManager extends Manager implements GameManager {

    private Map<String, Game> games;
    private Queue<String> rotation;
    private Map<UUID, Scoreboard> scoreboards;
    private Map<UUID, GameHost> hosts; // UUID, host by permission
    private Map<UUID, GameHost> cohosts; //

    private GameRunner gameRunner;
    private ScoreboardManager scoreboardManager;
    private LobbyManager lobbyManager;
//    VotingManager votingManager;

    private GameReplicator replicator;
    private ServerGameSettings settings;

    @Override
    public void onEnable() {
        games = new HashMap<>();
        rotation = new CircularQueue<>();
        replicator = new ServerGameReplicator(this);
        lobbyManager = new LobbyManager();
//        votingManager = new VotingManager();
        scoreboards = new HashMap<>();

        hosts = new HashMap<>();
        cohosts = new HashMap<>();

        settings = new ServerGameSettings();

        File file = new File(super.plugin.getDataFolder(), ".rotation");

        FileInputStream in = null;
        FileOutputStream out = null;
        ObjectInputStream objIn = null;
        ObjectOutputStream objOut = null;

        try {
            if (file.exists()) {
                in = new FileInputStream(file);
                objIn = new ObjectInputStream(in);
                rotation.addAll(Arrays.asList((String[]) objIn.readObject()));
            } else {
                out = new FileOutputStream(file);
                objOut = new ObjectOutputStream(out);
                objOut.writeObject(new String[0]);
                objOut.writeByte(1);
                rotation = new CircularQueue<>(games.values().stream().map(game -> game.getInfo().getInternalName()).collect(Collectors.toList()));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (objIn != null) {
                    objIn.close();
                }

                if (objOut != null) {
                    objOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WorldManager wm = require(WorldManager.class, plugin);
        new Message("Arcade", "Removing all old maps...").console(false);
        wm.deleteLobbyWorlds();
        new Message("Arcade", "Creating game lobby world (" + wm.getLobbyWorldName() + ")...").console(false);
        wm.deepCopyLobbyWorld();
        new Message("Arcade", "Lobby world created, should now be ready!").console(false);

        scoreboardManager = new ScoreboardManager(super.plugin) {
            @Override
            public void handlePlayerJoin(Player player) {

                if (gameRunner == null) return;
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    GameTeam gameTeam = null;
                    if (gameRunner.getGame() != null && gameRunner.getTeam(onlinePlayer) != null) {
                        gameTeam = gameRunner.getTeam(onlinePlayer);
                    }

                    addPlayerToScoreboards(onlinePlayer, gameTeam);
                }

                if (gameRunner.getGame() != null) {
                    if (gameRunner.isEliminated(player)) {
                        gameRunner.getScoreboard().setSpectating(player);
                    } else {
                        gameRunner.getScoreboard().setPlayerTeam(player, gameRunner.getTeam(player));
                    }
                }
            }

            @Override
            public void handlePlayerQuit(Player player) {

            }

            @Override
            public void setup(MainScoreboard scoreboard) {
                scoreboard.register(LoaderScoreboardLine.PLAYERS_SPACER)
                        .register(LoaderScoreboardLine.PLAYERS_NAME)
                        .register(LoaderScoreboardLine.PLAYERS_VALUE)
                        .register(LoaderScoreboardLine.KIT_SPACER)
                        .register(LoaderScoreboardLine.KIT_NAME)
                        .register(LoaderScoreboardLine.KIT_VALUE)
                        .recalculate();

                scoreboard.get(LoaderScoreboardLine.PLAYERS_NAME).write(Message.HEADER + ChatColor.BOLD + "Players");
                scoreboard.get(LoaderScoreboardLine.KIT_NAME).write(Message.HEADER + ChatColor.BOLD + "Kit");
            }

            @Override
            public void draw(MainScoreboard scoreboard) {
                if (false) {//votingManager.isVoteInProgress()) {
//                    scoreboard.setSidebarName(ChatColor.BOLD + "Vote ends in " + Message.BoldGreen + votingManager.getCurrentVote().getTimer());
                } else {
                    if (gameRunner == null && games.size() > 0) {
                        scoreboard.setSidebarName(ChatColor.BOLD + "Error: No Gamerunner");
                        scoreboard.get(LoaderScoreboardLine.PLAYERS_VALUE).write(String.valueOf(Bukkit.getOnlinePlayers().size()));
                        scoreboard.get(LoaderScoreboardLine.KIT_VALUE).write("Unknown");
                    } else if (gameRunner == null) {
                        scoreboard.setSidebarName(ChatColor.BOLD + "No games installed");
                        scoreboard.get(LoaderScoreboardLine.PLAYERS_VALUE).write(String.valueOf(Bukkit.getOnlinePlayers().size()));
                        scoreboard.get(LoaderScoreboardLine.KIT_VALUE).write("Unknown");
                    } else {
                        int countdown = gameRunner.getCountdown();
                        GameState state = gameRunner.getState();
                        if (gameRunner.getGame() == null) {
                            scoreboard.setSidebarName(ChatColor.BOLD + "No game selected");
                        } else if (countdown > 0 && gameRunner.isCountdown()) {
                            scoreboard.setSidebarName(ChatColor.BOLD + "Starting in " + Message.BoldGreen + countdown + " second" + (countdown == 1 ? "" : "s"));
                        } else if (countdown == 0) {
                            scoreboard.setSidebarName(Message.BoldGreen + "In Progress...");
                        } else if (state == GameState.RECRUITING || state == GameState.PRELOAD) {
                            scoreboard.setSidebarName(Message.BoldGreen + "Waiting for players");
                        } else if (state == GameState.LOADING) {
                            scoreboard.setSidebarName(Message.BoldGreen + "Loading...");
                        }

                        scoreboard.get(LoaderScoreboardLine.PLAYERS_VALUE).write(gameRunner.getPlayers().size() + (gameRunner.getGame() != null ? "/" + gameRunner.getGame().getInfo().getMaxPlayers() : ""));

                        ChatColor teamColor = ChatColor.GOLD;
                        String kitName = "None";

                        if (gameRunner.getGame() != null) {
                            GameKit kit = gameRunner.getKit(scoreboard.getOwner());
                            GameTeam team = gameRunner.getTeam(scoreboard.getOwner());

                            if (kit != null) {
                                kitName = kit.getName();
                            }

                            if (team != null) {
                                teamColor = team.getColor();
                            }
                        }

                        scoreboard.get(LoaderScoreboardLine.KIT_NAME).write(teamColor + Message.HEADER + ChatColor.BOLD + "Kit");
                        scoreboard.get(LoaderScoreboardLine.KIT_VALUE).write(kitName);
                    }
                }
            }
        };

        try {
            loadGame(new Game() {

            }, new DynamicGameInfo("Test Game", "testgame", "testtesttest", "1.0", new ArrayList<>(), new ArrayList<>(), 20, new ItemStack(Material.GRASS)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        queueGame();
    }

    @Override
    public void onDisable() {
        games.clear();
        games = null;
        scoreboards.clear();
        scoreboards = null;
    }

    /**
     * Gets the next game that should be queued
     * @return the next game
     */
    @Override
    public Game getNextGame() {
       if (settings.doGameRotation()) {
           return getRotation().isEmpty() ? null : getRotation().poll();
       } else {
           return new ArrayList<>(games.values()).get(new Random().nextInt(games.size()));
       }
    }

    /**
     * Queues the next game from the rotation, or if there is no rotation, picks a new game at random
     */
    @Override
    public void queueGame() {
        if (gameRunner != null) {
            gameRunner.stop();
            gameRunner = null;
        }
        Game game = getNextGame();
        if (game == null) return;
        gameRunner = createGameRunner(game);
        gameRunner.run();
    }

    /**
     * Loads a specific game into the next position in the rotation
     *
     * @param game the game to load
     */
    @Override
    public void queueGame(Game game) {
        gameRunner = createGameRunner(game);
        gameRunner.run();
    }

    /**
     * Creates a {@link GameRunner} object to start the next game
     *
     * @param game the {@link Game} to load
     */
    @Override
    public GameRunner createGameRunner(Game game) {
        return new ServerGameRunner(game, plugin);
    }

    /**
     * Loads an internal game. This is for independent plugins which import this api and want to load multiple games in one repository
     *
     * @param game the game to load
     */
    @Override
    public void loadGame(Game game, GameInfo info) throws InvalidGameException {
        Validate.isTrue(info instanceof DynamicGameInfo, "game info for internal game must be of type DynamicGameInfo");
        game.setInfo(info);
        try {
            game.setEnabled(true);
            game.onEnable();
        } catch (Exception e) {
            game.onDisable();
            throw new InvalidGameException("Error has come from onEnable in internal game " + info.getInternalName() + ", attempting to isolate stage...", e);
        }
        games.put(game.getInfo().getInternalName(), game);
    }

    /**
     * Loads a list of games from the directory located in the plugins 'game' directory
     */
    @Override
    public void loadExternalGames() throws InvalidGameException {
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
        loadExternalGames(file);
    }

    private void loadExternalGames(File directory) throws InvalidGameException {
        Validate.notNull(directory, "Directory must not be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");

        List<Game> stages = new ArrayList<>();

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.getName().endsWith(".jar")) continue;
            new Message("Games-Creation", "Attempting to load " + file.getName() + " stage...").console();
            GameInfo gameInfo;
            Game game;
            gameInfo = new FileBasedGameInfo(file, plugin);
            try (GameClassLoader loader = new GameClassLoader(getClass().getClassLoader(), gameInfo, file)) {
                stages.add(game = loader.getGame());
            } catch (Exception e) {
                throw new InvalidGameException("Unable to find stage data from " + file.getName(), e);
            }
            new Message("Games-Creation", file.getName() + " stage loaded!").console();

            try {
                game.setReplicator(replicator);
                game.setEnabled(true);
                game.onEnable();
            } catch (Exception e) {
                game.onDisable();
                throw new InvalidGameException("Error has come from onEnable in " + file.getName() + ", attempting to isolate stage...", e);
            }
        }
        for (Game stage : stages) {
            games.put(stage.getInfo().getInternalName(), stage);
        }
    }

    /**
     * Gets a list of loaded games on the plugin
     *
     * @return map of loaded games
     */
    @Override
    public Map<String, Game> getGames() {
        return games;
    }

    /**
     * Gets the current iteration queue for the games in rotation. If the rotation is turned off, the placement of games in this queue will be ignored
     *
     * @return the queue of games
     */
    @Override
    public Queue<Game> getRotation() {
        Queue<Game> games = new CircularQueue<>();
        for (String gameStr : rotation) {
            Game game = getGames().get(gameStr);
            if (game == null) continue;
            games.add(game);
        }
        return games;
    }

    /**
     * Saves a queue for future reference as a .rotation file in the plugins directory, which will be deserialized on loaded
     */
    @Override
    public void saveRotation() {
        FileOutputStream out = null;
        ObjectOutputStream objOut = null;

        File file = new File(plugin.getDataFolder(), ".rotation");

        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("could not delete old rotation file");
        }

        try {
            out = new FileOutputStream(file);
            objOut = new ObjectOutputStream(out);
            objOut.writeObject(rotation.toArray(new String[0]));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (objOut != null) {
                    objOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if a game is in the current rotation
     *
     * @param game the game to check
     * @return if the game is in the rotation
     */
    @Override
    public boolean isInRotation(Game game) {
        return rotation.contains(game.getInfo().getInternalName());
    }

    /**
     * Adds a game to the game rotation
     *
     * @param game the game to add
     */
    @Override
    public void addToRotation(Game game) {
        rotation.add(game.getInfo().getInternalName());
        saveRotation();
    }

    /**
     * Removes a game from the game rotation
     *
     * @param game the game to remove
     */
    @Override
    public void removeFromRotation(Game game) {
        rotation.remove(game.getInfo().getInternalName());
        saveRotation();
    }

    /**
     * Checks if the rotation is turned on
     *
     * @return if the rotation is on
     */
    @Override
    public boolean isRotationEnabled() {
        return settings.doGameRotation();
    }

    /**
     * Sets if the game rotation is turned on or not
     *
     * @param rotationEnabled if the rotation is to be turned on
     */
    @Override
    public void setRotationEnabled(boolean rotationEnabled) {
        this.settings.setGameRotation(rotationEnabled);
    }

    /**
     * Loads the game scoreboard onto the given player
     *
     * @param player the player to load the scoreboard on
     */
    @Override
    public void addPlayerToScoreboards(Player player, GameTeam team) {
        String teamId = "null";

        for (MainScoreboard scoreboard : scoreboardManager.getScoreboards().values()) {
           Team bukkitTeam = scoreboard.getOrMakeTeam(teamId);
            if (bukkitTeam == null) {
                bukkitTeam = scoreboard.getHandle().registerNewTeam(teamId);
                if (team != null) {
                    bukkitTeam.setPrefix(team.getColor() + "");
                }
            }
            bukkitTeam.addEntry(player.getName());
        }
    }

    /**
     * Checks if there is a game currently ready to be played
     *
     * @return if there is a queued game
     */
    @Override
    public boolean isGameQueued() {
        return gameRunner != null && gameRunner.getGame() != null;
    }

    /**
     * Gets the {@link GameRunner} of the current queued game which controls launching it
     *
     * @return the {@link GameRunner}
     */
    @Override
    public GameRunner getGameRunner() {
        return gameRunner;
    }

    /**
     * Checks if a player is a game host, anyone with a specified permission node or is a private server owner will be a host
     *
     * @param uuid the uuid of the player
     * @return if the player is a host
     */
    @Override
    public boolean isHost(UUID uuid) {
        return hosts.containsKey(uuid);
    }

    /**
     * Checks if a player is a game co-host, anyone a host elects as a co-host will be a co-host
     *
     * @param uuid the uuid of the player
     * @return if the player is a co-host
     */
    @Override
    public boolean isCoHost(UUID uuid) {
        return cohosts.containsKey(uuid);
    }

    /**
     * Adds a host to the game server without a player giving it, should be only used when system is giving host access
     *
     * @param uuid the uuid of the new host
     */
    @Override
    public void addHost(UUID uuid) {
        hosts.put(uuid, new ServerHostData(uuid));
    }

    /**
     * @param uuid    the uuid of the new co-host
     * @param addedBy the uuid of the co-host that added them
     */
    @Override
    public void addCoHost(UUID uuid, UUID addedBy) {
        cohosts.put(uuid, new ServerHostData(uuid, addedBy));
    }

    /**
     * Removes a host or co-host
     *
     * @param uuid the host to remove
     */
    @Override
    public void removeHost(UUID uuid) {
        hosts.remove(uuid);
        cohosts.remove(uuid);
    }

    /**
     * Gets a list of hosts in a game server
     *
     * @return a map of hosts with their host data
     */
    @Override
    public Map<UUID, GameHost> getHosts() {
        return hosts;
    }

    /**
     * Gets a list of co-hosts in a game server
     *
     * @return a map of co-hosts with their host data
     */
    @Override
    public Map<UUID, GameHost> getCoHosts() {
        return cohosts;
    }

    /**
     * Gets a list of hosts in a game server but returns players instead of UUIDs
     *
     * @return a map of hosts with their host data
     */
    @Override
    public Map<OfflinePlayer, GameHost> getResolvedHosts() {
        Map<OfflinePlayer, GameHost> map = new HashMap<>();
        for (Map.Entry<UUID, GameHost> entry : hosts.entrySet()) {
            map.put(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue());
        }
        return map;
    }

    /**
     * Gets a list of co-hosts in a game server but returns players instead of UUIDs
     *
     * @return a map of co-hosts with their host data
     */
    @Override
    public Map<OfflinePlayer, GameHost> getResolvedCoHosts() {
        Map<OfflinePlayer, GameHost> map = new HashMap<>();
        for (Map.Entry<UUID, GameHost> entry : cohosts.entrySet()) {
            map.put(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue());
        }
        return map;
    }

    /**
     * Gets game server settings
     *
     * @return game server settings
     */
    @Override
    public GameSettings getSettings() {
        return settings;
    }

    @EventHandler
    private void onPlayerLoad(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("gameloader.auto-host")) {
            hosts.put(event.getPlayer().getUniqueId(), new ServerHostData(event.getPlayer().getUniqueId(), null));
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        // Remove fake hosts
        GameHost data = hosts.get(event.getPlayer().getUniqueId());
        if (data != null && data.getAddedBy() == null) {
            hosts.remove(event.getPlayer().getUniqueId());
        }
        data = cohosts.get(event.getPlayer().getUniqueId());
        if (data != null && data.getAddedBy() == null) {
            cohosts.remove(event.getPlayer().getUniqueId());
        }
    }
}
