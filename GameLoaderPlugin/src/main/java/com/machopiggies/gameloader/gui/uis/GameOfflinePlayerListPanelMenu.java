package com.machopiggies.gameloader.gui.uis;

import com.machopiggies.gameloader.gui.GuiColor;
import com.machopiggies.gameloader.gui.MenuInterface;
import com.machopiggies.gameloader.gui.buttons.MenuInterfaceButton;
import com.machopiggies.gameloaderapi.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public abstract class GameOfflinePlayerListPanelMenu extends MenuInterface {
    protected Player player;
    protected Supplier<List<OfflinePlayer>> playerGetter;
    protected int page;

    public GameOfflinePlayerListPanelMenu(Player player, Supplier<List<OfflinePlayer>> playerGetter) {
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

        List<OfflinePlayer> players = playerGetter.get();

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

        List<OfflinePlayer> concise = players.subList(page * 28, Math.min((page * 28) + (players.size() - (page * 28)), (page + 1) * 28));

        int x = 10, y = 0;
        for (OfflinePlayer player : concise) {
            set(x + (y * 9), createOfflinePlayerButton(player));
            if (x % 8 == 0) {
                y++;
                x = 0;
            } else {
                x++;
            }
        }
    }

    public abstract MenuInterfaceButton createOfflinePlayerButton(OfflinePlayer player);
}