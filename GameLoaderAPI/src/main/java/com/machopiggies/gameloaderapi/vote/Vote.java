package com.machopiggies.gameloaderapi.vote;

import java.util.Map;
import java.util.UUID;

public interface Vote<E> extends Runnable {

    boolean isActive();
    int getCountdown();
    int getVotes(E object);
    void addVote(E object, UUID uuid);
    void removeVote(E object, UUID uuid);
    E getWinner();
    void addItem(VoteOption<E> option);
    VoteOption<E> getOptionFromObject(E object);

    void start();
    void stop();
    void setCallback(VoteCallback callback);
}
