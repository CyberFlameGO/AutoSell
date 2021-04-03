package net.cloud.autosell.prices.hooks;

import com.earth2me.essentials.Essentials;
import lombok.Getter;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.cloud.autosell.prices.IPricing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusProvider implements IPricing {

    @Getter private boolean enabled;

    public ShopGUIPlusProvider() {
        this.enabled = Bukkit.getServer().getPluginManager().isPluginEnabled("ShopGUIPlus") ? true : false;
    }


    @Override
    public String getName() {
        return "ShopGUIPlus";
    }

    @Override
    public boolean isSellable(OfflinePlayer player, Material material) {
        if(enabled) {
            try {
                return (ShopGuiPlusApi.getItemStackPriceSell(player.getPlayer(), new ItemStack(material, 1)) <= 0 ? false : true);
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public double getValue(OfflinePlayer player, Material material, Integer amount) {
        if(enabled) {
            return (ShopGuiPlusApi.getItemStackPriceSell(player.getPlayer(), new ItemStack(material, 1)) * amount);
        }
        return 0.0;
    }
}
