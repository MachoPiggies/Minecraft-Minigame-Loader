package com.machopiggies.gameloader.gui.uis;

import com.machopiggies.gameloader.gui.GuiColor;
import com.machopiggies.gameloader.gui.MenuInterface;
import com.machopiggies.gameloader.gui.buttons.MenuInterfaceButton;
import com.machopiggies.gameloaderapi.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public abstract class GamePlayerListPanelMenu extends MenuInterface {
    protected Player player;
    protected Supplier<List<Player>> playerGetter;
    protected int page;

    public GamePlayerListPanelMenu(Player player, Supplier<List<Player>> playerGetter) {
        super("Games", 45);
        this.player = player;
        this.playerGetter = playerGetter;
        this.page = 0;

        for (int i = 0; i < 9; i++) {
            set(i, new MenuInterfaceButton(createSpacer(GuiColor.GRAY)));
            set(i + 36, new MenuInterfaceButton(createSpacer(GuiColor.GRAY)));
        }

        update();
    }

    @Override
    public void update() {

        List<Player> players = playerGetter.get();

        double exactMaxPage = (double) players.size() / 27;
        int maxPage;
        if (exactMaxPage > 0 && exactMaxPage < 1) {
            maxPage = 0;
        } else {
            maxPage = (int) Math.ceil(exactMaxPage);
        }

        if (maxPage > 0 && page < maxPage) {
            set(26, new MenuInterfaceButton(new ItemBuilder(Material.ARROW)
                    .setDisplayName(ChatColor.GOLD + "Next")
                    .build(), (m,e) -> {
                page += 1;
                update();
            }));
        }

        if (page > 0) {
            set(18, new MenuInterfaceButton(new ItemBuilder(Material.ARROW)
                    .setDisplayName(ChatColor.GOLD + "Previous")
                    .build(), (m,e) -> {
                page -= 1;
                update();
            }));
        }

        List<Player> concise = players.subList(page * 28, Math.min((page * 28) + (players.size() - (page * 28)), (page + 1) * 28));

        int x = 10, y = 0;
        for (Player player : concise) {
            set(x + (y * 9), createPlayerButton(player));
            if (x % 8 == 0) {
                y++;
                x = 0;
            } else {
                x++;
            }
        }
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

    public abstract MenuInterfaceButton createPlayerButton(Player player);
}
