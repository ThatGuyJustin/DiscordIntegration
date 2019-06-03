package com.github.thatguyjustin.otherUtils;

import org.bukkit.Bukkit;
public class Logger {
    private static String prefix = StringUtils.color("&7&l[&b&lDiscord&f&lIntegration&7&l] &r");

    private static void log(String message, Type type, boolean prefix) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.color((prefix ? Logger.prefix : "") + (type.getPrefix()) + message));
    }

    private static void log(String prefix, String message, Type type) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.color((prefix == null ? "" : prefix) + (type.getPrefix() + message)));
    }


    private enum Type {
        NORMAL(""),
        INFO("&7&l[&b&lINFO&7&l] &r"),
        WARNING("&7&l[&c&lWARNING&7&l] &r"),
        SEVERE("&7&l[&4&lSEVERE&7&l] &r"),
        ERROR("&7&l[&4&lERROR&7&l] &r"),
        DEBUG("&7&l[&4&lDEBUG&7&l] &r");

        private String prefix;

        Type(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static void debug(String message) {
        log(message, Type.DEBUG, false);
    }

    public static void log(String message, boolean pluginPrefix) {
        log(message, Type.NORMAL, pluginPrefix);
    }

    public static void info(String message, boolean pluginPrefix) {
        log(message, Type.INFO, pluginPrefix);
    }

    public static void warning(String message, boolean pluginPrefix) {
        log(message, Type.WARNING, pluginPrefix);
    }

    public static void severe(String message, boolean pluginPrefix) {
        log(message, Type.SEVERE, pluginPrefix);
    }

    public static void error(String message, boolean pluginPrefix) {
        log(message, Type.ERROR, pluginPrefix);
    }

}