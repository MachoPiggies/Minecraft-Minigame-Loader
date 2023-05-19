package com.machopiggies.gameloader.vote;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloaderapi.event.tick.TickEvent;
import com.machopiggies.gameloaderapi.event.tick.TickType;
import com.machopiggies.gameloaderapi.vote.Vote;
import com.machopiggies.gameloaderapi.vote.VoteCallback;
import com.machopiggies.gameloaderapi.vote.VoteOption;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class GameVote<E> implements Vote<E>, Listener {

    boolean doCountdown = false;
    int countdown = 30;
    VoteCallback callback;
    Map<VoteOption<E>, Set<UUID>> items;

    public GameVote() {
        this.items = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, Core.getSelf());
    }

    @Override
    public boolean isActive() {
        return doCountdown;
    }

    @Override
    public int getCountdown() {
        return countdown;
    }

    @Override
    public int getVotes(E object) {
        VoteOption<E> option = getOptionFromObject(object);
        if (option == null) return 0;
        return items.getOrDefault(option, new HashSet<>()).size();
    }

    @Override
    public VoteOption<E> getOptionFromObject(E object) {
        for (VoteOption<E> option : items.keySet()) {
            if (!option.equals(object)) continue;
            return option;
        }
        return null;
    }

    @Override
    public void addVote(E object, UUID uuid) {
        for (Map.Entry<VoteOption<E>, Set<UUID>> entry : items.entrySet()) {
            entry.getValue().remove(uuid);
            items.put(entry.getKey(), entry.getValue());
        }
        VoteOption<E> option = getOptionFromObject(object);
        if (option == null) return;
        if (!items.containsKey(option)) return;
        Set<UUID> set = items.getOrDefault(option, new HashSet<>());
        set.add(uuid);
        items.put(option, set);
    }

    @Override
    public void removeVote(E object, UUID uuid) {
        VoteOption<E> option = getOptionFromObject(object);
        if (option == null) return;
        if (!items.containsKey(option)) return;
        Set<UUID> set = items.getOrDefault(option, new HashSet<>());
        set.remove(uuid);
        items.put(option, set);
    }

    @Override
    public E getWinner() {
        int max = items.values().stream().map(Set::size).max(Integer::compare).orElse(0);
        List<E> games = new ArrayList<>();
        for (Map.Entry<VoteOption<E>, Set<UUID>> entry : items.entrySet()) {
            if (entry.getValue().size() != max) continue;
            games.add(entry.getKey().getObject());
        }
        return games.isEmpty() ? null : games.get(new Random().nextInt(games.size()));
    }

    @Override
    public void addItem(VoteOption<E> item) {
        items.put(item, new HashSet<>());
    }

    @Override
    public void start() {
        doCountdown = true;
    }

    @Override
    public void stop() {
        doCountdown = false;
        countdown = 30;
    }

    @EventHandler
    private void onTick(TickEvent event) {
        if (event.getType() != TickType.SEC) return;
        if (!doCountdown) return;
        if (countdown > 0) countdown--;
        if (countdown <= 0) {
            callback.run();
            stop();
        }
    }

    @Override
    public void setCallback(VoteCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        doCountdown = true;
    }
}
