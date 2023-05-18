package com.machopiggies.gameloaderapi.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.function.Function;

public class TextUtil {

    private static final Map<String, List<String>> generatedStrings;

    public static final int DefaultWrap = 36;
    public static final int MaxWrap = 48;

    public static String randomString(int length, String... blacklist) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Random rand = new Random();
        while (i < length) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
            i++;
        }

        String str = sb.toString();
        List<String> uses = generatedStrings.getOrDefault(str, new ArrayList<>());
        if (blacklist != null && blacklist.length > 0) {
            for (String inst : blacklist) {
                if (uses.contains(inst)) {
                    randomString(length, blacklist);
                }
            }
        }

        uses.add(str);
        generatedStrings.put(str, uses);
        return str;
    }

    public static String concat(Collection<String> list) {
        return concat(" ", list);
    }

    public static String concat(String... list) {
        return concat(" ", list);
    }

    public static String concat(String joiner, Collection<String> list) {
        return concat(joiner, list.toArray(new String[0]));
    }

    public static String concat(String joiner, String... list) {
        StringBuilder builder = new StringBuilder();
        for (String arg : list) {
            if (arg == null) continue;
            builder.append(arg).append(joiner);
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - joiner.length());
        }
        return builder.toString().trim();
    }

    public static List<String> wrap(String string, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();
        StringBuilder lineText = new StringBuilder();
        for (String word : StringUtils.split(string, " ")) {
            if (!lineText.toString().equals("")) {
                if (lineText.length() + 1 + word.length() > maxCharsPerLine) {
                    String previousColor = ChatColor.getLastColors(lineText.toString());
                    lines.add(lineText.toString().trim());
                    lineText = new StringBuilder(previousColor).append(word).append(" ");
                } else {
                    lineText.append(word).append(" ");
                }
            } else {
                lineText.append(word).append(" ");
            }
        }

        if (lineText.length() > 0) {
            lines.add(lineText.toString().trim());
        }

        return lines;
    }

    public static int countCharacter(char character, String string) {
        int amount = 0;
        for (char c : string.toCharArray()) {
            if (c != character) continue;
            amount++;
        }
        return amount;
    }

    public static List<String> insert(List<String> list, String insert, InsertArea area) {
        List<String> newList = new ArrayList<>();
        Function<String, String> method = str -> {
            if (area == InsertArea.BEFORE) {
                str = insert + str;
            } else {
                str += insert;
            }
            return str;
        };

        for (String str : list) {
            newList.add(method.apply(str));
        }

        return newList;
    }

    public static boolean isVowel(char character) {
        return Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U').contains(character);
    }

    public static boolean isAlphanumeric(String string) {
        return string.matches("^[a-zA-Z0-9]*$");
    }

    public enum InsertArea {
        BEFORE,
        AFTER
    }

    static {
        generatedStrings = new HashMap<>();
    }
}
