package net.cloud.autosell.boosters;

import lombok.Getter;
import net.cloud.autosell.AutoSell;
import net.cloud.autosell.boosters.hooks.SuperBoostersProvider;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class BoosterManager {

    @Getter private AutoSell plugin;
    public BoosterManager(AutoSell autoSell) {
        plugin = autoSell;
        providers = new ArrayList<>();

        addProvider(new SuperBoostersProvider(this));
    }

    private List<IBooster> providers;

    public void addProvider(IBooster booster) {
        providers.add(booster);
    }

    public double calculate(OfflinePlayer player, double amount) {
        double[] calculated = { amount };

        providers.forEach(provider -> {
            double multiplier = provider.getBooster(player);
            if(multiplier == 0 || multiplier == 1) return;

            calculated[0] = calculated[0] + ((amount * multiplier) - amount);
        });

        return calculated[0];
    }

}
