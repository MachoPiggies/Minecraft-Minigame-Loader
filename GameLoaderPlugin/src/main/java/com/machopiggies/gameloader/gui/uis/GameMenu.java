package com.machopiggies.gameloader.gui.uis;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.game.ServerGameManager;
import com.machopiggies.gameloader.gui.MenuInterface;
import com.machopiggies.gameloader.gui.buttons.MenuInterfaceButton;
import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.game.GameManager;
import com.machopiggies.gameloaderapi.game.GameRunner;
import com.machopiggies.gameloaderapi.player.GameHost;
import com.machopiggies.gameloaderapi.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameMenu extends MenuInterface {

    private Player player;
    private GameManager gm;

    public GameMenu(Player player) {
        super("Game", 27);
        this.player = player;
        this.gm = Manager.require(ServerGameManager.class, Core.getSelf());

        update();
    }

    @Override
    protected void update() {
        if (gm.isGameQueued()) {
            if (!gm.getGameRunner().getState().isStarted() && !gm.getGameRunner().isCountdown()) {
                set(0, new MenuInterfaceButton(new ItemBuilder(Material.EMERALD_BLOCK)
                        .setDisplayName(ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "Start Game")
                        .build(), (g,e) -> {
                    if (!gm.getGameRunner().getState().isStarted()) {
                        gm.getGameRunner().startCountdown();
                        new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " has started the game!").send(player);
                        PlayerUtil.playSoundToAll(Sound.NOTE_PLING, 1f, 1f);
                    }
                    update();
                }));
            } else {
                set(0, new MenuInterfaceButton(new ItemBuilder(Material.EMERALD_BLOCK)
                        .addGlow()
                        .setDisplayName(ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "Starting... (Start Game)")
                        .build()));
            }
        } else {
            set(0, new MenuInterfaceButton(new ItemBuilder(Material.COAL_BLOCK)
                    .addGlow()
                    .setDisplayName(ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "No Game Queued (Start Game)")
                    .build()));
        }

        if (gm.getGameRunner() != null && gm.getGameRunner().getState().isStarted()) {
            set(9, new MenuInterfaceButton(new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Stop Game")
                    .build(), (g,e) -> {
                if (gm.getGameRunner().getState().isStarted()) {
                    gm.getGameRunner().stopCountdown();
                    new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " has stopped the game!").send(player);
                    PlayerUtil.playSoundToAll(Sound.NOTE_PLING, 1f, 1f);
                }
                update();
            }));
        } else {
            set(9, new MenuInterfaceButton(new ItemBuilder(Material.COAL_BLOCK)
                    .addGlow()
                    .setDisplayName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "No Running Game (Stop Game)")
                    .build()));
        }

        set(2, new MenuInterfaceButton(new ItemBuilder(Material.BOOKSHELF)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Select Game")
                .build(), (g,e) -> {
            GameListMenu menu = new GameListMenu(player) {
                @Override
                protected MenuInterfaceButton createGameButton(Game game) {
                    return new MenuInterfaceButton(new ItemBuilder(game.getInfo().getItem().clone())
                            .setDisplayName(ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + game.getInfo().getName() + " (" + game.getInfo().getInternalName() + ")")
                            .setLore("")
                            .addLore(TextUtil.wrap(Message.DEFAULT + game.getInfo().getDescription(), 36))
                            .addLore("")
                            .addLore(Message.HEADER + "Click" + Message.DEFAULT + " to select game!")
                            .build(), (g,e) -> clicked(game));
                }

                @Override
                protected void clicked(Game game) {
                    if (gm.getGameRunner() == null || !gm.getGameRunner().getState().isStarted()) {
                        gm.queueGame(game);
                        new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " set the game to " + Message.HEADER + game.getInfo().getName() + Message.DEFAULT + "!").send(player);
                        new GameMenu(player).launch(player);
                        PlayerUtil.playSoundToAll(Sound.NOTE_PLING, 1f, 1f);
                    }
                }
            };
            menu.launch(player);
        }));

        set(11, new MenuInterfaceButton(new ItemBuilder(Material.PAPER)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Game Rotation")
                .build(), (g,e) -> {
            GameListMenu menu = new GameListMenu(player) {
                @Override
                protected MenuInterfaceButton createGameButton(Game game) {
                    return new MenuInterfaceButton(new ItemBuilder(game.getInfo().getItem().clone())
                            .setDisplayName((gm.isInRotation(game) ? ChatColor.GREEN + String.valueOf(ChatColor.BOLD) : ChatColor.RED + String.valueOf(ChatColor.BOLD)) + game.getInfo().getName() + " (" + game.getInfo().getInternalName() + ")")
                            .addGlow(gm.isInRotation(game))
                            .setLore("")
                            .addLore(TextUtil.wrap(Message.DEFAULT + game.getInfo().getDescription(), 36))
                            .addLore("")
                            .addLore(TextUtil.wrap(Message.HEADER + "Click" + Message.DEFAULT + " to " + (gm.isInRotation(game) ? "remove from" : "add to") + " rotation!", 36))
                            .build(), (g,e) -> clicked(game));
                }

                @Override
                protected void clicked(Game game) {
                    if (gm.isInRotation(game)) {
                        gm.removeFromRotation(game);
                        new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " removed " + Message.HEADER + game.getInfo().getName() + Message.DEFAULT + " from the game rotation!").send(player);
                    } else {
                        gm.addToRotation(game);
                        new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " added " + Message.HEADER + game.getInfo().getName() + Message.DEFAULT + " to the game rotation!").send(player);
                    }
                    update();
                    PlayerUtil.playSoundToAll(Sound.NOTE_PLING, 1f, 1f);
                }

                @Override
                public void update() {
                    super.update();

                    set(39, new MenuInterfaceButton(new ItemBuilder(gm.getSettings().doGameRotation() ? Material.PISTON_STICKY_BASE : Material.PISTON_BASE)
                            .setDisplayName((gm.getSettings().doGameRotation() ? ChatColor.GREEN + String.valueOf(ChatColor.BOLD) : ChatColor.RED + String.valueOf(ChatColor.BOLD)) + "Random Game Rotation")
                            .setLore("")
                            .addLore(TextUtil.wrap(Message.DEFAULT + "Use a random order of the games in rotation or use the defined list?", 36))
                            .addLore("")
                            .addLore(TextUtil.wrap(Message.HEADER + "Click" + Message.DEFAULT + " to " + (gm.getSettings().doGameRotation() ? "disable" : "enable") + " ordered game rotation!", 36))
                            .build(), (g,e) -> {
                        gm.getSettings().setGameRotation(!gm.getSettings().doGameRotation());
                        new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " set game rotation type to " + Message.HEADER + (gm.getSettings().doGameRotation() ? "ordered" : "random") + Message.DEFAULT + "!").send(player);
                        update();
                        PlayerUtil.playSoundToAll(Sound.NOTE_PLING, 1f, 1f);
                    }));

                    set(41, new MenuInterfaceButton(new ItemBuilder(Material.CHEST)
                            .setDisplayName(Message.HEADER + ChatColor.BOLD + "Arrange Order")
                            .setLore("")
                            .addLore(TextUtil.wrap(Message.DEFAULT + "By arranging an order, you can have games launch in a specific order after each other.", 36))
                            .addLore("")
                            .addLore(TextUtil.wrap(Message.HEADER + "Click" + Message.DEFAULT + " to review game order!", 36))
                            .build()));
                }
            };
            menu.launch(player);
        }));

        set(4, new MenuInterfaceButton(new ItemBuilder(Material.GRASS)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Select Map")
                .build()));

        set(13, new MenuInterfaceButton(new ItemBuilder(Material.PAPER)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Map Rotation")
                .build()));

        if (gm.isHost(player.getUniqueId())) {
            set(6, new MenuInterfaceButton(new ItemBuilder(Material.BEACON)
                    .setDisplayName(Message.HEADER + ChatColor.BOLD + "Modify Hosts")
                    .build(), (g,e) -> {
                GameOfflinePlayerListPanelMenu menu = new GameOfflinePlayerListPanelMenu(player, () -> {
                    List<OfflinePlayer> players = new ArrayList<>();
                    players.addAll(gm.getResolvedHosts().keySet());
                    players.addAll(gm.getResolvedCoHosts().keySet());
                    return players;
                }) {

                    @Override
                    public MenuInterfaceButton createOfflinePlayerButton(OfflinePlayer player) {
                        String hostType = gm.isHost(player.getPlayer().getUniqueId()) ? "Host" : gm.isCoHost(player.getUniqueId()) ? "Co-host" : "None";
                        GameHost data = gm.getHosts().get(player.getUniqueId());
                        String addedAgo = "Unknown";
                        if (gm.isCoHost(player.getPlayer().getUniqueId())) {
                            if (data.getAddedAt() >= 0) {
                                Pair<Double, TimeUnit> conversion = TimeDateUtil.convert(System.currentTimeMillis() - data.getAddedAt(), 1);
                                addedAgo = conversion.getKey() + " " + conversion.getValue().name().toLowerCase();
                            }
                        }

                        return new MenuInterfaceButton(new ItemBuilder.PlayerSkull(player).getStackAsItemBuilder()
                                .setDisplayName(Message.HEADER + ChatColor.BOLD + player.getName())
                                .setLore("",
                                        Message.HEADER + "Type: " + Message.DEFAULT + hostType,
                                        Message.HEADER + "Added By: " + Message.DEFAULT + (data.getAddedBy() != null ? Bukkit.getOfflinePlayer(data.getAddedBy()).getName() : "System")
                                ).addLore(data.getAddedBy() != null, Message.HEADER + "Added At: " + Message.DEFAULT + addedAgo
                                ).build());
                    }
                };
                menu.launch(player);
            }));
        }

        set(15, new MenuInterfaceButton(new ItemBuilder.PlayerSkull(player).getStackAsItemBuilder()
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Players")
                .build(), (g,e) -> {
            GamePlayerListPanelMenu menu = new GamePlayerListPanelMenu(player, () -> new ArrayList<>(Bukkit.getOnlinePlayers())) {

                @Override
                public MenuInterfaceButton createPlayerButton(Player player) {
                    GameRunner runner = gm.getGameRunner();
                    String hostType = gm.isHost(player.getUniqueId()) ? "Host" : gm.isCoHost(player.getUniqueId()) ? "Co-host" : null;
                    if (runner != null) {
                        return new MenuInterfaceButton(new ItemBuilder.PlayerSkull(player).getStackAsItemBuilder()
                                .setDisplayName(Message.HEADER + ChatColor.BOLD + player.getName())
                                .setLore("")
                                .addLore(hostType != null, Message.HEADER + "Host: " + Message.DEFAULT + hostType)
                                .addLore(!runner.getSpectators().contains(player), Message.HEADER + "Team: " + (runner.getTeam(player) != null ? runner.getTeam(player).getFormattedName() : Message.DEFAULT + "N/A"))
                                .addLore(Message.HEADER + "State: " + Message.DEFAULT + (runner.getSpectators().contains(player) ? "Spectator" : "Playing"))
                                .build());
                    } else {
                        return new MenuInterfaceButton(new ItemBuilder.PlayerSkull(player).getStackAsItemBuilder()
                                .setDisplayName(Message.HEADER + ChatColor.BOLD + player.getName())
                                .setLore("",
                                        Message.HEADER + "Type: " + Message.DEFAULT + hostType
                                ).build());
                    }
                }
            };
            menu.launch(player);
        }));

        set(8, new MenuInterfaceButton(new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Settings")
                .build(), (g,e) -> {
            GameSettingsPanelMenu menu = new GameSettingsPanelMenu(player);
            menu.launch(player);
        }));

        set(21, new MenuInterfaceButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Game Voting")
                .build()));

        set(23, new MenuInterfaceButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                .setDisplayName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Map Voting")
                .build()));
    }
}
