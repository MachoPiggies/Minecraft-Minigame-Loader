package com.machopiggies.gameloaderapi.scoreboard.animations;

import org.bukkit.ChatColor;

@Deprecated
public class Scroll {
    private final String input;
    private String primary;
    private String secondary;
    private String tertiary;
    private boolean _bold;

    public Scroll(String input) {
        this.input = input;
        this.primary = ChatColor.WHITE.toString();
        this.secondary = primary;
        this.tertiary = primary;
    }

    public Scroll left(ChatColor colour) {
        primary = colour.toString();
        return this;
    }

    public Scroll mid(ChatColor colour) {
        secondary = colour.toString();
        return this;
    }

    public Scroll right(ChatColor colour) {
        tertiary = colour.toString();
        return this;
    }

    public Scroll bold() {
        _bold = true;
        return this;
    }

    public String[] build() {
        String[] output = new String[input.length() * 2];
        String[] primaryRun = getFrames(primary, secondary);
        String[] secondaryRun = getFrames(secondary, primary);

        System.arraycopy(primaryRun, 0, output, 0, input.length());
        System.arraycopy(secondaryRun, 0, output, input.length(), input.length());

        return output;
    }

    private String[] getFrames(String primary, String secondary) {
        String[] output = new String[input.length()];

        for (int i = 0; i < input.length(); i++) {
            StringBuilder builder = new StringBuilder(input.length() * 3)
                    .append(primary)
                    .append(_bold ? ChatColor.BOLD : "");

            for (int j = 0; j < input.length(); j++) {
                char c = input.charAt(j);

                if (j == i) {
                    builder.append(tertiary).append(_bold ? ChatColor.BOLD : "");
                } else if (j == i + 1) {
                    builder.append(secondary).append(_bold ? ChatColor.BOLD : "");
                }
                builder.append(c);
            }
            output[i] = builder.toString();
        }

        return output;
    }
}
