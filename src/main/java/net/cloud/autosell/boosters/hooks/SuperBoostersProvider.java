package net.cloud.autosell.boosters.hooks;

import me.swanis.boosters.BoostersAPI;
import me.swanis.boosters.booster.ActiveBooster;
import net.cloud.autosell.boosters.BoosterManager;
import net.cloud.autosell.boosters.IBooster;
import net.cloud.autosell.files.enums.CloudFileType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class SuperBoostersProvider implements IBooster {

    private boolean enabled;
    private boolean configEnabled;

    public SuperBoostersProvider(BoosterManager boosterManager) {
        enabled = Bukkit.getPluginManager().isPluginEnabled("SuperBoosters") ? true : false;
        configEnabled = boosterManager.getPlugin().getFileUtils().getFileByType(CloudFileType.SETTINGS).getBoolean("Booster-Settings.Use-Super-Boosters", false);
    }

    @Override
    public String getName() {
        return "SuperBoosters";
    }

    @Override
    public boolean hasBoosters(OfflinePlayer player) {
        if (enabled) {
            for(ActiveBooster booster : BoostersAPI.getBoosterManager().getActiveBoosters()) {
                if (booster.getBooster().getName().equalsIgnoreCase("money")) return true;
            }
        }
        return false;
    }

    public boolean hasPersonalBoosters(OfflinePlayer player) {
        if(enabled && configEnabled) {
            for(ActiveBooster booster : BoostersAPI.getBoosterManager().getActivePersonalBoosters(player.getUniqueId())) {
                if(booster.getBooster().getName().equalsIgnoreCase("money")) return true;
            }
        }
        return false;
    }

    @Override
    public double getBooster(OfflinePlayer player) {
        double multiplier = 0;

        if(enabled && configEnabled && hasBoosters(player)) multiplier = multiplier + 2;
        if(enabled && configEnabled && hasPersonalBoosters(player)) {
            if(multiplier == 0) {
                multiplier = multiplier + 2;
            } else {
                multiplier = multiplier + 1;
            }
        }

        return multiplier;
    }
}
