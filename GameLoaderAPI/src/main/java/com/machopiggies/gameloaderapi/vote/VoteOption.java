package com.machopiggies.gameloaderapi.vote;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface VoteOption<E> {

    E getObject();
    String getName();
    String getDisplayName();
    ItemStack getIcon();
}
