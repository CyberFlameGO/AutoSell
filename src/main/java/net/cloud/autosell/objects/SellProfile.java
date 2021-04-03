package net.cloud.autosell.objects;

import lombok.Data;
import net.cloud.autosell.AutoSell;
import net.cloud.autosell.enums.SellMessagesEnum;
import net.cloud.autosell.files.enums.CloudFileType;
import net.cloud.autosell.managers.AutoSellManager;
import net.cloud.autosell.utils.PlaceholderReplacer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
public class SellProfile {

    private AutoSellManager autoSellManager;

    private Long startTime;

    private OfflinePlayer player;
    private Map<Material, Integer> materialMap;
    private Map<Material, Integer> currentSell;

    private Integer taskId;
    private boolean cancelled;

    public SellProfile(OfflinePlayer player, AutoSellManager manager) {
        this.autoSellManager = manager;
        this.player = player;
        this.materialMap = new HashMap<>();

        startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        current();
    }

    public void addMaterial(Material material, int amount) {
        materialMap.put(material, getQuantity(material) + amount);
    }

    public Integer getQuantity(Material material) {
        if(materialMap.containsKey(material)) return materialMap.get(material);
        return 0;
    }

    public void sell() {
        if(!(player.isOnline())) {
            AutoSell.getCore().getAutoSellManager().removePlayer(this);
            return;
        }

        if(currentSell == null) currentSell = new HashMap<>();

        for(int i=0; i < player.getPlayer().getInventory().getSize(); i++) {
            ItemStack item = player.getPlayer().getInventory().getItem(i);
            if(item == null || item.getType().name().equalsIgnoreCase("AIR")) continue;
            if((!(autoSellManager.getPriceManager().isAllowCustomItems())) && isCustomItem(item)) continue;

            if(autoSellManager.getPriceManager().isSellable(player, item.getType())) {
                try {
                    currentSell.put(item.getType(), getCurrentSellAmount(item.getType()) + item.getAmount());
                    materialMap.put(item.getType(), getMaterialMapAmount(item.getType()) + item.getAmount());
                    player.getPlayer().getInventory().setItem(i, null);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void endCurrent(SellMessagesEnum message) {
        double[] deposit = { 0.0 };
        Integer[] items = { 0 };

        if(!(currentSell == null || currentSell.isEmpty())) {
            currentSell.forEach((material, amount) -> {
                if (autoSellManager.getPriceManager().isSellable(player, material)) {
                    deposit[0] = deposit[0] + autoSellManager.getPriceManager().getValue(player, material, amount);
                    items[0] = items[0] + amount;
                }
            });
            currentSell.clear();
        }

        if(player.isOnline()) {
            switch(message) {
                case SOLD:
                    if(items[0] == 0 || deposit[0] == 0) return;

                    PlaceholderReplacer sold = new PlaceholderReplacer().addPlaceholder("%money%", autoSellManager.getAutoSell().getUtils().getDecimalFormat().format(deposit[0]))
                            .addPlaceholder("%items%", autoSellManager.getAutoSell().getUtils().getDecimalFormat().format(items[0]))
                            .addPlaceholder("%time%", getTimeActive());

                    autoSellManager.getAutoSell().getUtils().sendMessage(player.getPlayer(), getAutoSellManager().getAutoSell().getFileUtils().getFileByType(CloudFileType.MESSAGES), "ITEMS-SOLD", sold);
                    break;
                case DEACTIVATED:
                    PlaceholderReplacer deactivated = new PlaceholderReplacer().addPlaceholder("%money%", autoSellManager.getAutoSell().getUtils().getDecimalFormat().format(getTotalSold()))
                            .addPlaceholder("%items%", autoSellManager.getAutoSell().getUtils().getDecimalFormat().format(getTotalItems()))
                            .addPlaceholder("%time%", getTimeActive());

                    autoSellManager.getAutoSell().getUtils().sendMessage(player.getPlayer(), getAutoSellManager().getAutoSell().getFileUtils().getFileByType(CloudFileType.MESSAGES), "DEACTIVATED-AUTO-SELL", deactivated);
                    break;
            }
        }


        autoSellManager.getAutoSell().getVaultHook().addMoney(player, deposit[0]);
    }

    public void current() {
        this.taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(autoSellManager.getAutoSell(), (Runnable) () -> {
            if(cancelled) Bukkit.getScheduler().cancelTask(getTaskId());

            endCurrent(SellMessagesEnum.SOLD);
        }, 20 * autoSellManager.getAutoSell().getFileUtils().getFileByType(CloudFileType.SETTINGS).getInt("Sell-Settings.Sell-Summary", 60), 20 * autoSellManager.getAutoSell().getFileUtils().getFileByType(CloudFileType.SETTINGS).getInt("Sell-Settings.Sell-Summary", 60));
    }

    public void sendStatistics() {
        PlaceholderReplacer deactivated = new PlaceholderReplacer().addPlaceholder("%money%", autoSellManager.getAutoSell().getUtils().getDecimalFormat().format(getTotalSold()))
                .addPlaceholder("%items%", autoSellManager.getAutoSell().getUtils().getDecimalFormat().format(getTotalItems()))
                .addPlaceholder("%time%", getTimeActive());

        autoSellManager.getAutoSell().getUtils().sendMessage(player.getPlayer(), getAutoSellManager().getAutoSell().getFileUtils().getFileByType(CloudFileType.MESSAGES), "COMMAND-SUMMARY", deactivated);
    }

    public void clear() {
        materialMap.clear();
    }

    public String getTimeActive() {
        return AutoSell.getCore().getUtils().getTimeFormat(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) - startTime);
    }

    public Integer getTotalItems() {
        int[] amount = { 0 };

        materialMap.forEach((material, integer) -> {
            amount[0] = amount[0] + integer;
        });
        return amount[0];
    }

    public double getTotalSold() {
        double[] amount = { 0 };

        materialMap.forEach((material, quantity) -> {
            if (autoSellManager.getPriceManager().isSellable(player, material)) {
                amount[0] = amount[0] + autoSellManager.getPriceManager().getValue(player, material, quantity);
            }
        });
        return amount[0];
    }

    public Integer getCurrentSellAmount(Material material) {
        if(currentSell == null) currentSell = new HashMap<>();
        if(currentSell.containsKey(material)) return currentSell.get(material);
        return 0;
    }

    public Integer getMaterialMapAmount(Material material) {
        if(materialMap == null) materialMap = new HashMap<>();
        if(materialMap.containsKey(material)) return materialMap.get(material);
        return 0;
    }

    private boolean isCustomItem(ItemStack item) {
        if(item.hasItemMeta()) return true;
        return false;
    }

}
