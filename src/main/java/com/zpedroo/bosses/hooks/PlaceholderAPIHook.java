package com.zpedroo.bosses.hooks;

import com.zpedroo.bosses.enums.EnchantProperty;
import com.zpedroo.bosses.enums.Enchants;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.progress.ProgressConverter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

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
        ItemStack item = player.getItemInHand();
        double experience = BossKillerUtils.getItemExperience(item);
        switch (identifier.toUpperCase()) {
            case "KILLED":
                return NumberFormatter.getInstance().formatDecimal(data.getKilledBossesAmount());
            case "HEALTH":
                BossSpawner bossSpawner = DataManager.getInstance().getLastActiveBossSpawner();
                if (bossSpawner == null) return "-/-";

                return bossSpawner.getBossHealth() <= 0 ? "-/-" : NumberFormatter.getInstance().formatThousand(bossSpawner.getBossHealth());
            case "LEVEL":
                int level = BossKillerUtils.getItemLevel(item);
                return NumberFormatter.getInstance().formatThousand(level);
            case "QUALITY":
                int quality = BossKillerUtils.getItemQuality(item);
                return ProgressConverter.convertQuality(quality);
            case "PROGRESS":
                return ProgressConverter.convertExperience(experience);
            case "PERCENTAGE":
                return NumberFormatter.getInstance().formatDecimal(ProgressConverter.getPercentage(experience));
            case "POINTS":
                BigInteger pointsAmount = BossKillerUtils.getItemPoints(item);
                return NumberFormatter.getInstance().format(pointsAmount);
            case "ITEM_DAMAGE":
                int damage = (int) BossKillerUtils.getEnchantEffectByItem(item, Enchants.DAMAGE.get(), EnchantProperty.DAMAGE);
                return NumberFormatter.getInstance().formatThousand(damage);
        }

        return null;
    }
}