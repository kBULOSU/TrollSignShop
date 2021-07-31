package com.trollpixel.signshop.misc.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class MessageUtils {

    public static String translateFormat(String format, Object... args) {
        return MessageUtils.translateColorCodes(String.format(
                format, args
        ));
    }

    public static String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String[] translateColorCodes(String... messages) {
        List<String> list = new ArrayList<>();
        for (String message : messages) {
            String translateColorCodes = translateColorCodes(message);
            list.add(translateColorCodes);
        }

        return list.toArray(new String[0]);
    }

    public static String stripColor(String message, ChatColor... colors) {

        if (colors.length < 1) {
            colors = ChatColor.values();
        }

        StringBuilder codes = new StringBuilder();

        for (ChatColor color : colors) {
            codes.append(color.toString().toCharArray()[1]);
        }

        return Pattern.compile("(?i)" + COLOR_CHAR + "[" + codes.toString().toUpperCase() + "]")
                .matcher(message)
                .replaceAll("");
    }
}
