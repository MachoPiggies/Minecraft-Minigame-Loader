package com.machopiggies.gameloaderapi.team;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Distributes players fairly between teams
 */
public interface TeamDistributor {

    /**
     * Distributes players across specific registered teams in the current active game
     * @param players the players to distribute
     * @return the players new teams
     */
    Map<Player, GameTeam> distribute(List<Player> players, List<GameTeam> teams);
}
