package com.machopiggies.gameloaderapi.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Creates a kit for a player that is loaded onto them when the game starts, which can include items, abilities or other perks
 */
public interface GameKit {
    /**
     * The internal name of the kit, so it can be identified in debugging and so kits with the same name aren't cross-confused
     * @return the internal name of the kit
     */
    String getName();

    /**
     * Sets the internal name of a kit
     * @param name the internal name
     */
    void setName(String name);

    /**
     * Gets the display name of a kit which is what is shown to players
     * @return the name to give players
     */
    String getDisplayName();

    /**
     * Sets the display name which is what is shown to players
     * @param displayName the name to show players
     */
    void setDisplayName(String displayName);

    /**
     * Gets the description of a kit which is shown to players, lines extended over 36 chars will be new-lined
     * @return the description for the kit
     */
    String[] getDescription();

    /**
     * Sets the description of a kit
     * @param description the kits description
     */
    void setDescription(String... description);

    /**
     * Sets the description of a kit
     * @param description the kits description
     */
    void setDescription(List<String> description);

    /**
     * Gets the {@link ItemStack} for this kit to use in UIs
     * @return the {@link ItemStack} for UIs
     */
    ItemStack getIcon();

    /**
     * Sets an {@link ItemStack} item as the kit icon in UIs
     * @param icon the icon for the kit
     */
    void setIcon(ItemStack icon);

    /**
     * Checks if a player has the permission needed to use this kit, checks if user has permission
     * @param player the player to check
     * @return if the player can use the kit
     */
    boolean has(Player player);

    /**
     * Applies a kit onto a player, to be used when a game is in the final countdown stage
     * @param player loads the kit onto the player
     */
    void apply(Player player);

    /**
     * Selects a kit for a player ready to be applied
     * @param player player to load the kit on
     */
    void select(Player player);

    /**
     * Deselects a kit for a player
     * @param player player to unload the kit on
     */
    void deselect(Player player);
}
