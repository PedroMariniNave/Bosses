package com.zpedroo.bosses.utils.formatter;

import com.zpedroo.bosses.utils.FileUtils;
import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

import static com.zpedroo.bosses.utils.formatter.TimeFormatter.Translations.*;

public class TimeFormatter {

    public static String format(long time) {
        StringBuilder builder = new StringBuilder();

        long hours = TimeUnit.MILLISECONDS.toHours(time) - (TimeUnit.MILLISECONDS.toDays(time) * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);

        if (hours > 0) builder.append(hours).append(" ").append(hours == 1 ? HOUR : HOURS).append(" ");
        if (minutes > 0) builder.append(minutes).append(" ").append(minutes == 1 ? MINUTE : MINUTES).append(" ");
        if (seconds > 0) builder.append(seconds).append(" ").append(seconds == 1 ? SECOND : SECONDS);

        return builder.toString().isEmpty() ? NOW : builder.toString();
    }


    static class Translations {

        public static final String SECOND = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.second"));

        public static final String SECONDS = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.seconds"));

        public static final String MINUTE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.minute"));

        public static final String MINUTES = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.minutes"));

        public static final String HOUR = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.hour"));

        public static final String HOURS = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.hours"));

        public static final String NOW = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Time-Formatter.now"));

        private static String getColored(String str) {
            return ChatColor.translateAlternateColorCodes('&', str);
        }
    }
}