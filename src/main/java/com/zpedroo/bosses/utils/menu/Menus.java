package com.zpedroo.bosses.utils.menu;

import com.google.common.collect.Lists;
import com.zpedroo.bosses.enums.Enchants;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.utils.FileUtils;
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

        for (String items : FileUtils.get().getSection(file, "Inventory.items")) {
            String action = FileUtils.get().getString(file, "Inventory.items." + items + ".action");
            String[] split = action.split(":");
            String upgradeElementName = split.length > 1 ? split[1] : "NULL";
            Enchant enchant = Enchants.getEnchantByName(upgradeElementName);
            ItemStack item = buildUpgradeItem(itemToUpgrade, fileConfiguration, items, action, enchant);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + items + ".slot");

            inventory.addItem(item, slot, () -> {
                if (!StringUtils.containsIgnoreCase(action, "UPGRADE:")) return;

                ItemStack newItem = null;
                switch (upgradeElementName.toUpperCase()) {
                    case "QUALITY":
                        if (!BossKillerUtils.canUpgradeQuality(itemToUpgrade)) {
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                            return;
                        }

                        newItem = BossKillerUtils.upgradeQuality(itemToUpgrade);
                        break;
                    default:
                        if (enchant == null) return;
                        if (!BossKillerUtils.canUpgradeEnchant(itemToUpgrade, enchant)) {
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                            return;
                        }

                        newItem = BossKillerUtils.upgradeEnchantment(itemToUpgrade, enchant);
                        break;
                }

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

    @NotNull
    private ItemStack buildUpgradeItem(ItemStack itemToUpgrade, FileConfiguration fileConfiguration, String items, String action, Enchant enchant) {
        ItemStack item = null;
        String toGet = getElementToGet(itemToUpgrade, action, enchant);

        if (fileConfiguration.contains("Inventory.items." + items + "." + toGet)) {
            String[] placeholders = getUpgradePlaceholders();
            String[] replacers = getUpgradeReplacers(itemToUpgrade, enchant);

            item = ItemBuilder.build(fileConfiguration, "Inventory.items." + items + "." + toGet, placeholders, replacers).build();
        } else {
            item = ItemBuilder.build(fileConfiguration, "Inventory.items." + items).build();
        }

        return item;
    }

    @Nullable
    private String getElementToGet(ItemStack itemToUpgrade, String action, Enchant enchant) {
        String toGet = null;
        if (enchant != null) {
            toGet = getItemEnchantStatus(itemToUpgrade, enchant);
        } else if (StringUtils.containsIgnoreCase(action, "QUALITY")) {
            toGet = getItemQualityStatus(itemToUpgrade);
        }

        return toGet;
    }

    private String[] getUpgradePlaceholders() {
        List<String> placeholders = Lists.newArrayList(BossKillerUtils.getPlaceholders());
        placeholders.add("{cost}");
        placeholders.add("{required_level}");

        return placeholders.toArray(new String[0]);
    }

    private String[] getUpgradeReplacers(@NotNull ItemStack item, @Nullable Enchant enchant) {
        List<String> replacers = Lists.newArrayList(BossKillerUtils.getReplacers(item));
        replacers.add(NumberFormatter.getInstance().formatThousand(
                enchant == null ? BossKillerUtils.getQualityUpgradeCost(item) : BossKillerUtils.getEnchantUpgradeCost(item, enchant))
        );
        replacers.add(NumberFormatter.getInstance().formatThousand(
                enchant == null ? BossKillerUtils.getQualityUpgradeLevelRequired(item) : BossKillerUtils.getEnchantUpgradeLevelRequired(item, enchant))
        );

        return replacers.toArray(new String[0]);
    }

    private String getItemEnchantStatus(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return "undefined";
        if (BossKillerUtils.isMaxEnchantLevel(item, enchant)) {
            return "max-level";
        } else if (!BossKillerUtils.isUnlockedEnchant(item, enchant)) {
            return "locked";
        } else if (!BossKillerUtils.canUpgradeEnchant(item, enchant)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }

    private String getItemQualityStatus(@NotNull ItemStack item) {
        if (BossKillerUtils.isMaxQualityLevel(item)) {
            return "max-level";
        } else if (!BossKillerUtils.isUnlockedQuality(item)) {
            return "locked";
        } else if (!BossKillerUtils.canUpgradeQuality(item)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }
}