package net.cloud.autosell.prices;

import lombok.Getter;
import net.cloud.autosell.AutoSell;
import net.cloud.autosell.files.enums.CloudFileType;
import net.cloud.autosell.prices.hooks.CustomProvider;
import net.cloud.autosell.prices.hooks.EssentialsProvider;
import net.cloud.autosell.prices.hooks.ShopGUIPlusProvider;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PriceManager {

    @Getter private AutoSell plugin;

    @Getter private boolean allowCustomItems;
    @Getter private boolean onlyUseWhitelisted;

    @Getter private List<Material> whitelistedMaterials;
    @Getter private List<Material> blacklistedMaterials;

    public PriceManager(AutoSell autoSell) {
        plugin = autoSell;
        provider = loadPriceProviders();

        allowCustomItems = plugin.getFileUtils().getFileByType(CloudFileType.SETTINGS).getBoolean("Item-Settings.Allow-Custom-Items", false);
        onlyUseWhitelisted = plugin.getFileUtils().getFileByType(CloudFileType.SETTINGS).getBoolean("Item-Settings.Use-Whitelisted-Only", false);
        loadLists();
    }

    @Getter private IPricing provider;

    private IPricing loadPriceProviders() {
        String provider = plugin.getFileUtils().getFileByType(CloudFileType.SETTINGS).getString("Price-Provider", "CUSTOM");
        switch(provider.toUpperCase()) {
            case "SHOPGUIPLUS":
                plugin.getLogger().info("Loaded ShopGUIPlus Pricing Integration.");
                return new ShopGUIPlusProvider();
            case "ESSENTIALS":
                plugin.getLogger().info("Loaded Essentials Pricing Integration.");
                return new EssentialsProvider();
            default:
                plugin.getLogger().info("Loaded Custom Pricing Integration.");
                return new CustomProvider(this);
        }
    }


    public boolean isSellable(Player player, Material material) {
        if(blacklistedMaterials.contains(material)) return false;
        if((!(onlyUseWhitelisted)) || (onlyUseWhitelisted && isWhitelisted(material))) {
            return provider.isSellable(player, material);
        }
        return false;
    }

    public boolean isSellable(OfflinePlayer player, Material material) {
        if(blacklistedMaterials.contains(material)) return false;
        if((!(onlyUseWhitelisted)) || (onlyUseWhitelisted && isWhitelisted(material))) {
            return provider.isSellable(player, material);
        }
        return false;
    }

    public double getValue(Player player, Material material, Integer amount) {
        if(blacklistedMaterials.contains(material)) return 0.0;
        if((!(onlyUseWhitelisted)) || (onlyUseWhitelisted && isWhitelisted(material))) {
            return plugin.getBoosterManager().calculate(player, provider.getValue(player, material, amount));
        }
        return 0.0;
    }

    public double getValue(OfflinePlayer player, Material material, Integer amount) {
        if(blacklistedMaterials.contains(material)) return 0.0;
        if((!(onlyUseWhitelisted)) || (onlyUseWhitelisted && isWhitelisted(material))) {
            return plugin.getBoosterManager().calculate(player, provider.getValue(player, material, amount));
        }
        return 0.0;
    }

    public void loadLists() {
        FileConfiguration settings = plugin.getFileUtils().getFileByType(CloudFileType.SETTINGS);

        whitelistedMaterials = new ArrayList<>();
        blacklistedMaterials = new ArrayList<>();

        /*
        Whitelist
         */

        if(settings.contains("Whitelisted-Items") && settings.isList("Whitelisted-Items")) {
            settings.getStringList("Whitelisted-Items").forEach(item -> {
                if(isMaterial(item)) {
                    whitelistedMaterials.add(Material.matchMaterial(item));
                }
            });
        }

        /*
        Blacklist
         */

        if(settings.contains("Blacklisted-Items") && settings.isList("Blacklisted-Items")) {
            settings.getStringList("Blacklisted-Items").forEach(item -> {
                if(isMaterial(item)) {
                    whitelistedMaterials.add(Material.matchMaterial(item));
                }
            });
        }
    }

    public boolean isWhitelisted(Material material) {
        if(whitelistedMaterials.contains(material)) return true;
        return false;
    }

    public boolean isBlacklisted(Material material) {
        if(blacklistedMaterials.contains(material)) return true;
        return false;
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
