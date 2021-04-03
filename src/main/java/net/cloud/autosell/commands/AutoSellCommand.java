package net.cloud.autosell.commands;

import lombok.Getter;
import net.cloud.autosell.AutoSell;
import net.cloud.autosell.builders.CloudCommandBuilder;
import net.cloud.autosell.builders.SubCommand;
import net.cloud.autosell.commands.subcommands.ReloadCommand;
import net.cloud.autosell.commands.subcommands.StatusCommand;
import net.cloud.autosell.commands.subcommands.SummaryCommand;
import net.cloud.autosell.commands.subcommands.ToggleCommand;
import net.cloud.autosell.enums.CommandAccessType;
import net.cloud.autosell.files.enums.CloudFileType;
import net.cloud.autosell.utils.PlaceholderReplacer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoSellCommand extends CloudCommandBuilder {

    @Getter private AutoSell plugin;
    private List<SubCommand> subCommands;

    @Getter private FileConfiguration messagesConfig;

    private String memberPermission;
    private String adminPermission;

    public AutoSellCommand(AutoSell autoSell) {
        super(autoSell.getFileUtils().getFileByType(CloudFileType.SETTINGS).getString("Command-Settings.Name", "autosell"));
        setCommandDescription("The main auto selling command")
            .setCommandAliases(autoSell.getFileUtils().getFileByType(CloudFileType.SETTINGS).getStringList("Command-Settings.Aliases"));
        plugin = autoSell;
        subCommands = Arrays.asList(new SummaryCommand(this),
                new ToggleCommand(this),
                new StatusCommand(this),
                new ReloadCommand(this));

        messagesConfig = plugin.getFileUtils().getFileByType(CloudFileType.MESSAGES);

        memberPermission = plugin.getFileUtils().getFileByType(CloudFileType.SETTINGS).getString("Permission-Settings.Member-Command", "autosell.member");
        adminPermission = plugin.getFileUtils().getFileByType(CloudFileType.SETTINGS).getString("Permission-Settings.Admin-Command", "autosell.admin");
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if(args.length == 0) {
            PlaceholderReplacer replacer = new PlaceholderReplacer().addPlaceholder("%version%", plugin.getDescription().getVersion());
            if(sender.hasPermission(adminPermission)) {
                plugin.getUtils().sendMessage(sender, messagesConfig, "ADMIN-HELP-MESSAGE", replacer);
                return false;
            }

            if(sender.hasPermission(memberPermission)) {
                plugin.getUtils().sendMessage(sender, messagesConfig, "MEMBER-HELP-MESSAGE", replacer);
                return false;
            }

            plugin.getUtils().sendMessage(sender, messagesConfig, "NO-PERMISSION", null);
            return false;
        }

        String firstArgument = args[0];
        for(SubCommand subCommand : subCommands) {
            if(subCommand.getArgs().equalsIgnoreCase(firstArgument)) {
                if((subCommand.getAccess().equals(CommandAccessType.MEMBER) && sender.hasPermission(memberPermission)) || (subCommand.getAccess().equals(CommandAccessType.ADMIN) && sender.hasPermission(adminPermission))) {
                    subCommand.execute(sender, args);
                } else {
                    plugin.getUtils().sendMessage(sender, messagesConfig, "NO-PERMISSION", null);
                }
                return false;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        List<String> tabCompletion = new ArrayList<>();
        if(args.length == 0 || args.length == 1) {
            for(SubCommand subCommand : subCommands) {
                if(subCommand.getAccess().equals(CommandAccessType.ADMIN)) {
                    if(sender.hasPermission(adminPermission)) tabCompletion.add(subCommand.getArgs());
                } else {
                    if(subCommand.getAccess().equals(CommandAccessType.MEMBER)) {
                        if(sender.hasPermission(memberPermission)) tabCompletion.add(subCommand.getArgs());
                    }
                }
            }
        } else {
            String firstArgument = args[0];
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getArgs().equalsIgnoreCase(firstArgument)) {
                    if ((subCommand.getAccess().equals(CommandAccessType.ADMIN) && sender.hasPermission(adminPermission)) || (subCommand.getAccess().equals(CommandAccessType.MEMBER) && sender.hasPermission(memberPermission))) {
                        tabCompletion = subCommand.getTabCompletion(sender, args);
                    }
                }
            }
        }

        String arg = args[args.length - 1];
        if(args == null) return tabCompletion;
        List<String> newTab = new ArrayList<>();
        for(String argument : tabCompletion) {
            if(argument.toLowerCase().startsWith(arg.toLowerCase())) {
                newTab.add(argument);
            } else {
                continue;
            }
        }

        if(newTab == null || newTab.isEmpty()) return tabCompletion;
        return newTab;
    }
}
