package net.cloud.autosell.builders;

import net.cloud.autosell.enums.CommandAccessType;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract String getArgs();
    public abstract CommandAccessType getAccess();
    public abstract void execute(CommandSender sender, String[] args);
    public abstract List<String> getTabCompletion(CommandSender sender, String[] args);

}
