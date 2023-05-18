package com.machopiggies.gameloaderapi.team;

import com.machopiggies.gameloaderapi.player.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.List;

/**
 * Establishes a team that allows players to work together in games
 */
public interface GameTeam {
    /**
     * Gets the internal name of a team, must be different to other teams in the game
     * @return internal name of the team
     */
    String getName();

    /**
     * Sets the internal name of a team, used to differentiate between teams with the same name
     * @param name the internal team name
     */
    void setName(String name);

    /**
     * Gets the display name of the team which will be presented to players
     * @return the name to be displayed to players
     */
    String getDisplayName();

    /**
     * Sets the display name of a team
     * @param name team display name
     */
    void setDisplayName(String name);

    /**
     * Colors the display name of the team which can then be correctl ypresented to players
     * @return a formatted name to be presented to players
     */
    String getFormattedName();

    /**
     * The color of the team which is used in UIs, chat and nametags
     * @return team chat color
     */
    ChatColor getColor();

    /**
     * Sets the color that the team will use in its display names
     * @param color team colour
     */
    void setColor(ChatColor color);

    /**
     * Gets the dye color that the team will use in UIs and item form
     * @return team dye color
     */
    DyeColor getDyeColor();

    /**
     * Gets all players on the team
     * @return team players
     */
    List<Player> getPlayers();

    /**
     * Gets all players on the team with the option of only getting alive ones
     * @param includeEliminated alive only
     * @return team players
     */
    List<Player> getPlayers(boolean includeEliminated);

    /**
     * Adds a player to the team
     * @param player the player
     */
    void addPlayer(Player player);

    /**
     * Adds a player to the team with the option of them being eliminated when they join it
     * @param player the player
     */
    void addPlayer(Player player, boolean eliminated);

    /**
     * Removes a player from the team
     * @param player the player
     */
    void removePlayer(Player player);

    /**
     * Checks if a player is on the team
     * @param player the player
     * @return if the player is on the team
     */
    boolean hasPlayer(Player player);

    /**
     * Gets the maximum amount of players allowed on the team
     * @return max team players
     */
    int getMaxPlayers();

    /**
     * Sets the player state in the game
     * @param player the player
     * @param state new state
     */
    void setPlayerState(Player player, PlayerState state);

    /**
     * Checks if the team is eliminated or not
     * @return if the team is eliminated
     */
    boolean isEliminated();

    /**
     * Sets the spawn points for the team players
     * @param locations list of locations for players to spawn
     */
    void setSpawns(Collection<Location> locations);

    /**
     * Sets the spawn points for the team players
     * @param locations list of locations for players to spawn
     */
    void setSpawns(Location... locations);

    /**
     * Gets the list of spawn points for players on the team
     * @return list of spawn points for team
     */
    List<Location> getSpawns();

    /**
     * Gets the team ID of the team
     * @return the team ID
     */
    long getTeamId();

    /**
     * Gets when the team was created
     * @return UNIX timestamp of team creation
     */
    long getTimeCreated();
}
