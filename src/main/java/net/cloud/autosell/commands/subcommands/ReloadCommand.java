package net.cloud.autosell.commands.subcommands;

import net.cloud.autosell.builders.SubCommand;
import net.cloud.autosell.commands.AutoSellCommand;
import net.cloud.autosell.enums.CommandAccessType;
import net.cloud.autosell.utils.PlaceholderReplacer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends SubCommand {

    private AutoSellCommand autoSellCommand;
    public ReloadCommand(AutoSellCommand autoSellCommand) {
        this.autoSellCommand = autoSellCommand;
    }

    @Override
    public String getArgs() {
        return "reload";
    }

    @Override
    public CommandAccessType getAccess() {
        return CommandAccessType.ADMIN;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();

        autoSellCommand.getPlugin().reload();
        autoSellCommand.getPlugin().getUtils().sendMessage(sender, autoSellCommand.getMessagesConfig(), "PLUGIN-RELOADED", new PlaceholderReplacer().addPlaceholder("%ms%", String.valueOf(System.currentTimeMillis() - startTime)));
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String[] args) {
        return null;
    }
}
