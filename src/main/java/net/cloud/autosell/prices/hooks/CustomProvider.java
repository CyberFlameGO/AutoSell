package net.cloud.autosell.prices.hooks;

import net.cloud.autosell.files.enums.CloudFileType;
import net.cloud.autosell.prices.IPricing;
import net.cloud.autosell.prices.PriceManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class CustomProvider implements IPricing {

    private PriceManager priceManager;
    public CustomProvider(PriceManager priceManager) {
        this.priceManager = priceManager;
        this.prices = new HashMap<>();

        load();
    }

    private Map<Material, Double> prices;

    @Override
    public String getName() {
        return "Custom";
    }

    @Override
    public boolean isSellable(OfflinePlayer player, Material material) {
        if(prices.containsKey(material)) return true;
        return false;
    }

    @Override
    public double getValue(OfflinePlayer player, Material material, Integer amount) {
        if(prices.containsKey(material)) return prices.get(material) * amount;
        return 0.0;
    }

    private void load() {
        FileConfiguration pricesConfig = priceManager.getPlugin().getFileUtils().getFileByType(CloudFileType.PRICES);
        if(pricesConfig.contains("Prices") && pricesConfig.isConfigurationSection("Prices")) {
            pricesConfig.getConfigurationSection("Prices").getKeys(false).forEach(price -> {
                if(isMaterial(price.toUpperCase())) {
                    double value = pricesConfig.getDouble("Prices." + price, 0);

                    prices.put(Material.matchMaterial(price.toUpperCase()), value);
                }
            });
        }
    }

    public boolean isMaterial(String args) {
        try {
            if(Material.matchMaterial(args) != null) return true;
            return false;
        } catch(Exception e) {
            return false;
        }
    }

}
