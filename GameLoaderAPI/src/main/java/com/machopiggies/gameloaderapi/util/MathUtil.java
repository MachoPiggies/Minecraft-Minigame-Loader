package com.machopiggies.gameloaderapi.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;

public class MathUtil {

    public static double distance2d(Location a, Location b) {
        return distance2d(a.toVector(), b.toVector());
    }

    public static double distance2d(Vector a, Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }

    public static double distance2dSquared(Location a, Location b) {
        return distance2dSquared(a.toVector(), b.toVector());
    }

    public static double distance2dSquared(Vector a, Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).lengthSquared();
    }

    public static double distance3d(Location a, Location b) {
        return distance3d(a.toVector(), b.toVector());
    }

    public static double distance3d(Vector a, Vector b) {
        return a.subtract(b).length();
    }

    public static double distance(Location a, Location b) {
        return distance(a.toVector(), b.toVector());
    }

    public static double distance(Vector a, Vector b) {
        return a.distance(b);
    }

    public static double distanceSquared(Location a, Location b) {
        return distanceSquared(a.toVector(), b.toVector());
    }

    public static double distanceSquared(Vector a, Vector b) {
        return a.distanceSquared(b);
    }

    public static Vector getTrajectory(Location from, Location to) {
        return getTrajectory(from.toVector(), to.toVector());
    }

    public static Vector getTrajectory(Vector from, Vector to) {
        return to.clone().subtract(from).normalize();
    }

    public static double round(double value, int places) {
        if (places >= 0) {
            long factor = (long) Math.pow(10, places);
            value = value * factor;
            long tmp = Math.round(value);
            return (double) tmp / factor;
        } else {
            throw new IllegalArgumentException("decimal places must be 0 or more");
        }
    }

    public static Location getLocationInCircumference(Location center, double radius, double angleInRadians) {
        double x = center.getX() + radius * Math.cos(angleInRadians);
        double z = center.getZ() + radius * Math.sin(angleInRadians);
        double y = center.getY();
        Location location = new Location(center.getWorld(), x, y, z);
        Vector difference = center.toVector().clone().subtract(location.toVector());
        location.setDirection(difference);
        return location;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static double trim(double number, int decimalPlaces) {
        if (decimalPlaces <= 0) {
            return (int) number;
        }

        StringBuilder decimalFormat = new StringBuilder("#.#");

        for (int i = 0; i < (decimalPlaces - 1); i++) {
            decimalFormat.append("#");
        }

        return Double.parseDouble(new DecimalFormat(decimalFormat.toString()).format(number));
    }
}
