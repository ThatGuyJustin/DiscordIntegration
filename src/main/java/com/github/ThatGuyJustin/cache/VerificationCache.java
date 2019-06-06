package com.github.thatguyjustin.cache;

import com.github.thatguyjustin.DiscordIntegration;
import com.github.thatguyjustin.otherUtils.Logger;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class VerificationCache {
    private HashMap<String, HashMap<Message, Timer>> verificationMessages = Maps.newHashMap();

    public void addVerificationMessage(String user, Message message, User dUser, DiscordIntegration pl, Player player) {
        HashMap<Message, Timer> map = Maps.newHashMap();
        Timer timer = new Timer();
        map.put(message, timer);
        verificationMessages.put(user, map);

        timer.schedule(new VerificationTimer(pl, dUser, message, player), 10000L, 10000L);
    }

    public Timer getTimer(String user) {
        return verificationMessages.get(user).get(verificationMessages.get(user).keySet().toArray()[0]);
    }

    public void removeVerificationMessage(String user) {
        verificationMessages.remove(user);
    }

    public Message getVerificationMessage(String user) {
        if (verificationMessages.containsKey(user))
            return (Message) verificationMessages.get(user).keySet().toArray()[0];

        return null;
    }

    public boolean hasVerificationMessage(String user) {
        return verificationMessages.containsKey(user);
    }

    private class VerificationTimer extends TimerTask {
        private Player player;
        private User user;
        private Message message;
        private DiscordIntegration pl;

        public VerificationTimer(DiscordIntegration pl, User user, Message msg, Player player) {
            this.player = player;
            this.message = msg;
            this.user = user;
            this.pl = pl;
        }

        @Override
        public void run() {
            Logger.debug("-2");
            if (pl.getVerificationCache().hasVerificationMessage(user.getId())) {
                Logger.debug("-1");
                Message msg = pl.getVerificationCache().getVerificationMessage(user.getId());
                Message updated = msg.getChannel().retrieveMessageById(msg.getId()).complete();
                updated.getReactions().forEach(r ->
//                msg.getReactions().forEach(r ->

                {
                    Logger.debug("0");

                    if (r.getReactionEmote().getName().equalsIgnoreCase("☑")) {
                        Logger.debug("1");
                        r.retrieveUsers().forEach(u ->
                        {
                            Logger.debug("2");

                            if (u.getId().equals(user.getId())) {
                                Logger.debug("3");

                                pl.getVerificationCache().removeVerificationMessage(user.getId());
                                getTimer(user.getId()).cancel();
                            } else {
                                Logger.debug("4");

                                pl.kickPlayer(player);
                                getTimer(user.getId()).cancel();

                            }
                        });
                    }
                });
            }
            getTimer(user.getId()).cancel();
//            player.kickPlayer(StringUtils.color("&4&lSorry, you did not verify in time. Please try again!"));
//            getTimer(user.getId()).cancel();
        }
    }
}
