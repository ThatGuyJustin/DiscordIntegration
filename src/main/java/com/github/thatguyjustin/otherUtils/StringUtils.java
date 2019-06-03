package com.github.thatguyjustin.otherUtils;

import org.bukkit.ChatColor;
public class StringUtils {

    /**
     * Color a message using & color codes
     *
     * @param message message
     * @return colored message
     */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
