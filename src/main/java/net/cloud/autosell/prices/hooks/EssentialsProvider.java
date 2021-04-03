package net.cloud.autosell.prices.hooks;

import com.earth2me.essentials.Essentials;
import lombok.Getter;
import net.cloud.autosell.prices.IPricing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class EssentialsProvider implements IPricing {

    @Getter private boolean enabled;
    @Getter private Essentials essentials;

    public EssentialsProvider() {
        this.enabled = Bukkit.getServer().getPluginManager().isPluginEnabled("Essentials") ? true : false;
        if(enabled) essentials = Essentials.getPlugin(Essentials.class);
    }


    @Override
    public String getName() {
        return "Essentials";
    }

    @Override
    public boolean isSellable(OfflinePlayer player, Material material) {
        if(enabled) {
            try {
                return (essentials.getWorth().getPrice(null, new ItemStack(material, 1)).doubleValue() <= 0 ? false : true);
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public double getValue(OfflinePlayer player, Material material, Integer amount) {
        if(enabled) {
            return (essentials.getWorth().getPrice(null, new ItemStack(material, 1))).doubleValue() * amount;
        }
        return 0.0;
    }
}
