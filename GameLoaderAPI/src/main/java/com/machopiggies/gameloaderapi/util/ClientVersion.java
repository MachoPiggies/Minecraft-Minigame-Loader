package com.machopiggies.gameloaderapi.util;

public enum ClientVersion {
    Version1_13("1.13", 393),
    Version1_9("1.9", 48),
    Version1_8("1.8", Integer.MIN_VALUE);

    private final String friendlyName;
    private final int minimum;

    ClientVersion(String friendlyName, int minimum) {
        this.friendlyName = friendlyName;
        this.minimum = minimum;
    }

    public String friendlyName() {
        return friendlyName;
    }

    public boolean atOrAbove(ClientVersion other) {
        return ordinal() <= other.ordinal();
    }

    public static ClientVersion fromInt(int version) {
        for (ClientVersion test : values()) {
            if (version < test.minimum) continue;
            return test;
        }

        return null;
    }
}
