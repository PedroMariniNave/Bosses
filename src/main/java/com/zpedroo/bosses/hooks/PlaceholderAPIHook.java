package com.zpedroo.bosses.hooks;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Plugin plugin;

    public PlaceholderAPIHook(Plugin plugin) {
        this.plugin = plugin;
        this.register();
    }

    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @NotNull
    public String getIdentifier() {
        return "bosses";
    }

    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        PlayerData data = DataManager.getInstance().getPlayerData(player);
        switch (identifier.toUpperCase()) {
            case "KILLED":
                return NumberFormatter.getInstance().formatDecimal(data.getKilledBossesAmount());
            case "POINTS":
                return NumberFormatter.getInstance().format(data.getPointsAmount());
            case "HEALTH":
                BossSpawner bossSpawner = DataManager.getInstance().getLastActiveBossSpawner();
                if (bossSpawner == null) return "-/-";

                return bossSpawner.getBossHealth() <= 0 ? "-/-" : NumberFormatter.getInstance().formatThousand(bossSpawner.getBossHealth());
            default:
                return null;
        }
    }
}