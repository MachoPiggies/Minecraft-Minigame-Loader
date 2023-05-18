package com.machopiggies.gameloaderapi.scoreboard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class ScoreboardElement {
	private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)" + ChatColor.COLOR_CHAR + "[0-9A-F]");
	private static final char[] CHARS = "1234567890abcdefklmnor".toCharArray();

	private static final List<String> HAS_COLOR_TRACKERS = new ArrayList<>();

	static {
		for (char c : CHARS) {
			HAS_COLOR_TRACKERS.add(ChatColor.COLOR_CHAR + "" + c);
		}
	}

	private static int COUNTER = 0;

	private MainScoreboard scoreboard;
	private final Objective sidebar;
	private ScoreboardLine line;
	private Team team;
	private String tracker;
	private String customTracker;
	private final Set<String> customTrackerTracker;
	private String oldValue;
	private int lineNumber;

	public ScoreboardElement(MainScoreboard scoreboard, Objective sidebar, ScoreboardLine line, String tracker, int lineNumber) {
		this.scoreboard = scoreboard;
		this.sidebar = sidebar;
		this.line = line;
		this.tracker = tracker;
		customTrackerTracker = scoreboard.getCustomTrackers();
		team = scoreboard.getHandle().registerNewTeam("SBE" + COUNTER++);
		team.addEntry(this.tracker);
		this.lineNumber = lineNumber;
		this.sidebar.getScore(this.tracker).setScore(this.lineNumber);
	}

	public void write(int value) {
		write(String.valueOf(value));
	}

	public void write(String value) {
		if (value.equals(oldValue)) return;

		oldValue = value;

		if (value.length() <= 16) {
			if (!StringUtils.equals(team.getPrefix(), value)) team.setPrefix(value);
			if (!StringUtils.equals(team.getSuffix(), "")) team.setSuffix("");

			clearCustomTracker();
		} else {
			String left = value.substring(0, 16);
			String right = value.substring(16);
			String ending = ChatColor.getLastColors(left);
			right = ending + right;

			if (right.length() <= 16) {
				if (!StringUtils.equals(team.getPrefix(), left)) team.setPrefix(left);
				if (!StringUtils.equals(team.getSuffix(), right)) team.setSuffix(right);

				clearCustomTracker();
			} else {
				String temp = value.substring(16);
				temp = ending + temp;
				Matcher matcher = COLOR_PATTERN.matcher(ending);
				boolean hasColors = matcher.find();
				if (!hasColors) temp = ChatColor.WHITE + temp;

				String tracker = null;
				int index = 0;

				while (tracker == null) {
					String temp1 = HAS_COLOR_TRACKERS.get(index++) + temp;
					String substr = temp1.length() <= 40 ? temp1 : temp1.substring(0, 40);
					if (substr.equals(customTracker) || customTrackerTracker.add(substr)) {
						tracker = substr;
						temp = temp1;
					}
				}

				if (customTracker == null || !customTracker.equals(tracker)) {
					clearCustomTracker();
				}

				if (temp.length() <= 40) {
					if (customTracker == null) {
						customTracker = temp;

						scoreboard.getHandle().resetScores(this.tracker);

						team.addEntry(customTracker);
						sidebar.getScore(customTracker).setScore(lineNumber);
					}

					if (!StringUtils.equals(team.getPrefix(), left)) team.setPrefix(left);
					if (!StringUtils.equals(team.getSuffix(), "")) team.setSuffix("");
				} else {
					right = temp.substring(40);

					if (right.length() > 16) {
						right = right.substring(0, 16);
						System.out.println("WARNING: Trimmed suffix from '" + temp.substring(40) + "' to '" + right + "'");
					}

					if (customTracker == null) {
						customTracker = tracker;

						scoreboard.getHandle().resetScores(this.tracker);

						team.addEntry(customTracker);
						sidebar.getScore(customTracker).setScore(lineNumber);
					}

					if (!StringUtils.equals(team.getPrefix(), left)) team.setPrefix(left);
					if (!StringUtils.equals(team.getSuffix(), right)) team.setSuffix(right);
				}
			}
		}
	}

	protected Team getHandle() {
		return team;
	}

	protected String getTracker() {
		return customTracker != null ? customTracker : tracker;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
		sidebar.getScore(getTracker()).setScore(this.lineNumber);
	}

	public void delete() {
		team.unregister();
		scoreboard.getHandle().resetScores(tracker);
		if (customTracker != null) {
			scoreboard.getHandle().resetScores(customTracker);
			customTrackerTracker.remove(customTracker);
		}
		scoreboard.returnTracker(tracker);
		team = null;
		scoreboard = null;
		tracker = null;
	}

	private void clearCustomTracker() {
		if (customTracker != null) {
			scoreboard.getHandle().resetScores(customTracker);
			team.removeEntry(customTracker);
			customTrackerTracker.remove(customTracker);
			customTracker = null;

			team.addEntry(tracker);
			sidebar.getScore(tracker).setScore(lineNumber);
		}
	}
}
