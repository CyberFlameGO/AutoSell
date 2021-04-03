package net.cloud.autosell.commands.subcommands;

import net.cloud.autosell.builders.SubCommand;
import net.cloud.autosell.commands.AutoSellCommand;
import net.cloud.autosell.enums.CommandAccessType;
import net.cloud.autosell.objects.SellProfile;
import net.cloud.autosell.utils.PlaceholderReplacer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StatusCommand extends SubCommand {

    private AutoSellCommand autoSellCommand;
    public StatusCommand(AutoSellCommand autoSellCommand) {
        this.autoSellCommand = autoSellCommand;
    }

    @Override
    public String getArgs() {
        return "status";
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
                autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "AUTO-SELL-STATUS", new PlaceholderReplacer().addPlaceholder("%status%", getFormat(true)));
            } else {
                autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "AUTO-SELL-STATUS", new PlaceholderReplacer().addPlaceholder("%status%", getFormat(false)));
            }
            return;
        }

        autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "NOT-PLAYER", null);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return null;
    }

    public String getFormat(boolean active) {
        return autoSellCommand.getMessagesConfig().getString("AUTO-SELL-STATUS." + (active ? "Activated" : "Deactivated") + "-Format");
    }
}
