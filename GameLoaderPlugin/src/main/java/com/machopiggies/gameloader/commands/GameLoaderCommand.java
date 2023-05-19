package com.machopiggies.gameloader.commands;

import com.machopiggies.gameloader.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class GameLoaderCommand extends CommandManager {
    public GameLoaderCommand() {
        super("gameloader", "Parent command for gameloader", "gameloader.open", "/gameloader", "gl");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getLogger().severe(String.valueOf(sender));
        Bukkit.getLogger().severe(String.valueOf(command));
        Bukkit.getLogger().severe(s);
        Bukkit.getLogger().severe(Arrays.toString(args) + "");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return super.onTabComplete(sender, command, s, args);
    }
}
