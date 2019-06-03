package com.github.ThatGuyJustin;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;


public class DiscordIntegration extends JavaPlugin {

    private JDA jda = null;

    @Override
    public void onEnable()
    {
        getLogger().info("DiscordIntegration Loaded!");
        this.saveDefaultConfig();

        try {
            this.createBot();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createBot() throws LoginException, InterruptedException {
        String token = this.getConfig().getString("discord.bot_token");
        if(token.equals("insert_token_here") || token == null)
        {
            this.getLogger().info("ERROR: Discord Token missing from config, can't create bot connection. Disabling Plugin.");
            this.setEnabled(false);
        }
        if(token.isEmpty())
        {
            this.getLogger().info("ERROR: Discord Token missing from config, can't create bot connection. Disabling Plugin.");
            this.setEnabled(false);
        }

        this.jda = new JDABuilder(AccountType.BOT)
                .setAudioEnabled(false)
                .setAutoReconnect(true)
                .setBulkDeleteSplittingEnabled(false)
                .setToken(token)
                .setContextEnabled(false)
                .build().awaitReady();

        System.out.println("Discord Connected!");

        if(this.getConfig().getBoolean("features.logging.bot_connected") && this.getConfig().getBoolean("features.logging.enabled"))
        {
            long logging_channel = this.getConfig().getLong("discord.log_channel_id");

            String[] embed_desc = {
                String.format("**Bot connect as**: %s", this.jda.getSelfUser().getAsTag()),
                String.format("**Total Servers**: %s", this.jda.getGuilds().size())
            };

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Bot Connected")
                    .setDescription(String.join("\n", embed_desc));

            try{
                TextChannel log_channel = jda.getTextChannelById(logging_channel);

                log_channel.sendMessage(embed.build()).queue();

            }catch (Error e){
                this.getLogger().info("Error: Unable to log to configured channel.");
                System.err.print(e);
            }
        }
    }

    @Override
    public void onDisable(){
        if(this.jda != null) {
            this.jda.shutdown();
            this.jda = null;
        }
        if(this.jda == null)
            getLogger().info("DiscordIntegration Disabled!");
    }

    public String getConfigOption(String path){
        return this.getConfig().getString(path);
    }

    public boolean saveConfigNew(){
        try {
            this.saveConfig();
            return true;
        }catch(Error e){
            System.err.println(e);
            return false;
        }
    }
}
