package com.machopiggies.gameloader.team;

import com.machopiggies.gameloaderapi.player.PlayerState;
import com.machopiggies.gameloaderapi.team.GameTeam;
import com.machopiggies.gameloaderapi.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ServerGameTeam implements GameTeam {

    private static long TEAM_ID;

    private String name;
    private String displayName;
    private ChatColor color;
    private final Map<Player, PlayerState> players;
    private List<Location> spawns;

    private final long teamCreatedTime = System.currentTimeMillis();

    private final long teamId = TEAM_ID += 1;

    public ServerGameTeam(String name, String displayName, ChatColor color) {
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        this.spawns = new ArrayList<>();
        this.players = new HashMap<>();
    }

    @Override
    public long getTimeCreated() {
        return teamCreatedTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    @Override
    public List<Location> getSpawns() {
        return spawns;
    }

    @Override
    public void addPlayer(Player player, boolean in) {
        players.put(player, in ? PlayerState.IN : PlayerState.OUT);

        new Message("Team", "You have joined " + getDisplayName() + " Team" + ChatColor.GRAY + "!").send(player);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
    }

    @Override
    public boolean hasPlayer(Player player) {
        return players.containsKey(player);
    }

    @Override
    public int getMaxPlayers() {
        return players.size();
    }

    @Override
    public void setPlayerState(Player player, PlayerState state) {
        if (player == null) return;

        players.put(player, state);
    }

    @Override
    public boolean isEliminated() {
        for (PlayerState state : players.values()) {
            if (state == PlayerState.OUT) continue;
            return false;
        }

        return true;
    }

    @Override
    public List<Player> getPlayers(boolean playerIn) {
        List<Player> alive = new ArrayList<>();

        for (Player player : players.keySet()) {
            if (!playerIn || (players.get(player) == PlayerState.IN && player.isOnline())) {
                alive.add(player);
            }
        }

        return alive;
    }

    /**
     * Adds a player to the team
     *
     * @param player the player
     */
    @Override
    public void addPlayer(Player player) {

    }

    @Override
    public String getFormattedName() {
        return getColor() + String.valueOf(ChatColor.BOLD) + getName();
    }

    @Override
    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public String getDisplayName() {
        return displayName == null ? name : displayName;
    }

    @Override
    public DyeColor getDyeColor() {
        switch (getColor()) {
            case GOLD:
                return DyeColor.ORANGE;
            case LIGHT_PURPLE:
                return DyeColor.PINK;
            case AQUA:
                return DyeColor.LIGHT_BLUE;
            case YELLOW:
                return DyeColor.YELLOW;
            case GREEN:
                return DyeColor.LIME;
            case DARK_GRAY:
                return DyeColor.GRAY;
            case GRAY:
                return DyeColor.SILVER;
            case DARK_AQUA:
                return DyeColor.CYAN;
            case DARK_PURPLE:
                return DyeColor.PURPLE;
            case BLUE:
            case DARK_BLUE:
                return DyeColor.BLUE;
            case DARK_GREEN:
                return DyeColor.GREEN;
            case RED:
                return DyeColor.RED;
            default:
                return DyeColor.WHITE;
        }
    }

    /**
     * Gets all players on the team
     *
     * @return team players
     */
    @Override
    public List<Player> getPlayers() {
        return null;
    }

    @Override
    public void setSpawns(Collection<Location> spawns) {
        this.spawns = new ArrayList<>(spawns);
    }

    @Override
    public void setSpawns(Location... locations) {
        setSpawns(Arrays.asList(locations));
    }

    @Override
    public long getTeamId() {
        return teamId;
    }
}
