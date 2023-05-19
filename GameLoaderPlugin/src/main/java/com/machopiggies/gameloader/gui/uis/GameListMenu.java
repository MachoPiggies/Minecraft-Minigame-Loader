package com.machopiggies.gameloader.gui.uis;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.game.ServerGameManager;
import com.machopiggies.gameloader.gui.GuiColor;
import com.machopiggies.gameloader.gui.MenuInterface;
import com.machopiggies.gameloader.gui.buttons.MenuInterfaceButton;
import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.game.GameManager;
import com.machopiggies.gameloaderapi.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class GameListMenu extends MenuInterface {
    protected Player player;
    protected int page;
    protected GameManager gm;

    public GameListMenu(Player player) {
        super("Games", 45);
        this.player = player;
        this.page = 0;
        this.gm = Core.getGameManager();

        for (int i = 0; i < 9; i++) {
            set(i, new MenuInterfaceButton(createSpacer(GuiColor.GRAY)));
            set(i + 36, new MenuInterfaceButton(createSpacer(GuiColor.GRAY)));
        }

        update();
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

    @Override
    public void update() {
        double exactMaxPage = (double) gm.getGames().values().size() / 27;
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

        List<Game> concise = new ArrayList<>(gm.getGames().values()).subList(page * 28, Math.min((page * 28) + (gm.getGames().values().size() - (page * 28)), (page + 1) * 28));

        int x = 10, y = 0;
        for (Game game : concise) {
            set(x + (y * 9), createGameButton(game));
            if (x % 8 == 0) {
                y++;
                x = 0;
            } else {
                x++;
            }
        }
    }

    protected abstract MenuInterfaceButton createGameButton(Game game);

    protected abstract void clicked(Game game);
}
