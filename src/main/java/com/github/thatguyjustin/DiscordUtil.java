package com.github.thatguyjustin;

import com.github.thatguyjustin.listeners.DiscordListener;
import com.github.thatguyjustin.otherUtils.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DiscordUtil {

    private JDA bot;
    private DiscordIntegration pl;

    DiscordUtil(DiscordIntegration pl) {
        this.pl = pl;
    }

    void createBot(String token) {
        if (token == null) {
            return;
        }
        try {
            this.bot = new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(false)
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .addEventListeners(new DiscordListener(this.pl))
                    .setToken(token)
                    .setContextEnabled(false)
                    .build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
        Logger.info(String.format("&7[&e&lDiscord&7] &aBot connect to Discord as&b %s &r", this.bot.getSelfUser().getAsTag()), false);
        if (pl.getConfig().getBoolean("features.logging.enabled") && pl.getConfig().getBoolean("features.logging.bot_connected"))
            this.logConnect();
    }

    private void logConnect() {
        TextChannel log_channel = this.bot.getTextChannelById(pl.getConfig().getLong("discord.log_channel_id"));

        if (log_channel == null)
            return;

        String[] embed_desc = {
                "__**Bot Information**__",
                String.format("**Bot connect as**: %s", this.bot.getSelfUser().getAsTag()),
                String.format("**Total Servers**: %s", this.bot.getGuilds().size()),
                "",
                "__**Server Information**__",
                String.format("**Server IP**: %s", this.pl.getConfig().getString("server.ip")),
                String.format("**Server Port**: %s", this.pl.getConfig().getString("server.port"))
        };

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Startup Information")
                .setDescription(String.join("\n", embed_desc))
                .setFooter("Created By The Aperture Team", "https://cdn.discordapp.com/avatars/330770985450078208/075d02a034e1da3f827c3f16a6324c6a.webp?size=1024")
                .setTimestamp(Instant.now())
                .setThumbnail(String.format("https://assists.aperturebot.science/minecraft/GetServerIcon.php?ip=%1$s&port=%2$s", pl.getConfig().getString("server.ip"), pl.getConfig().getString("server.port")));

        log_channel.sendMessage(embed.build()).queue();

    }

    public static String getColor(Member member)
    {
        String temp = "f";
        String discordColor;
        if((Integer) member.getColorRaw() != null)
            discordColor = Integer.toHexString(member.getColor().getRGB() & 0xffffff);

        else
            discordColor = "ffffff";

        try {
            JSONObject hex = new JSONObject();
            hex.append("hex", discordColor);
            HttpResponse<JsonNode> response = Unirest.post("https://assists.aperturebot.science/minecraft/colorconvert.php")
                    .header("accept", "application/json")
                    .body(hex)
                    .asJson();
            temp= response.getBody().getObject().get("chat_color").toString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return "&" + temp;
    }

    public Message sendPrivateMessage(User user, Message msg) {
        if (!user.hasPrivateChannel()) user.openPrivateChannel().complete();

        ((UserImpl) user).getPrivateChannel().sendMessage(msg).queue();
        return msg;
    }

    public Message sendPrivateMessage(User user, String msg, boolean isVerification, Player player) {
        if (!user.hasPrivateChannel()) user.openPrivateChannel().complete();
        AtomicReference<Message> toReturn = new AtomicReference<Message>(null);
        ((UserImpl) user).getPrivateChannel().sendMessage(msg).queue(e ->
        {
            toReturn.set(e);

            if (isVerification) {
                pl.getVerificationCache().addVerificationMessage(user.getId(), e, user, this.pl, player);

                pl.getVerificationCache().getVerificationMessage(user.getId()).addReaction("☑").queue();
                new BukkitRunnable() {
                    @Override
                    public void run() {

                    }
                }.runTaskLater(pl, 20L * TimeUnit.SECONDS.toSeconds(10));

            }

        });
        return toReturn.get();
    }

    public JDA getBot() {
        return bot;
    }

    void setNull() {
        this.bot = null;
    }
}
