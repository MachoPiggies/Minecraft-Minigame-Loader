package com.machopiggies.gameloaderapi.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeDateUtil {

    public static String getSimpleDurationStringFromSeconds(long seconds) {
        if (seconds >= 60) {
            double mins = MathUtil.round((double) seconds / 60, 1);
            if (mins >= 60) {
                double hours = MathUtil.round(mins / 60, 1);
                if (hours >= 24) {
                    return trimTime(MathUtil.round(hours / 24, 1) == 1 ? MathUtil.round(hours / 24, 1) + " day" : MathUtil.round(hours / 24, 1) + "days");
                } else {
                    return trimTime(hours == 1 ? hours + " hour" : hours + " hours");
                }
            } else {
                return trimTime(mins == 1 ? mins + " minute" : mins + " minutes");
            }
        } else {
            return trimTime(seconds == 1 ? seconds + " second" : seconds + " seconds");
        }
    }

    public static String getSimpleDurationStringFromMilis(long milis) {
        return getSimpleDurationStringFromMilis(milis, true);
    }

    public static String getSimpleDurationStringFromMilis(long milis, boolean includeType) {
        if (milis >= 1000) {
            double secs = MathUtil.round((double) milis / 1000, 1);
            if (secs >= 60) {
                double mins = MathUtil.round(secs / 60, 1);
                if (mins >= 60) {
                    double hours = MathUtil.round(mins / 60, 1);
                    if (hours >= 24) {
                        return trimTime(MathUtil.round(hours / 24, 1) == 1 ? MathUtil.round(hours / 24, 1) + " day" : MathUtil.round(hours / 24, 1) + " days");
                    } else {
                        if (includeType) {
                            return trimTime(hours == 1 ? hours + " hour" : hours + " hours");
                        } else {
                            return trimTime(Double.toString(hours));
                        }
                    }
                } else {
                    if (includeType) {
                        return trimTime(mins == 1 ? mins + " minute" : mins + " minutes");
                    } else {
                        return trimTime(Double.toString(mins));
                    }
                }
            } else {
                if (includeType) {
                    return trimTime(secs == 1 ? secs + " second" : secs + " seconds");
                } else {
                    return trimTime(Double.toString(secs));
                }
            }
        } else {
            if (includeType) {
                return trimTime(milis / 1000 == 1 ? milis / 1000 + " second" : milis / 1000 + " seconds");
            } else {
                return trimTime(Double.toString(milis / 1000D));
            }
        }
    }

    public static String trimTime(String time) {
        int lowest = Integer.MAX_VALUE;
        String[] numbersToLookFor = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String num : numbersToLookFor) {
            if (time.contains(num) && time.indexOf(num) < lowest) {
                lowest = time.indexOf(num);
            }
        }
        if (lowest != Integer.MAX_VALUE) {
            time = time.substring(lowest);
            return time.replace(".0", "");
        } else {
            return "0";
        }
    }

    public static String getTimeAndDateFromUnix(long millis) {
        return new SimpleDateFormat("MM'/'dd'/'yyyy '('h:mm:ss a') [EST]'").format(millis - 7200000);
    }

    public static String getTimeAndDateFromEpoch(long seconds) {
        return new SimpleDateFormat("MM'/'dd'/'yyyy '('h:mm:ss a') [EST]'").format(seconds * 1000 - 7200000);
    }

    public static String getTimeAndDateFromEpochFormally(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        String str = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (str.endsWith("1")) {
            str += "st";
        } else if (str.endsWith("2")) {
            str += "nd";
        } else if (str.endsWith("3")) {
            str += "rd";
        } else {
            str += "th";
        }
        str += " ";
        str += calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        str += " ";
        str += calendar.get(Calendar.YEAR);
        str += " (";
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        str += calendar.get(Calendar.HOUR_OF_DAY) + ":" + (minute.length() == 2 ? minute : "0" + minute) + " " + calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
        str += ") [EST]";
        return str;
    }

    public static Pair<Double, TimeUnit> convert(long millis, int decimalPlace) {

        TimeUnit timeUnit;

        if (millis < 60000) {
            timeUnit = TimeUnit.SECONDS;
        } else if (millis < 3600000) {
            timeUnit = TimeUnit.MINUTES;
        } else if (millis < 86400000) {
            timeUnit = TimeUnit.HOURS;
        } else {
            timeUnit = TimeUnit.DAYS;
        }

        if (timeUnit == TimeUnit.DAYS) {
            return new ImmutablePair<>(MathUtil.trim(millis / 86400000d, decimalPlace), timeUnit);
        }
        if (timeUnit == TimeUnit.HOURS) {
            return new ImmutablePair<>(MathUtil.trim(millis / 3600000d, decimalPlace), timeUnit);
        }
        if (timeUnit == TimeUnit.MINUTES) {
            return new ImmutablePair<>(MathUtil.trim(millis / 60000d, decimalPlace), timeUnit);
        }
        return new ImmutablePair<>(MathUtil.trim(millis / 1000d, decimalPlace), timeUnit);

    }
}
