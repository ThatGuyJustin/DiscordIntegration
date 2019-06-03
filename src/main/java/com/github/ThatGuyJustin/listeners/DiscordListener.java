package com.github.thatguyjustin.listeners;

import com.github.thatguyjustin.DiscordIntegration;
import com.github.thatguyjustin.otherUtils.Logger;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListener extends ListenerAdapter {

    private DiscordIntegration pl;

    public DiscordListener(DiscordIntegration pl)
    {
        this.pl = pl;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        Logger.debug("Reaction is: " + event.getReaction().getReactionEmote());
    }

}
