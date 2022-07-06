package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.bosskiller.BossKillerEnchant;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.builder.ItemBuilder;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Items {

    private static final ItemStack BOSS_POINTS_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Points-Item").build();
    private static final ItemStack BOSS_KILLER_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Boss-Killer-Item").build();
    private static final ItemStack BOSS_SPAWNER_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Spawner-Item").build();

    @NotNull
    public static ItemStack getBossPointsItem(int amount) {
        NBTItem nbt = new NBTItem(BOSS_POINTS_ITEM.clone());
        nbt.setInteger(BossKillerUtils.POINTS_NBT, amount);

        String[] placeholders = new String[]{
                "{amount}"
        };
        String[] replacers = new String[]{
                NumberFormatter.getInstance().formatThousand(amount)
        };

        return replaceItemPlaceholders(nbt.getItem(), placeholders, replacers);
    }

    @NotNull
    public static ItemStack getBossKillerItem() {
        NBTItem nbt = new NBTItem(BOSS_KILLER_ITEM.clone());
        nbt.setBoolean("BossKiller", true);

        ItemStack item = nbt.getItem();
        return replaceItemPlaceholders(item, BossKillerUtils.getPlaceholders(), BossKillerUtils.getReplacers(item));
    }

    @NotNull
    public static ItemStack getBossKillerItem(@NotNull ItemStack baseItem) {
        NBTItem nbt = new NBTItem(BOSS_KILLER_ITEM.clone());
        nbt.setBoolean(BossKillerUtils.IDENTIFIER_NBT, true);

        for (BossKillerEnchant bossKillerEnchant : BossKillerEnchant.values()) {
            Enchant enchant = bossKillerEnchant.get();
            int level = BossKillerUtils.getEnchantmentLevel(baseItem, enchant);
            if (level <= enchant.getInitialLevel()) continue;

            nbt.setInteger(enchant.getName(), level);
        }

        nbt.setDouble(BossKillerUtils.EXPERIENCE_NBT, BossKillerUtils.getItemExperience(baseItem));
        nbt.setInteger(BossKillerUtils.POINTS_NBT, BossKillerUtils.getItemPoints(baseItem));

        ItemStack item = nbt.getItem();
        return replaceItemPlaceholders(item, BossKillerUtils.getPlaceholders(), BossKillerUtils.getReplacers(item));
    }

    @NotNull
    public static ItemStack getBossSpawnerItem() {
        NBTItem nbt = new NBTItem(BOSS_SPAWNER_ITEM.clone());
        nbt.addCompound("BossSpawner");

        return nbt.getItem();
    }

    @NotNull
    private static ItemStack replaceItemPlaceholders(ItemStack item, String[] placeholders, String[] replacers) {
        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
            ItemMeta meta = item.getItemMeta();
            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, placeholders, replacers));
            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());
                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, placeholders, replacers));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}