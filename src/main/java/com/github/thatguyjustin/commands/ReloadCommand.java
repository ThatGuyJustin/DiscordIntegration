package com.github.thatguyjustin.commands;

import com.github.thatguyjustin.DiscordIntegration;
import com.github.thatguyjustin.config.Config;
import com.github.thatguyjustin.config.Messages;
import com.github.thatguyjustin.otherUtils.Logger;
import com.github.thatguyjustin.otherUtils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by GrimReaper52498 on 3/22/2016.
 *
 * @author Tyler Brady (GrimReaper52498)
 */
public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(sender instanceof ConsoleCommandSender)
        {
            Config.getInstance().reloadConfig(DiscordIntegration.getInstance().getDataFolder(), "messages");
            DiscordIntegration.getInstance().reloadConfig();
            Bukkit.getPluginManager().disablePlugin(DiscordIntegration.getInstance());
            Bukkit.getPluginManager().enablePlugin(DiscordIntegration.getInstance());
            Logger.info(StringUtils.color(Messages.PLUGIN_RELOADED.getMessage()), false);
            return false;
        }

        Player p = (Player) sender;

        if (!p.isOp()) {
            p.sendMessage(StringUtils.color("&cYou do not have permission to do this!"));
            return false;
        }

        Config.getInstance().reloadConfig(DiscordIntegration.getInstance().getDataFolder(), "messages");
        DiscordIntegration.getInstance().reloadConfig();

        Bukkit.getPluginManager().disablePlugin(DiscordIntegration.getInstance());
        Bukkit.getPluginManager().enablePlugin(DiscordIntegration.getInstance());
        p.sendMessage(StringUtils.color(Messages.PLUGIN_RELOADED.getMessage()));
        return false;
    }

}
