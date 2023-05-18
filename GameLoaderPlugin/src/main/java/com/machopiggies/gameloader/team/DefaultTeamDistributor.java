package com.machopiggies.gameloader.team;

import com.machopiggies.gameloaderapi.team.GameTeam;
import com.machopiggies.gameloaderapi.team.TeamDistributor;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.*;

public class DefaultTeamDistributor implements TeamDistributor {

    @Override
    public Map<Player, GameTeam> distribute(List<Player> players, List<GameTeam> teams) {
        Validate.isTrue(!players.isEmpty(), "there must be at least 1 player in the game");
        Validate.isTrue(!teams.isEmpty(), "there are no teams to distribute players across");
        Collections.shuffle(teams);
        Collections.shuffle(players);

        int i = 0;
        Map<Player, GameTeam> distribution = new HashMap<>();
        for (Player player : players) {
//            teams.get(i).addPlayer(player, true);
            distribution.put(player, teams.get(i));
            if (++i >= teams.size()) {
                i = 0;
            }
        }
        return distribution;
    }
}
