package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.builder.ItemBuilder;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Items {

    private static final ItemStack bossPointsItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Points-Item").build();
    private static final ItemStack bossKillerItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Boss-Killer-Item").build();
    private static final ItemStack bossKillerArrow = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Boss-Killer-Arrow").build();
    private static final ItemStack bossSpawnerItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Spawner-Item").build();

    public static ItemStack getBossPointsItem(BigInteger amount) {
        NBTItem nbt = new NBTItem(bossPointsItem.clone());
        nbt.setString("BossPointsAmount", amount.toString());

        ItemStack item = nbt.getItem();
        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
            ItemMeta meta = item.getItemMeta();

            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, new String[]{
                    "{amount}"
            }, new String[]{
                    NumberFormatter.getInstance().format(amount)
            }));

            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());

                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, new String[]{
                            "{amount}"
                    }, new String[]{
                            NumberFormatter.getInstance().format(amount)
                    }));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack getBossKillerItem(double damage) {
        NBTItem nbt = new NBTItem(bossKillerItem.clone());
        nbt.setDouble("BossKillerDamage", damage);

        ItemStack item = nbt.getItem();
        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
            ItemMeta meta = item.getItemMeta();

            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, new String[]{
                    "{damage}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(damage)
            }));

            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());

                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, new String[]{
                            "{damage}"
                    }, new String[]{
                            NumberFormatter.getInstance().formatDecimal(damage)
                    }));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack getBossKillerArrow(double damage) {
        NBTItem nbt = new NBTItem(bossKillerArrow.clone());
        nbt.setDouble("BossKillerArrowDamage", damage);

        ItemStack item = nbt.getItem();
        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
            ItemMeta meta = item.getItemMeta();

            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, new String[]{
                    "{damage}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(damage)
            }));

            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());

                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, new String[]{
                            "{damage}"
                    }, new String[]{
                            NumberFormatter.getInstance().formatDecimal(damage)
                    }));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack getBossSpawnerItem() {
        NBTItem nbt = new NBTItem(bossSpawnerItem.clone());
        nbt.addCompound("BossSpawner");

        return nbt.getItem();
    }
}