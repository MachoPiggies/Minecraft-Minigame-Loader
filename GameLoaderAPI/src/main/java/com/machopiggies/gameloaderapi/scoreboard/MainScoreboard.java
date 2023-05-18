package com.machopiggies.gameloaderapi.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.*;

@Deprecated
public class MainScoreboard {
    private static final char[] CHARS = "1234567890abcdefklmnor".toCharArray();

    private final Player owner;
    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    private final Objective sidebar;
    private final LinkedList<String> availableTrackers;
    private final Set<String> customTrackers;

    private final List<ScoreboardLine> calculated;
    private final Map<ScoreboardLine, ScoreboardElement> calculatedMap;
    private final List<ScoreboardLine> buffered;

    protected final Plugin plugin;

    public MainScoreboard(Player owner, Plugin plugin) {
        this.owner = owner;
        this.plugin = plugin;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        availableTrackers = new LinkedList<>();
        customTrackers = new HashSet<>();
        calculated = new ArrayList<>();
        calculatedMap = new HashMap<>();
        buffered = new ArrayList<>();

        sidebar = scoreboard.registerNewObjective("sidebar", "sidebar");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (char c : CHARS) {
            availableTrackers.add(ChatColor.COLOR_CHAR + String.valueOf(c) + ChatColor.RESET);
        }
    }

    public MainScoreboard register(ScoreboardLine line) {
        buffered.add(line);
        return this;
    }

    public MainScoreboard registerAfter(ScoreboardLine line, ScoreboardLine after) {
        int index = buffered.indexOf(after);
        if (index == -1) throw new IllegalStateException("Could not locate line: " + after);
        buffered.add(index, line);
        return this;
    }

    public MainScoreboard unregister(ScoreboardLine line) {
        buffered.remove(line);
        return this;
    }

    public void recalculate() {
        if (calculated.size() == 0) {
            for (int i = 0; i < buffered.size() && !availableTrackers.isEmpty(); i++) {
                String tracker = availableTrackers.pop();
                ScoreboardLine line = buffered.get(i);
                calculated.add(line);
                calculatedMap.put(line, new ScoreboardElement(this, sidebar, line, tracker, buffered.size() - i));
            }
        } else {
            for (ScoreboardLine calculated : calculated) {
                if (!buffered.contains(calculated)) {
                    ScoreboardElement element = get(calculated);
                    element.delete();
                }
            }

            Map<ScoreboardLine, ScoreboardElement> prevCalculatedMap = new HashMap<>(calculatedMap);

            calculated.clear();
            calculatedMap.clear();

            for (int i = 0; i < buffered.size(); i++) {
                ScoreboardLine line = buffered.get(i);
                ScoreboardElement element = prevCalculatedMap.get(line);
                int expectedScore = buffered.size() - i;

                if (element != null) {
                    Score score = sidebar.getScore(element.getTracker());
                    if (score.getScore() != expectedScore) {
                        score.setScore(expectedScore);
                        element.setLineNumber(expectedScore);
                    }

                    calculated.add(line);
                    calculatedMap.put(line, element);
                } else {
                    String tracker = availableTrackers.pop();
                    calculated.add(line);
                    calculatedMap.put(line, new ScoreboardElement(this, sidebar, line, tracker, expectedScore));
                }
            }
        }

        buffered.clear();
        buffered.addAll(calculated);
    }

    public boolean isRegistered(ScoreboardLine line) {
        return calculatedMap.containsKey(line);
    }

    public ScoreboardElement get(ScoreboardLine line) {
        return calculatedMap.get(line);
    }

    public Player getOwner() {
        return owner;
    }

    public void setSidebarName(String sidebarName) {
        if (!sidebar.getName().equals(sidebarName)) {
            sidebar.setDisplayName(sidebarName);
        }
    }

    public org.bukkit.scoreboard.Scoreboard getHandle() {
        return scoreboard;
    }

    public Team getOrMakeTeam(String name) {
        Team team = scoreboard.getTeam(name);
        if (team != null) return team;
        return scoreboard.registerNewTeam(name);
    }

    void returnTracker(String tracker) {
        availableTrackers.add(tracker);
    }

    Set<String> getCustomTrackers() {
        return customTrackers;
    }
}
