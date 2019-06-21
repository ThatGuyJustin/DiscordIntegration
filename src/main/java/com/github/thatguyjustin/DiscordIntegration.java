package com.github.thatguyjustin;

import com.github.thatguyjustin.cache.VerificationCache;
import com.github.thatguyjustin.commands.ReloadCommand;
import com.github.thatguyjustin.config.Config;
import com.github.thatguyjustin.config.Messages;
import com.github.thatguyjustin.listeners.PluginListener;
import com.github.thatguyjustin.otherUtils.Logger;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordIntegration extends JavaPlugin {

    private DiscordUtil discord = new DiscordUtil(this);
    private static DiscordIntegration instance = null;
    private static Chat chat = null;

    private VerificationCache verificationCache = null;

    @Override
    public void onEnable() {
        instance = this;
        if(Bukkit.getPluginManager().getPlugin("Vault") == null)
        {
            Logger.error("&cVault not found. Disabling plugin.", true);
            getServer().getPluginManager().disablePlugin(this);
        }
        if (!setupChat()) {
            Logger.error("&cCould not hook into chat, please make sure there is a permissions plugin installed.", true);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        verificationCache = new VerificationCache();
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
        getCommand("direload").setExecutor(new ReloadCommand());
        this.saveDefaultConfig();
        String token = this.getToken();
        if (token == null) {
            Logger.error("Unable to correctly get the bot's token. Please check that the token is correct, and reload the plugin.", true);
            getServer().getPluginManager().disablePlugin(this);
        }
        try {
            discord.createBot(token);
        } catch (Error e) {
            getServer().getPluginManager().disablePlugin(this);
        }

        if (getConfig().getBoolean("firstRun")) {

            FileConfiguration messageConfig = Config.getInstance().getConfig(getDataFolder(), "messages");
            for (Messages msg : Messages.values()) {
                messageConfig.set(msg.getPath(), msg.getDefaultMessage());
            }
            Config.getInstance().saveConfig(getDataFolder(), "messages");
            getConfig().set("firstRun", false);
        }


        Logger.info("&aPlugin Enabled!", true);
    }

    private String getToken() {
        String temp = this.getConfig().getString("discord.bot_token");
        if (temp == null)
            return null;
        if (temp.isEmpty())
            return null;
        return temp;
    }

    @Override
    public void onDisable() {

        saveDefaultConfig();

        if (this.discord.getBot() != null) {
            this.discord.getBot().shutdown();
            this.discord.setNull();
        }
        if (this.discord.getBot() == null)
            Logger.info("&6Bot: &cBot Shutdown!", true);

        Logger.info("&cPlugin Disabled!", true);
    }

    public DiscordUtil getDiscord() {
        return this.discord;
    }

    public VerificationCache getVerificationCache() {
        return verificationCache;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp == null)
            return false;
        chat = rsp.getProvider();
        return chat != null;
    }

    public Chat getChat() {
        return chat;
    }

    public static DiscordIntegration getInstance() {
        return instance;
    }
}
