package com.github.thatguyjustin.listeners;

import com.github.thatguyjustin.DiscordIntegration;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // Return if:
        // * Prefix is null
        // * Message doesn't start with prefix
        // * If it's the chat log channel (Commands auto disabled in that channel)
        if(pl.getConfig().getString("discord.prefix") == null || !event.getMessage().getContentRaw().startsWith(pl.getConfig().getString("discord.prefix")) || event.getChannel().getId().equals(pl.getConfig().getString("discord.chat_channel_id")))
            return;
    }

}
