package com.machopiggies.gameloaderapi.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    public static String BoldGreen = ChatColor.GREEN + String.valueOf(ChatColor.BOLD);

    protected String message, header;
    protected BaseComponent[] components;

    public Message(String header, String message) {
        this.header = header;
        this.message = message;
        this.components = null;
    }

    public Message(String message) {
        this.header = "Mutinies";
        this.message = message;
        this.components = null;
    }

    public Message(BaseComponent... components) {
        this.components = components;
    }

    public static String HEADER = String.valueOf(ChatColor.GOLD);
    public static String DEFAULT = String.valueOf(ChatColor.GRAY);
    public static String ERROR = String.valueOf(ChatColor.RED);
    public static String CATEGORY = String.valueOf(ChatColor.WHITE);
    public static String VARIABLE = String.valueOf(ChatColor.LIGHT_PURPLE);

    public boolean isSpecial() {
        return components != null;
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(formatMessage(header, message));
        } else {
            Bukkit.getConsoleSender().sendMessage(formatMessage(header, message));
        }
    }

    public void sendSpecial(CommandSender sender) {
        if (components == null) {
            throw new NullPointerException("there is no special message to send");
        }
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(components);
        } else {
            new Message("Console", TextComponent.toPlainText(components)).send(sender);
        }
    }

    public void broadcast() {
        Bukkit.broadcastMessage(formatMessage(header, message));
    }

    public void console() {
        console(true);
    }

    public void console(boolean color) {
        if (color) {
            Bukkit.getConsoleSender().sendMessage(formatMessage(header, message));
        } else {
            Bukkit.getLogger().info(formatMessage(header, message));
        }
    }

    public void sendError(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(formatError(header, message));
        } else {
            Bukkit.getConsoleSender().sendMessage(formatError(header, message));
        }
    }

    public String formatMessage(String header, String message) {
        if (header != null) {
            return CATEGORY + "[" + VARIABLE + header + CATEGORY + "] " + DEFAULT + message;
        } else {
            return message;
        }
    }

    protected String formatError(String header, String message) {
        return CATEGORY + "[" + VARIABLE + header + CATEGORY + "] " + ERROR + message;
    }

    public String getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }

    public BaseComponent[] getComponents() {
        return components;
    }
}
