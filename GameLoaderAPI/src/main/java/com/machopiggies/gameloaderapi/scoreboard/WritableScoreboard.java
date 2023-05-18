package com.machopiggies.gameloaderapi.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class WritableScoreboard extends MainScoreboard {

	private final List<WritableScoreboardLine> _lines;
	protected final List<String> _bufferedLines = new ArrayList<>(15);
	private final List<String> _drawnLines = new ArrayList<>(15);

	public WritableScoreboard(Plugin plugin) {
		this(null, plugin);
	}

	public WritableScoreboard(Player owner, Plugin plugin) {
		super(owner, plugin);

		List<WritableScoreboardLine> lines = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			lines.add(new WritableScoreboardLine());
		}
		_lines = Collections.unmodifiableList(lines);
	}

	public void write(String line) {
		_bufferedLines.add(line);
	}

	public void writeNewLine() {
		write("");
	}

	public void draw() {
		if (_bufferedLines.size() > 15) throw new IllegalStateException("Too many lines! (" + _bufferedLines.size() + " > 15)");

		if (_bufferedLines.size() > _drawnLines.size()) {
			for (int i = _drawnLines.size(); i < _bufferedLines.size(); i++) {
				super.register(_lines.get(i));
			}
		} else if (_bufferedLines.size() < _drawnLines.size()) {
			for (int i = _bufferedLines.size(); i < _drawnLines.size(); i++) {
				super.unregister(_lines.get(i));
			}
		}

		recalculate();

		for (int i = 0; i < _bufferedLines.size(); i++) {
			get(_lines.get(i)).write(_bufferedLines.get(i));
		}

		this._drawnLines.clear();
		this._drawnLines.addAll(this._bufferedLines);
		this._bufferedLines.clear();
	}

	public void reset() {
		this._bufferedLines.clear();
	}

	@Override
	public MainScoreboard register(ScoreboardLine line) {
		throw new IllegalArgumentException("You cannot register lines with a WritableMutiniesScoreboard!");
	}

	@Override
	public MainScoreboard unregister(ScoreboardLine line) {
		throw new IllegalArgumentException("You cannot unregister lines with a WritableMutiniesScoreboard!");
	}

	private static class WritableScoreboardLine implements ScoreboardLine {

	}
}
