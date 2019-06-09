package com.github.thatguyjustin.listeners;

import com.github.thatguyjustin.DiscordIntegration;
import com.github.thatguyjustin.DiscordUtil;
import com.github.thatguyjustin.otherUtils.StringUtils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class DiscordListener extends ListenerAdapter {

    private DiscordIntegration pl;

    public DiscordListener(DiscordIntegration pl) {
        this.pl = pl;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.isFromType(ChannelType.PRIVATE) && pl.getVerificationCache().hasVerificationMessage(event.getUser().getId()) && event.getReactionEmote().getName().equals("☑")) {
            Message msg = pl.getVerificationCache().getVerificationMessage(event.getUser().getId());

            msg.editMessage("**Verification Confirmed**!\nSorry for the hassle!").queue();

            pl.getVerificationCache().getTimer(event.getUser().getId()).cancel();

        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {


        if (!event.isFromGuild() || !event.getAuthor().getId().equals("104376018222972928"))
            return;

        if (!pl.getConfig().getBoolean("features.discord_to_chat.enabled"))
            return;

        String chatColor = DiscordUtil.getColor(event.getMember());

        if (!pl.getConfig().getBoolean("features.discord_to_chat.use_hoist_name")) {
            String name = event.getAuthor().getName();
            if (pl.getConfig().getBoolean("features.discord_to_chat.use_nickname"))
                name = event.getMember().getNickname();
            String message = StringUtils.color(String.format("&8[&9Discord&8]&r&%3$s %1$s &8»&7 %2$s", name, event.getMessage().getContentStripped(), chatColor));
            Bukkit.broadcastMessage(message);
        }
    }
}
