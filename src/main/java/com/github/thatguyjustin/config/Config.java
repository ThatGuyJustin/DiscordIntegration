package com.github.thatguyjustin.config;

import com.github.thatguyjustin.DiscordIntegration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by GrimReaper52498 on 4/23/2016.
 *
 * @author GrimReaper52498 (Tyler Brady)
 */
public class Config {
    private static Config instance = null;
    private JavaPlugin plugin = null;
    private HashMap<JavaPlugin, HashMap<String, HashMap<String, FileConfiguration>>> configs =
            new HashMap<>();

    private Config() {
        this.plugin = DiscordIntegration.getInstance();
    }

    /**
     * Get the Config instance
     *
     * @return this
     */
    public static Config getInstance() {
        if (instance == null) {
            return (instance = new Config());
        }

        return instance;
    }

    /**
     * Reload the specified config, from the given Plugin
     *
     * @param directory Directory where the file is stored
     * @param id        Name of the config file
     * @return FileConfiguration of the file
     */
    public FileConfiguration reloadConfig(File directory, String id) {
        if (!configs.containsKey(plugin)) {
            HashMap<String, HashMap<String, FileConfiguration>> map = new HashMap<>();
            configs.put(plugin, map);
        }
        File customConfigFile;

        if (directory.equals(plugin.getDataFolder())) {
            customConfigFile = new File(plugin.getDataFolder(), id + ".yml");
        } else {
            customConfigFile = new File(plugin.getDataFolder(), File.separator + "/" + directory.getName() + "/" + File.separator + id + " .yml");
        }

        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        InputStream defConfigStream = plugin.getResource(id + ".yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(customConfigFile);
            customConfig.setDefaults(defConfig);
        }

        if (directory.equals(plugin.getDataFolder())) {
            if (configs.get(plugin).containsKey(plugin.getDataFolder().getName())) {
                configs.get(plugin).get(plugin.getDataFolder().getName()).put(id, customConfig);
            } else {
                HashMap<String, FileConfiguration> map = new HashMap<>();
                map.put(id, customConfig);
                configs.get(plugin).put(plugin.getDataFolder().getName(), map);

            }
        } else {
            if (configs.get(plugin).containsKey(directory.getName())) {
                configs.get(plugin).get(directory.getName()).put(id, customConfig);
            } else {
                HashMap<String, FileConfiguration> map = new HashMap<>();
                map.put(id, customConfig);
                configs.get(plugin).put(directory.getName(), map);

            }
        }
        return customConfig;
    }

    public HashMap<String, FileConfiguration> getConfigsFromDirectory(File dir) {
        if (configs.containsKey(plugin) && configs.get(plugin).containsKey(dir.getName())) {
            return configs.get(plugin).get(dir);
        }
        return null;
    }

    /**
     * Retrieve the specified config
     *
     * @param directory Directory where the file is stored
     * @param id        Name of the config file
     * @return FileConfiguration of the file
     */
    public FileConfiguration getConfig( File directory, String id) {
        if (configs.containsKey(plugin) && configs.get(plugin).containsKey(directory.getName()) && configs.get(plugin).get(directory.getName()).containsKey(id)) {
            return configs.get(plugin).get(directory.getName()).get(id);
        }
        return reloadConfig(directory, id);
    }

    /**
     * Save the specified config
     *
     * @param directory Directory where the file is stored
     * @param id        Name of the config file
     */
    public void saveConfig(File directory, String id) {
        try {
            File customConfigFile;
            if (directory.equals(plugin.getDataFolder())) {
                customConfigFile = new File(plugin.getDataFolder(), id + ".yml");
            } else {
                customConfigFile = new File(plugin.getDataFolder(), File.separator + "/" + directory.getName() + "/" + File.separator + id + " .yml");
            }
            getConfig(directory, id).save(customConfigFile);
        } catch (Exception ex) {
        }
    }

    /**
     * Check whether or not the file exists
     *
     * @param directory Directory where the file is stored
     * @param id        Name of the config file
     */
    public boolean exists(File directory, String id) {
        File customConfigFile;
        if (directory.equals(plugin.getDataFolder())) {
            customConfigFile = new File(plugin.getDataFolder(), id + ".yml");
        } else {
            customConfigFile = new File(plugin.getDataFolder(), File.separator + directory.getName() + File.separator + id + " .yml");
        }
        return customConfigFile.exists();
    }

    public void cacheAll(String directory, String... extensions) {
        final File file = new File(plugin.getDataFolder(), File.separator + directory);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (final File child : FileUtils.listFiles(file, extensions, false)) {
            reloadConfig(file, FilenameUtils.getBaseName(child.getName()));
//            Utils.Log("Cached " + child.getName());
        }
    }
}
