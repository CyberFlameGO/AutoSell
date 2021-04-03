package net.cloud.autosell.utils;

import lombok.Getter;
import net.cloud.autosell.AutoSell;
import net.cloud.autosell.support.XSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

public class Utils {

    private AutoSell plugin;
    public Utils(AutoSell autoSell) {
        plugin = autoSell;
        decimalFormat = new DecimalFormat("###,###.##");
    }

    public String getTimeFormat(Long milliseconds) {
        long days = milliseconds / 1000 / 60 / 60 / 24;
        long hours = milliseconds / 1000 / 60 / 60 - days * 24;
        long minutes = milliseconds / 1000 / 60 - days * 24 * 60 - hours * 60;
        long seconds = milliseconds / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60;

        StringBuilder sb = new StringBuilder();
        if(days >= 1) sb.append(days + " day" + (days > 1 ? "s" : ""));

        if(hours >= 1) {
            if(!(sb.toString().isEmpty())) sb.append(" ");
            sb.append(hours + " hour" + (hours > 1 ? "s" : ""));
        }

        if(minutes >= 1) {
            if(!(sb.toString().isEmpty())) sb.append(" ");
            sb.append(minutes + " minute" + (minutes > 1 ? "s" : ""));
        }

        if(seconds >= 1) {
            if(!(sb.toString().isEmpty())) sb.append(" ");
            sb.append(seconds + " second" + (seconds > 1 ? "s" : ""));
        }

        if(sb.toString().isEmpty()) return "None";
        return sb.toString();
    }

    public String getColor(String args) {
        return ChatColor.translateAlternateColorCodes('&', args);
    }

    public boolean playSound(Player player, String sound) {
        try {
            XSound.playSoundFromString(player, sound);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean playSound(Player player, XSound sound) {
        try {
            XSound.playSoundFromString(player, sound.name());
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void sendMessage(CommandSender sender, FileConfiguration config, String path, PlaceholderReplacer placeholders) {
        if(config.getBoolean(path + ".Sound.Enabled", false) && (sender instanceof Player)) {
            playSound((Player) sender, config.getString(path + ".Sound.Value", "ENTITY_BAT_TAKEOFF"));
        }

        if(config.getBoolean(path + ".Message.Enabled", false)) {
            List<String> messages = config.getStringList(path + ".Message.Value");
            for(String message : messages) {
                if(placeholders == null) {
                    sender.sendMessage(getColor(message));
                } else {
                    sender.sendMessage(getColor(placeholders.parse(message)));
                }
            }
        }
    }
    public void broadcastMessage(FileConfiguration config, String path, PlaceholderReplacer placeholders) {
        if(config.getBoolean(path + ".Sound.Enabled", false)) {
            Bukkit.getOnlinePlayers().forEach(player -> playSound(player, config.getString(path + ".Sound.Value", "ENTITY_BAT_TAKEOFF")));
        }

        if(config.getBoolean(path + ".Message.Enabled", false)) {
            List<String> messages = config.getStringList(path + ".Message.Value");
            for(String message : messages) {
                if(placeholders == null) {
                    Bukkit.broadcastMessage(getColor(message));
                } else {
                    Bukkit.broadcastMessage(getColor(placeholders.parse(message)));
                }
            }
        }
    }

    public XSound getSound(FileConfiguration config, String path, XSound emptyValue) {
        try {
            XSound xSound = XSound.getSoundFromString(config.getString(path, ""));
            return xSound;
        } catch(Exception e) {  }
        return emptyValue;
    }

    @Getter private DecimalFormat decimalFormat;


}
