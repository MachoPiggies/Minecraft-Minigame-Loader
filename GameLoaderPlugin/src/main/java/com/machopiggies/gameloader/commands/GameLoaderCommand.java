package com.machopiggies.gameloader.commands;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.commands.anno.GameCommand;
import com.machopiggies.gameloader.game.ServerGameManager;
import com.machopiggies.gameloader.gui.uis.GameMenu;
import com.machopiggies.gameloader.manager.Manager;
import com.machopiggies.gameloaderapi.game.GameManager;
import com.machopiggies.gameloaderapi.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@GameCommand(name = "gameloader", description = "Parent command for gameloader", permission = "gameloader.open", aliases = {"gl"})
public class GameLoaderCommand extends CommandManager {

    private GameManager gm;

    public GameLoaderCommand() {
        gm = Core.getGameManager();
    }

    @Override
    public void processCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getLogger().severe(String.valueOf(sender));
        Bukkit.getLogger().severe(String.valueOf(command));
        Bukkit.getLogger().severe(s);
        Bukkit.getLogger().severe(Arrays.toString(args) + "");
    }

    @GameCommand(name = "version", description = "Shows the loader version", permission = "gameloader.version", aliases = {"v"})
    public void getVersion(CommandSender sender, Command command, String s, String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(core.getPluginClassLoader().getResourceAsStream("version.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String git = properties.getProperty("build.git", "Unknown");
        git = git.equals("${describe}") ? "Unknown" : git;

        new Message(null, Message.HEADER + ChatColor.BOLD + "Build Version").send(sender);
        new Message(null, " " + Message.HEADER + ChatColor.BOLD + "User" + " " + ChatColor.GRAY + properties.getProperty("build.user", "Unknown")).send(sender);
        new Message(null, " " + Message.HEADER + ChatColor.BOLD + "Date" + " " + ChatColor.GRAY + properties.getProperty("build.date", "Unknown") + " [UTC]").send(sender);
        new Message(null, " " + Message.HEADER + ChatColor.BOLD + "Git" + " " + ChatColor.GRAY + git).send(sender);
    }

    @GameCommand(name = "menu", description = "Displays game information", permission = "gameloader.menu", aliases = {"m"})
    public void menu(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (gm.isHost(((Player) sender).getUniqueId()) || gm.isCoHost(((Player) sender).getUniqueId())) {
                new GameMenu((Player) sender).launch(sender);
            } else {
                notHost((Player) sender);
            }
        } else {
            mustBePlayer(sender);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return super.onTabComplete(sender, command, s, args);
    }

    public void notHost(Player player) {
        new Message("Command", "You do not have permission to execute this command!").send(player);
    }
}
