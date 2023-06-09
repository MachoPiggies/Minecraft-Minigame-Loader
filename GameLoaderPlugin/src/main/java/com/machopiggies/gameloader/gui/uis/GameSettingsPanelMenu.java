package com.machopiggies.gameloader.gui.uis;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.game.ServerGameManager;
import com.machopiggies.gameloader.gui.GuiColor;
import com.machopiggies.gameloader.gui.MenuInterface;
import com.machopiggies.gameloader.gui.buttons.MenuInterfaceButton;
import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloaderapi.game.GameManager;
import com.machopiggies.gameloaderapi.game.GameSettings;
import com.machopiggies.gameloaderapi.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class GameSettingsPanelMenu extends MenuInterface {

    Player player;
    GameManager gm;

    public GameSettingsPanelMenu(Player player) {
        super("Game", 54);
        this.player = player;
        this.gm = Core.getGameManager();

        update();
    }

    @Override
    public void update() {
        createConfigOption(11, "Unlock All Kits", Material.IRON_AXE, GameSettings::isKitsUnlocked, x -> x.setKitsUnlocked(!x.isKitsUnlocked()));

        createConfigOption(13, "Give Rewards", Material.GOLD_NUGGET, GameSettings::doRewards, x -> x.setDoRewards(!x.doRewards()));

        createConfigOption(15, "Use Team Balancing", Material.RAILS, GameSettings::useTeamBalancing, x -> x.setTeamBalancing(!x.useTeamBalancing()));

        createConfigOption(37, "Auto Start", Material.EMERALD_BLOCK, GameSettings::isAutoStart, x -> x.setAutoStart(!x.isAutoStart()));

        createConfigOption(39, "Game Timeout", Material.COMPASS, GameSettings::doGameTimeout, x -> x.setGameTimeout(!x.doGameTimeout()));

        createConfigOption(41, "Kick Inactive", Material.WATCH, GameSettings::doKickInactive, x -> x.setKickInactive(!x.doKickInactive()));

        createConfigOption(43, "Game Rotation", Material.REDSTONE_ORE, GameSettings::doGameRotation, x -> x.setGameRotation(!x.doGameRotation()));
    }

    private void createConfigOption(int slot, String label, ItemStack stack, Function<GameSettings, Boolean> getter, Consumer<GameSettings> setter) {
        boolean defValue = getter.apply(gm.getSettings());
        set(slot, new MenuInterfaceButton(new ItemBuilder(stack)
                .setDisplayName((defValue ? ChatColor.GREEN + String.valueOf(ChatColor.BOLD) : ChatColor.RED + String.valueOf(ChatColor.BOLD)) + label)
                .build()));
        set(slot + 9, new MenuInterfaceButton(new ItemBuilder(Material.INK_SACK)
                .setDurability(defValue ? GuiColor.LIME.getDyeColorId() : GuiColor.RED.getDyeColorId())
                .setDisplayName((defValue ? ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "Enabled" : ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Disabled"))
                .build(), (g,e) -> {
            setter.accept(gm.getSettings());
            gm.getSettings().saveSettings();
            update();
        }));
    }

    @Override
    public void setPreviousGui(MenuInterface previousGui) {
        if (previousGui != null) {
            set(0, new MenuInterfaceButton(new ItemBuilder(Material.ARROW)
                    .setDisplayName(ChatColor.GOLD + "Back")
                    .build(), (m,e) -> previousGui.launch(player)));
        }
    }

    @Override
    public void launch() {
        launch(player);
    }

    private void createConfigOption(int slot, String label, Material material, Function<GameSettings, Boolean> getter, Consumer<GameSettings> setter) {
        createConfigOption(slot, label, new ItemStack(material), getter, setter);
    }
}
