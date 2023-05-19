package com.machopiggies.gameloader.commands;

import com.google.gson.Gson;
import com.machopiggies.gameloader.Core;
import com.machopiggies.gameloader.commands.anno.GameCommand;
import com.machopiggies.gameloader.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final static Map<PluginCommand, CommandExecutor> executors;

    protected Core core;
    protected String command;
    protected String desc;
    protected String usage;
    protected String permission;
    protected String[] aliases;

    protected Map<String, Subcommand> subcommands;

    public CommandManager() {
        GameCommand gCmd = getClass().getAnnotation(GameCommand.class);
        if (gCmd == null) return;
        this.core = Core.getSelf();
        this.command = gCmd.name().toLowerCase();
        this.desc = gCmd.description();
        this.usage = gCmd.usage();
        this.permission = gCmd.permission();
        this.aliases = gCmd.aliases();
        this.subcommands = new HashMap<>();

        SimpleCommandMap commands = getCommandMap();

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

        for (Method method : getClass().getDeclaredMethods()) {
            GameCommand gc = method.getAnnotation(GameCommand.class);
            if (gc == null) continue;
            subcommands.put(gc.name(), new Subcommand(
                    gc.name().toLowerCase(),
                    gc.description(),
                    gc.usage(),
                    gc.permission(),
                    false,
                    method
            ));
            for (String alias : gc.aliases()) {
                subcommands.put(alias, new Subcommand(
                        gc.name().toLowerCase(),
                        gc.description(),
                        gc.usage(),
                        gc.permission(),
                        true,
                        method
                ));
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length > 0) {
            Subcommand subcommand = subcommands.get(args[0].toLowerCase());
            if (subcommand != null) {
                if (!sender.hasPermission(subcommand.getPermission())) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }
                List<String> newArgs = new ArrayList<>(Arrays.asList(args));
                newArgs.remove(0);
                try {
                    subcommand.getMethod().invoke(this, sender, command, s, newArgs.toArray(new String[0]));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        processCommand(sender, command, s, args);
        return true;
    }

    public void processCommand(CommandSender sender, Command command, String s, String[] args) {

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

    public static class Subcommand {
        private final String name;
        private final String description;
        private final String usage;
        private final String permission;
        private final boolean alias;
        private final Method method;

        public Subcommand(String name, String description, String usage, String permission, boolean alias, Method method) {
            this.name = name;
            this.description = description;
            this.usage = usage;
            this.permission = permission;
            this.alias = alias;
            this.method = method;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getUsage() {
            return usage;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isAlias() {
            return alias;
        }

        public Method getMethod() {
            return method;
        }
    }
}
