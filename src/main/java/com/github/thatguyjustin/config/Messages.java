package com.github.thatguyjustin.config;

import com.github.thatguyjustin.DiscordIntegration;
import com.github.thatguyjustin.DiscordUtil;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public enum Messages {
    EXAMPLE_MESSAGE("path.to.message", "DefaultMessage"),
    DISCORD_TO_CHAT("minecraft.chat.format", "&8[&9Discord&8]&r%MemberColor%%MemberDisplayRole% &7| %MemberColor%%MemberNick% &8» &f%Message%"),
    MINECRAFT_AUTH_TIMEOUT("minecraft.auth.timeout", "%PluginPrefix%&4&lSorry, you did not verify in time. Please try again!"),
    PLUGIN_PREFIX("plugin_prefix", "&7&l[&b&lDiscord&f&lIntegration&7&l] &r"),
    PLUGIN_RELOADED("plugin_reload", "%PluginPrefix% &aPlugin has been reloaded!");

    private String path;
    private String defaultMessage;
    private String fileMessage;
    private String message;
    private boolean isCustom;

    Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;

        FileConfiguration config = Config.getInstance().getConfig(DiscordIntegration.getInstance().getDataFolder(), "messages");
        if (config != null) {
            if (config.isSet(path)) {
                this.fileMessage = config.getString(path);
                if (fileMessage.equalsIgnoreCase(defaultMessage)) {
                    isCustom = true;
                }
            }
        }
        message = (isCustom ? fileMessage : defaultMessage);
    }

    /**
     * Get the config path to this message
     *
     * @return Path to message
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Get the default message
     *
     * @return The default message
     */
    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    /**
     * Get the message as written in the file
     *
     * @return The message from the file
     */
    public String getFileMessage() {
        return fileMessage;
    }

    /**
     * Retrieve the message
     *
     * @return The default message or message from config if modified
     */
    public String getMessage() {
        String s = message;

        if (s.contains("%PluginPrefix%"))
            s = s.replace("%PluginPrefix%", "[DiscordIntegration]");

        return s;
    }

    /**
     * Get a message with Discord variables replaced
     *
     * @param user    User to replace variables for, Null if non-applicable
     * @param ch      Channel to replace variables for, Null if non-applicable
     * @param member  Member to replace variables for, Null if non-applicable
     * @param guild   Guild to replace variables for, Null if non-applicable
     * @param message Message to replace variables for, Null if non-applicable
     * @return
     */
    public String replaceDiscordVariables(User user, TextChannel ch, Member member, Guild guild, Message message) {

        String s = getMessage();
        if (user != null) {
            //USER VARIABLES
            if (s.contains("%UserName%"))
                s = s.replace("%UserName%", user.getName());
            if (s.contains("%UserDiscrim%"))
                s = s.replace("%UserDiscrim%", user.getDiscriminator());
            if (s.contains("%UserTag%"))
                s = s.replace("%UserTag%", user.getAsTag());
            if (s.contains("%UserID%"))
                s = s.replace("%UserID%", user.getId());
        }
        if (ch != null) {
            //CHANNEL VARIABLES
            if (s.contains("%ChannelName%"))
                s = s.replace("%ChannelName%", ch.getName());
            if (s.contains("%ChannelID%"))
                s = s.replace("%ChannelID%", ch.getId());
        }
        if (member != null) {
            //Member variables
            if (s.contains("%MemberNick%"))
                s = s.replace("%MemberNick%", member.getNickname());
            if (s.contains("%MemberColor%"))
                s = s.replace("%MemberColor%", DiscordUtil.getColor(member));
            if (s.contains("%MemberHColor"))
                s = s.replace("%MemberHColor%", Integer.toHexString(member.getColor().getRGB() & 0xffffff));
            if (s.contains("%MemberDisplayRole%")) {
                String highest = "NONE";
                if (member.getRoles().isEmpty())
                    s = s.replace("%MemberDisplayRole%", "NONE");
                for (Role r : member.getRoles()) {
                    if (r.isHoisted()) {
                        highest = r.getName();
                        break;
                    }
                }
                s = s.replace("%MemberDisplayRole%", highest);
            }
            if (s.contains("%MemberHighestRole%")) {
                if (member.getRoles().isEmpty())
                    s = s.replace("%MemberHighestRole%", "NONE");
                else
                    s = s.replace("%MemberHighestRole%", member.getRoles().get(0).getName());

            }
        }
        if (guild != null) {
            //Guild variables
            if (s.contains("%GuildName%"))
                s = s.replace("%GuildName%", guild.getName());
            if (s.contains("%GuildID%"))
                s = s.replace("%GuildID%", guild.getId());
            if (s.contains("%GuildMemberCount%"))
                s = s.replace("%GuildMemberCount%", String.valueOf(guild.getMembers().size()));
        }
        if (message != null) {
            if (s.contains("%Message%"))
                s = s.replace("%Message%", message.getContentStripped());
        }
        return s;
    }

    /**
     * Whether or not the message has been modified in the config
     *
     * @return True if the message was modified, false otherwise
     */
    public boolean isCustomMessage() {
        return isCustom;
    }

    /**
     * Return the message with Minecraft variables replaced
     *
     * @param p Players to replace variables for, or null if not applicable.
     * @return Message with variables replaced.
     */
    public String replaceMinecraftVariables(Player p) {
        String s = getMessage();

        if (s.contains("%PlayerCount%"))
            s = s.replace("%PlayerCount%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));

        if (p != null) {
            if (s.contains("%UserRawPrefix%")) {
                String prefix_raw = DiscordIntegration.getInstance().getChat().getPlayerPrefix(p);
                String prefix = ChatColor.translateAlternateColorCodes('&', prefix_raw);
                prefix = ChatColor.stripColor(prefix);
                s = s.replace("%UserRawPrefix%", prefix);
            }
            if (s.contains("%UserRawSuffix%")) {
                String suffix_raw = DiscordIntegration.getInstance().getChat().getPlayerSuffix(p);
                String suffix = ChatColor.translateAlternateColorCodes('&', suffix_raw);
                suffix = ChatColor.stripColor(suffix);
                s = s.replace("%UserRawSuffix%", suffix);
            }
            if (s.contains("%UserPrefix%"))
                s = s.replace("%UserPrefix%", DiscordIntegration.getInstance().getChat().getPlayerPrefix(p));
            if (s.contains("%UserSuffix%"))
                s = s.replace("%UserSuffix%", DiscordIntegration.getInstance().getChat().getPlayerSuffix(p));
            if (s.contains("%UserName%"))
                s = s.replace("%UserName%", p.getName());
            if (s.contains("%UserNickName%")) {
                String name = p.getDisplayName();
                String prefix_raw = DiscordIntegration.getInstance().getChat().getPlayerPrefix(p);
                String prefix = ChatColor.translateAlternateColorCodes('&', prefix_raw);
                prefix = ChatColor.stripColor(prefix);
                String suffix_raw = DiscordIntegration.getInstance().getChat().getPlayerSuffix(p);
                String suffix = ChatColor.translateAlternateColorCodes('&', suffix_raw);
                suffix = ChatColor.stripColor(suffix);

                name = ChatColor.stripColor(name);

                name = name.replace(prefix, "");
                name = name.replace(suffix, "");
                name = name.trim();
                s = s.replace("%UserNickName%", name);
            }
        }
        return s;
    }
    /*
    List of variables:
        Discord:
            User:
                %UserName% - Username from event || Justin
                %UserDiscrim% - Discriminator from event || 1337
                %UserTag% - Tag of user || Justin#1337
                %UserID% - ID of user || 104376018222972928
            Channel (TEXT):
                %ChannelName% - Name of channel || general
                %ChannelID% - ID of channel || 459217920111804446
            Member:
                %MemberNick% - Member's nickname
                %MemberColor% - Member's display color || &7
                %MemberHColor% - Member's display color as Hex || #007766
                %MemberDisplayRole% - Member's Hoisted role.
                %MemberHighestRole% - Member's Highest role name.
            Guild:
                %GuildName% - Name of guild
                %GuildID% - ID of guild
                %GuildMemberCount% - Amount of users in the guild || 10,000 (Formatted, not raw)
     */

    /*
        Minecraft:
            %PluginPrefix% - Default prefix the plugin will use || [DiscordIntegration]
            Server:
                %PlayerCount%
            User:
                %UserPrefix%
                %UserRawPrefix%
                %UserSuffix%
                %UserRawSuffix%
                %UserName%
     */
}
