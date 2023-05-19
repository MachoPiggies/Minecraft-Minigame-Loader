package com.machopiggies.gameloader.commands;

import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final static Map<PluginCommand, CommandExecutor> executors;

    protected Core core;
    protected String command;
    protected String desc;
    protected String usage;
    protected String permission;
    protected String[] aliases;

    public CommandManager(String command, String desc, String permission, String usage, String... aliases) {
        command = command.toLowerCase();
        this.core = Core.getSelf();
        this.command = command;
        this.desc = desc;
        this.usage = usage;
        this.permission = permission;
        aliases = aliases != null ? aliases : new String[0];
        this.aliases = aliases;

        SimpleCommandMap commands = getCommandMap();
        Bukkit.getLogger().info(commands + "");

        if (commands != null) {
            try {
                Constructor<PluginCommand> privateConst = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                privateConst.setAccessible(true);
                PluginCommand cmd = Bukkit.getPluginCommand(command);
                cmd = cmd == null ? privateConst.newInstance(command, Core.getSelf()) : cmd;
                cmd.setPermission(permission);
                cmd.setPermissionMessage(ChatColor.RED + "You do not have permission to use this command!");
                cmd.setDescription(desc);
                cmd.setUsage(usage);
                cmd.setAliases(Arrays.asList(aliases));
                cmd.setExecutor(this);
                cmd.setTabCompleter(this);
                commands.register(command, cmd);

                executors.put(cmd, this);
            } catch(InstantiationException | IllegalAccessException | NoSuchMethodException |
                    InvocationTargetException e) {
                Bukkit.getLogger().severe("An error occurred whilst trying to load a command. If restarting your server does not fix this, please contact the plugin developer with the following error log!");
            }
        }
    }

    //

    public boolean permissable(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean permissable(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public void mustBePlayer(CommandSender sender) {
        sender.sendMessage("You must be a player to execute this command!");
    }

    //
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }

    public static void activateCmds() {
        new GameLoaderCommand();
    }

    public static Map<PluginCommand, CommandExecutor> getExecutors() {
        return executors;
    }

    public static SimpleCommandMap getCommandMap() {
        SimpleCommandMap commands = null;
        try {
            Object cserver = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".CraftServer").cast(Bukkit.getServer());
            commands = (SimpleCommandMap) cserver.getClass().getMethod("getCommandMap").invoke(cserver);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("An error occurred whilst trying to get commandmap. If restarting your server does not fix this, please contact the plugin developer with the following error log!");
            e.printStackTrace();
        }
        return commands;
    }

    static {
        executors = new HashMap<>();
    }
}
