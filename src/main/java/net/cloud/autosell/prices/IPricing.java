package net.cloud.autosell.prices;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public interface IPricing {

    String getName();
    boolean isSellable(OfflinePlayer player, Material material);
    double getValue(OfflinePlayer player, Material material, Integer amount);

}
