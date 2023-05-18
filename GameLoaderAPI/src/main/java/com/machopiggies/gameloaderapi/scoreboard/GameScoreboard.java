package com.machopiggies.gameloaderapi.scoreboard;

import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.scoreboard.animations.Scroll;
import com.machopiggies.gameloaderapi.team.GameTeam;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.Function;

@Deprecated
public class GameScoreboard extends WritableScoreboard {

    private static final String[] TITLE = new Scroll("  LOADER  ")
            .left(ChatColor.GOLD)
            .mid(ChatColor.YELLOW)
            .right(ChatColor.WHITE)
            .bold()
            .build();

    private final Game game;

    private int shineIndex;

    public GameScoreboard(Game game, Plugin plugin) {
        this(game, null, plugin);
    }

    public GameScoreboard(Game game, Player player, Plugin plugin) {
        super(player, plugin);

        this.game = game;
        setSidebarName(TITLE[0]);
    }

    @Override
    public void draw() {
        if (_bufferedLines.size() > 15) {
            while (_bufferedLines.size() > 15) {
                _bufferedLines.remove(_bufferedLines.size() - 1);
            }
        }
        super.draw();
    }

    public Scoreboard getScoreboard() {
        return getHandle();
    }

    public void updateTitle() {
        setSidebarName(TITLE[shineIndex]);

        if (++shineIndex == TITLE.length) {
            shineIndex = 0;
        }
    }

    public void setPlayerTeam(Player player, GameTeam gameTeam) {
        String teamId = String.valueOf(gameTeam.getTeamId());

        if (getHandle().getTeam(teamId) == null) {
            Team targetTeam = getHandle().registerNewTeam(teamId);

            targetTeam.setPrefix(String.valueOf(gameTeam.getColor()));
            targetTeam.setSuffix(String.valueOf(ChatColor.RESET));
        }

        setPlayerTeam(player, teamId);
    }

    public void setSpectating(Player player) {
        if (getHandle().getTeam("SPEC") == null) {
            getHandle().registerNewTeam("SPEC").setPrefix(String.valueOf(ChatColor.GRAY));
        }
        setPlayerTeam(player, "SPEC");
    }

    private void setPlayerTeam(Player player, String teamId) {
        for (Team team : getHandle().getTeams()) {
            team.removeEntry(player.getName());
        }

        getHandle().getTeam(teamId).addEntry(player.getName());
    }

    public <T> void writeGroup(Collection<T> players, Function<T, Pair<String, Integer>> score, boolean prependScore) {
        Map<T, Integer> scores = new HashMap<>();
        Map<T, String> names = new HashMap<>();

        for (T player : players) {
            Pair<String, Integer> result = score.apply(player);
            if (result == null) continue;
            scores.put(player, result.getRight());
            names.put(player, result.getLeft());
        }

        scores = sortByValue(scores);

        for (Map.Entry<T, Integer> entry : scores.entrySet()) {
            String line = names.get(entry.getKey());
            if (prependScore) {
                line = entry.getValue() + " " + line;
            }
            write(line);
        }
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
