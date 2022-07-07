package com.zpedroo.bosses.utils.menu;

import com.google.common.collect.Lists;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.enums.Enchants;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.builder.InventoryBuilder;
import com.zpedroo.bosses.utils.builder.InventoryUtils;
import com.zpedroo.bosses.utils.builder.ItemBuilder;
import com.zpedroo.bosses.utils.color.Colorize;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Menus extends InventoryUtils {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    public Menus() {
        instance = this;
    }

    public void openMainMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.MAIN;

        String title = Colorize.getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);
        PlayerData data = DataManager.getInstance().getPlayerData(player);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{player}",
                    "{boss_points}",
                    "{killed_bosses}"
            }, new String[]{
                    player.getName(),
                    NumberFormatter.getInstance().format(data.getPointsAmount()),
                    NumberFormatter.getInstance().formatDecimal(data.getKilledBossesAmount())
            }).build();
            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventory.addItem(item, slot, () -> {
                if (StringUtils.contains(action, ":")) {
                    String[] split = action.split(":");
                    String command = split.length > 1 ? split[1] : null;
                    if (command == null) return;

                    switch (split[0].toUpperCase()) {
                        case "PLAYER":
                            player.chat("/" + command);
                            break;
                        case "CONSOLE":
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(command, new String[]{
                                    "{player}"
                            }, new String[]{
                                    player.getName()
                            }));
                            break;
                    }
                }

                switch (action.toUpperCase()) {
                    case "TOP":
                        openTopMenu(player);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openTopMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.TOP_BOSSES;

        String title = Colorize.getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        int pos = 0;
        String[] slots = FileUtils.get().getString(file, "Inventory.slots").replace(" ", "").split(",");

        for (PlayerData data : DataManager.getInstance().getCache().getTopBosses()) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Item", new String[]{
                    "{player}",
                    "{boss_points}",
                    "{killed_bosses}",
                    "{pos}"
            }, new String[]{
                    Bukkit.getOfflinePlayer(data.getUniqueId()).getName(),
                    NumberFormatter.getInstance().format(data.getPointsAmount()),
                    NumberFormatter.getInstance().formatDecimal(data.getKilledBossesAmount()),
                    String.valueOf(++pos)
            }).build();

            int slot = Integer.parseInt(slots[pos-1]);

            inventory.addItem(item, slot);
        }

        inventory.open(player);
    }

    public void openUpgradeMenu(Player player, ItemStack itemToUpgrade) {
        FileUtils.Files file = FileUtils.Files.UPGRADE;
        FileConfiguration fileConfiguration = FileUtils.get().getFile(file).get();

        String title = Colorize.getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            String enchantmentName = FileUtils.get().getString(file, "Inventory.items." + str + ".enchant");
            Enchants enchants = Enchants.getByName(enchantmentName);
            Enchant enchant = enchants == null ? null : enchants.get();
            ItemStack item = null;
            String toGet = getItemStatus(itemToUpgrade, enchant);
            if (fileConfiguration.contains("Inventory.items." + str + "." + toGet)) {
                String[] placeholders = getUpgradePlaceholders();
                String[] replacers = getUpgradeReplacers(itemToUpgrade, enchant);

                item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str + "." + toGet, placeholders, replacers).build();
            } else {
                item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            }

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventory.addItem(item, slot, () -> {
                if (enchant == null || !BossKillerUtils.canUpgrade(itemToUpgrade, enchant)) {
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                    return;
                }

                ItemStack newItem = BossKillerUtils.upgradeEnchantment(itemToUpgrade, enchant);

                player.setItemInHand(newItem);
                openUpgradeMenu(player, newItem);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 4f);
            }, ActionType.ALL_CLICKS);
        }

        int itemSlot = FileUtils.get().getInt(file, "Inventory.item-slot");
        if (itemSlot != -1) {
            inventory.addItem(itemToUpgrade, itemSlot);
        }

        inventory.open(player);
    }

    private String[] getUpgradePlaceholders() {
        List<String> placeholders = Lists.newArrayList(BossKillerUtils.getPlaceholders());
        placeholders.add("{cost}");
        placeholders.add("{required_level}");

        return placeholders.toArray(new String[0]);
    }

    private String[] getUpgradeReplacers(@NotNull ItemStack item, Enchant enchant) {
        List<String> replacers = Lists.newArrayList(BossKillerUtils.getReplacers(item));
        replacers.add(NumberFormatter.getInstance().formatThousand(BossKillerUtils.getUpgradeCost(item, enchant)));
        replacers.add(NumberFormatter.getInstance().formatThousand(BossKillerUtils.getUpgradeLevelRequired(item, enchant)));

        return replacers.toArray(new String[0]);
    }

    private String getItemStatus(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return "undefined";
        if (!BossKillerUtils.isUnlocked(item, enchant)) {
            return "locked";
        } else if (BossKillerUtils.isMaxLevel(item, enchant)) {
            return "max-level";
        } else if (!BossKillerUtils.canUpgrade(item, enchant)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }
}