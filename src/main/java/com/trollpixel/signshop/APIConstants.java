package com.trollpixel.signshop;

import com.trollpixel.signshop.location.unserializer.BukkitLocationParser;

import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class APIConstants {

    public static final BukkitLocationParser LOCATION_PARSER = new BukkitLocationParser();

    public static class Patterns {

        public static final Pattern COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-F]");
        public static final Pattern START_COLOR_PATTERN = Pattern.compile("^(?i)" + COLOR_CHAR + "[0-9A-F].*$");
    }

}
