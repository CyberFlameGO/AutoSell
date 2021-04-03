package net.cloud.autosell.hooks;

import lombok.Getter;
import net.cloud.autosell.AutoSell;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private AutoSell plugin;

    @Getter
    private Economy economy;

    public VaultHook(AutoSell autoSell) {
        plugin = autoSell;
    }

    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean addMoney(OfflinePlayer player, double amount) {
        EconomyResponse economyResponse = economy.depositPlayer(player, amount);
        return economyResponse.transactionSuccess() ? true : false;
    }

}
