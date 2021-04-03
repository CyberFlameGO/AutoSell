package net.cloud.autosell.commands.subcommands;

import net.cloud.autosell.builders.SubCommand;
import net.cloud.autosell.commands.AutoSellCommand;
import net.cloud.autosell.enums.CommandAccessType;
import net.cloud.autosell.objects.SellProfile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SummaryCommand extends SubCommand {

    private AutoSellCommand autoSellCommand;
    public SummaryCommand(AutoSellCommand autoSellCommand) {
        this.autoSellCommand = autoSellCommand;
    }

    @Override
    public String getArgs() {
        return "summary";
    }

    @Override
    public CommandAccessType getAccess() {
        return CommandAccessType.MEMBER;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(autoSellCommand.getPlugin().getAutoSellManager().containsPlayer(player.getUniqueId())) {
                SellProfile sellProfile = autoSellCommand.getPlugin().getAutoSellManager().getProfileFromUUID(player.getUniqueId());
                if(sellProfile == null) {
                    autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "NOT-IN-AUTOSELL", null);
                    return;
                }

                sellProfile.sendStatistics();
            } else {
                autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "NOT-IN-AUTOSELL", null);
            }
            return;
        }

        autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "NOT-PLAYER", null);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return null;
    }
}
