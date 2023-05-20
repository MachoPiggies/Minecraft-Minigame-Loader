package com.machopiggies.gameloader.gui.uis;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.gui.MenuInterface;
import com.machopiggies.gameloader.gui.buttons.MenuInterfaceButton;
import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.game.GameManager;
import com.machopiggies.gameloaderapi.game.GameRunner;
import com.machopiggies.gameloaderapi.game.GameState;
import com.machopiggies.gameloaderapi.player.GameHost;
import com.machopiggies.gameloaderapi.util.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
        this.gm = Core.getGameManager();

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

        if (gm.getGameRunner() != null && gm.getGameRunner().getState().ordinal() >= GameState.LOADING.ordinal()) {
            set(9, new MenuInterfaceButton(new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Stop Game")
                    .build(), (g,e) -> {
                if (gm.getGameRunner().getState().ordinal() >= GameState.LOADING.ordinal()) {
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
            menu.setPreviousGui(this);
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
            menu.setPreviousGui(this);
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
                menu.setPreviousGui(this);
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
            menu.setPreviousGui(this);
            menu.launch(player);
        }));

        set(8, new MenuInterfaceButton(new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Settings")
                .build(), (g,e) -> {
            GameSettingsPanelMenu menu = new GameSettingsPanelMenu(player);
            menu.setPreviousGui(this);
            menu.launch(player);
        }));

        if (!gm.getSettings().isAutoStart() && !gm.getSettings().doGameRotation()) {
            if (gm.getGameVote() != null && gm.getGameVote().isActive()) {
                set(21, new MenuInterfaceButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                        .setDisplayName(Message.HEADER + ChatColor.BOLD + "Game Voting")
                        .addGlow()
                        .build(), (g, e) -> {
                    gm.getGameVote().stop();
                    new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " has stopped the game vote!").send(player);
                    PlayerUtil.playSoundToAll(Sound.NOTE_PLING, 1f, 1f);
                    update();
                }));
            } else {
                set(21, new MenuInterfaceButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                        .setDisplayName(Message.HEADER + ChatColor.BOLD + "Game Voting")
                        .build(), (g, e) -> {
                    if (gm.getSettings().isAutoStart()) return;
                    if (gm.getSettings().doGameRotation()) return;
                    gm.startGameVote();
                    new Message("Game", Message.HEADER + player.getName() + Message.DEFAULT + " has started a game vote!").send(player);

                    TextComponent command = new TextComponent("Click to vote!");
                    command.setColor(ChatColor.LIGHT_PURPLE.asBungee());
                    command.setBold(true);
                    command.setUnderlined(true);

                    TextComponent hoverMessage = new TextComponent("Click to open vote gui!");
                    hoverMessage.setColor(ChatColor.GOLD.asBungee());

                    command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hoverMessage}));
                    command.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gameloader gamevote"));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        new Message(command).sendSpecial(p);
                    }

                    PlayerUtil.playSoundToAll(Sound.LEVEL_UP, 1f, 1f);
                    update();
                }));
            }
        } else {
            set(21, new MenuInterfaceButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                    .setDisplayName(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Game Voting")
                    .setLore("")
                    .addLore(TextUtil.wrap(ChatColor.RED + "Game votes can only be started when auto-start and game rotation are both turned off!", 36))
                    .build()));
        }

        set(23, new MenuInterfaceButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                .setDisplayName(Message.HEADER + ChatColor.BOLD + "Map Voting")
                .build(), (g,e) -> {

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
}
