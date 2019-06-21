package com.github.thatguyjustin.listeners;

import com.github.thatguyjustin.DiscordIntegration;
import com.github.thatguyjustin.otherUtils.Logger;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Set;

public class PluginListener implements Listener {

    private final HashMap<String, String> userMap;
    private DiscordIntegration pl;

    public PluginListener(DiscordIntegration pl) {
        userMap = Maps.newHashMap();
        this.pl = pl;
        populateMap();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // McIsTheLimit ---> 104376018222972928 (Discord UserID)
        // DiscordBot --> DM User ^
        // Wait for a green check mark
        // If no check mark after X amount of time
        // Kick them.
        String username = event.getPlayer().getName();
        if (userMap.containsKey(username)) {
            event.getPlayer();
            User dm_user = pl.getDiscord().getBot().getUserById(userMap.get(username));
            pl.getDiscord().sendPrivateMessage(dm_user, "Hey, just want to confirm that you logged into the server! Please click the reaction below within **10 seconds** to confirm!", true, event.getPlayer());
        }
    }

    private void populateMap() {
        ConfigurationSection userSection = pl.getConfig().getConfigurationSection("users");

        if (userSection == null)
            return;

        Set<String> users = userSection.getKeys(false);

        users.forEach(u -> userMap.put(u, userSection.getString(u)));

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        if(pl.getConfig().getString("discord.chat_channel_id").isEmpty() || !pl.getConfig().getBoolean("features.chat_to_discord"))
            return;

        String message = event.getMessage();
        String chat_channel = pl.getConfig().getString("discord.chat_channel_id");
        TextChannel discord_channel = pl.getDiscord().getBot().getTextChannelById(chat_channel);

        if(!pl.getConfig().getBoolean("features.chat_to_discord.show_prefix") && !pl.getConfig().getBoolean("features.chat_to_discord.show_suffix"))
        {
            String to_Send = String.format("%1$s » %2$s", event.getPlayer().getName(), event.getMessage());
            discord_channel.sendMessage(to_Send).queue();
        }else{
            String prefix = ChatColor.translateAlternateColorCodes('&', pl.getChat().getPlayerPrefix(event.getPlayer()));
            prefix = ChatColor.stripColor(prefix);
            String suffix = ChatColor.translateAlternateColorCodes('&', pl.getChat().getPlayerSuffix(event.getPlayer()));
            suffix = ChatColor.stripColor(suffix);
            String colorLess_displayName = ChatColor.stripColor(event.getPlayer().getDisplayName());
            Logger.debug(colorLess_displayName);
            ChatColor.translateAlternateColorCodes('&', prefix);
            String to_send = String.format("%1$s%2$s %3$s» %4$s", prefix != null && pl.getConfig().getBoolean("features.chat_to_discord.show_prefix") ? prefix: "", !pl.getConfig().getBoolean("features.chat_to_discord.use_nickname") ? event.getPlayer().getName(): event.getPlayer().getPlayerListName(), suffix != null && pl.getConfig().getBoolean("features.chat_to_discord.show_suffix") ? suffix: "", event.getMessage());
            discord_channel.sendMessage(to_send).queue();
        }
    }
}
