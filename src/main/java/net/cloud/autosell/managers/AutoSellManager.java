package net.cloud.autosell.managers;

import lombok.Getter;
import net.cloud.autosell.AutoSell;
import net.cloud.autosell.enums.SellMessagesEnum;
import net.cloud.autosell.objects.SellProfile;
import net.cloud.autosell.prices.PriceManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class AutoSellManager {

    @Getter private AutoSell autoSell;
    @Getter private PriceManager priceManager;

    public AutoSellManager(AutoSell plugin) {
        this.autoSell = plugin;

        sellProfiles = new HashMap<>();
    }

    private Map<UUID, SellProfile> sellProfiles;

    public void addPlayer(OfflinePlayer player) {
        if(sellProfiles.containsKey(player.getUniqueId()))  {
            sellProfiles.get(player.getUniqueId()).clear();
            return;
        }

        sellProfiles.put(player.getUniqueId(), new SellProfile(player, this));
    }

    public void removePlayer(OfflinePlayer player) {
        if(sellProfiles.containsKey(player.getUniqueId())) {
            SellProfile sellProfile = sellProfiles.get(player.getUniqueId());
            Bukkit.getScheduler().cancelTask(sellProfile.getTaskId());
            sellProfile.endCurrent(SellMessagesEnum.DEACTIVATED);

            sellProfiles.remove(player.getUniqueId());
        }
    }

    public void removePlayer(SellProfile profile) {
        if(sellProfiles.containsKey(profile.getPlayer().getUniqueId())) {
            Bukkit.getScheduler().cancelTask(profile.getTaskId());
            profile.endCurrent(SellMessagesEnum.DEACTIVATED);

            sellProfiles.remove(profile.getPlayer().getUniqueId());
        }
    }

    public boolean containsPlayer(UUID player) {
        if(sellProfiles.containsKey(player)) return true;
        return false;
    }

    public void sell(OfflinePlayer player) {
        SellProfile sellProfile = getProfileFromUUID(player.getUniqueId());
        if(sellProfile == null) return;

        sellProfile.sell();
    }

    public List<SellProfile> getSellProfiles() {
        return new ArrayList<>(sellProfiles.values());
    }

    public SellProfile getProfileFromUUID(UUID uuid) {
        return sellProfiles.get(uuid);
    }

    public PriceManager getPriceManager() {
        return autoSell.getPriceManager();
    }

}
