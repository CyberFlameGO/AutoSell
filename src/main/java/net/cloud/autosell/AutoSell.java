package net.cloud.autosell;

import lombok.Getter;
import net.cloud.autosell.boosters.BoosterManager;
import net.cloud.autosell.commands.AutoSellCommand;
import net.cloud.autosell.files.FileManager;
import net.cloud.autosell.files.FileUtils;
import net.cloud.autosell.files.enums.CloudFileType;
import net.cloud.autosell.files.logs.Debugger;
import net.cloud.autosell.hooks.VaultHook;
import net.cloud.autosell.managers.AutoSellManager;
import net.cloud.autosell.objects.SellProfile;
import net.cloud.autosell.prices.PriceManager;
import net.cloud.autosell.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AutoSell extends JavaPlugin {

    @Override
    public void onEnable() {
        core = this;

        loadFiles();

        vaultHook = new VaultHook(this);
        if(!(vaultHook.setupEconomy())) {
            getLogger().warning("Vault was not found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadClasses();
        autoSell();

        autoSellCommand = new AutoSellCommand(this);
        autoSellCommand.register();
    }

    @Override
    public void onDisable() {
    }

    public void loadFiles() {
        fileManager = new FileManager(this);
        fileUtils = new FileUtils(this);
        debugger = new Debugger(this, getDataFolder() + File.separator + "logs" + File.separator + "logs.txt");
    }

    public void loadClasses() {
        utils = new Utils(this);
        boosterManager = new BoosterManager(this);
        priceManager = new PriceManager(this);
        autoSellManager = new AutoSellManager(this);
    }

    public void autoSell() {
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                autoSellManager.getSellProfiles().forEach(SellProfile::sell);
            }
        }, fileUtils.getFileByType(CloudFileType.SETTINGS).getInt("Sell-Settings.Sell-Inventory", 20), fileUtils.getFileByType(CloudFileType.SETTINGS).getInt("Sell-Settings.Sell-Inventory", 20));
    }

    public void reload() {
        loadFiles();
        loadClasses();
    }

    @Getter private FileManager fileManager;
    @Getter private FileUtils fileUtils;
    @Getter private Debugger debugger;

    @Getter private Utils utils;
    @Getter private VaultHook vaultHook;
    @Getter private BoosterManager boosterManager;
    @Getter private PriceManager priceManager;
    @Getter private AutoSellManager autoSellManager;
    @Getter private AutoSellCommand autoSellCommand;

    @Getter private static AutoSell core;

}
