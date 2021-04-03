package net.cloud.autosell.boosters;

import org.bukkit.OfflinePlayer;

public interface IBooster {

    String getName();
    boolean hasBoosters(OfflinePlayer player);
    double getBooster(OfflinePlayer player);

}
