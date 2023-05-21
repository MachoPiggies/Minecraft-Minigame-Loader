package com.machopiggies.gameloaderapi.util;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PacketUtil {

    public enum NMSType {
        CRAFTBUKKIT,
        MINECRAFT
    }

    public static int getPlayerProtocolVersion(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
            return (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ClientVersion getPlayerVersion(Player player) {
        return ClientVersion.fromInt(getPlayerProtocolVersion(player));
    }

    @Deprecated
    public static void sendTitle(Player player, String jsonTitleString, String jsonSubtitleString, String jsonActionbarString, int fadeInTime, int showTime, int fadeOutTime) {
        try {
            if (jsonTitleString != null) {
                Object titleEnum = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, jsonTitleString);
                Constructor<?> titleConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getClassNMS("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = titleConstructor.newInstance(titleEnum, chatSerializer, fadeInTime, showTime, fadeOutTime);
                sendPacket(player, packet);
            }

            if (jsonSubtitleString != null) {
                Object subtitleEnum = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object ChatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, jsonSubtitleString);
                Constructor<?> subtitleConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getClassNMS("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = subtitleConstructor.newInstance(subtitleEnum, ChatSerializer, fadeInTime, showTime, fadeOutTime);
                sendPacket(player, packet);
            }

            if (jsonActionbarString != null) {
                Object chatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, jsonActionbarString);
                Constructor<?> actionbarConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutChat")).getConstructor(getClassNMS("IChatBaseComponent"), byte.class);
                Object packet = actionbarConstructor.newInstance(chatSerializer, (byte) 2);
                sendPacket(player, packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        if (title == null && subtitle == null) return;
        try {
            List<Object> packets = new ArrayList<>();
            if (title != null) {
                String json = ChatUtil.getAsTitleJson(TextComponent.fromLegacyText(title));
                Object titleEnum = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, json);
                Constructor<?> titleConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getClassNMS("IChatBaseComponent"));
                packets.add(titleConstructor.newInstance(titleEnum, chatSerializer));
            }

            if (subtitle != null) {
                String json = ChatUtil.getAsTitleJson(TextComponent.fromLegacyText(subtitle));
                Object subtitleEnum = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object ChatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, json);
                Constructor<?> subtitleConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getClassNMS("IChatBaseComponent"));
                packets.add(subtitleConstructor.newInstance(subtitleEnum, ChatSerializer));
            }

            Constructor<?> timingsConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(int.class, int.class, int.class);
            packets.add(timingsConstructor.newInstance(fadeInTime, showTime, fadeOutTime));
            for (Object packet : packets) {
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChatMessage(Player player, String jsonString) {
        try {
            Object chatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, jsonString);
            Constructor<?> chatConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutChat")).getConstructor(getClassNMS("IChatBaseComponent"), byte.class);
            Object packet = chatConstructor.newInstance(chatSerializer, (byte) 1);
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getClassNMS("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getClassNMS(String methodName) {
        return getClassNMS(methodName, NMSType.MINECRAFT);
    }

    public static Class<?> getClassNMS(String methodName, NMSType type, String... extraArgs) {
        StringBuilder extra = new StringBuilder();
        for (String arg : extraArgs) {
            extra.append(arg).append(".");
        }
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (type) {
            case CRAFTBUKKIT:
                try {
                    return Class.forName("org.bukkit.craftbukkit." + version + "." + extra + methodName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            case MINECRAFT:
                try {
                    return Class.forName("net.minecraft.server." + version + "." + extra + methodName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public static Object getPrivateFieldObject(String fieldName, Class<?> clss, Object obj) {
        try {
            Field field = clss.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void setPrivateFieldValue(String fieldName, Object object, T value, Class<?> clss) {
        try {
            Field field = clss.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
