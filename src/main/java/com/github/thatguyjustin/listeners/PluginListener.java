package com.github.thatguyjustin.listeners;

import com.github.thatguyjustin.DiscordIntegration;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
}
