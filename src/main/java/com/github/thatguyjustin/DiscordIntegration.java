package com.github.thatguyjustin;

import com.github.thatguyjustin.listeners.PluginListener;
import com.github.thatguyjustin.otherUtils.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordIntegration extends JavaPlugin {

   private DiscordUtil discord = new DiscordUtil(this);

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
        this.saveDefaultConfig();
        String token = this.getToken();
        if(token == null)
        {
            Logger.error("Unable to correctly get the bot's token. Please check that the token is correct, and reload the plugin.", true);
            this.setEnabled(false);
        }
        try {
            discord.createBot(token);
        }catch(Error e){
//            System.err.println(e);
            this.setEnabled(false);
        }
        Logger.info("&aPlugin Enabled!", true);
    }

    private String getToken()
    {
        String temp = this.getConfig().getString("discord.bot_token");
        if(temp == null)
            return null;
        if(temp.isEmpty())
            return null;
        return temp;
    }

    @Override
    public void onDisable(){
        if(this.discord.getBot() != null)
        {
            this.discord.getBot().shutdown();
            this.discord.setNull();
        }
        if(this.discord.getBot() == null)
            Logger.info("&6Bot: &cBot Shutdown!", true);

        Logger.info("&cPlugin Disabled!", true);
    }

    public DiscordUtil getDiscord(){
        return this.discord;
    }
}
