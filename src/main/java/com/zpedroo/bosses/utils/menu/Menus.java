package com.zpedroo.bosses.utils.menu;

import com.zpedroo.bosses.listeners.PlayerChatListener;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.EnchantProperties;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.general.ShopItem;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.bosskiller.BossKillerEnchant;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.builder.InventoryBuilder;
import com.zpedroo.bosses.utils.builder.InventoryUtils;
import com.zpedroo.bosses.utils.builder.ItemBuilder;
import com.zpedroo.bosses.utils.color.Colorize;
import com.zpedroo.bosses.utils.config.Messages;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Menus extends InventoryUtils {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    private final ItemStack nextPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Next-Page").build();
    private final ItemStack previousPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Previous-Page").build();

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
                    case "SHOP":
                        openShopMenu(player);
                        break;

                    case "TOP":
                        openTopMenu(player);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openShopMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.SHOP;

        String title = Colorize.getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        int nextPageSlot = FileUtils.get().getInt(file, "Inventory.next-page-slot");
        int previousPageSlot = FileUtils.get().getInt(file, "Inventory.previous-page-slot");

        InventoryBuilder inventory = new InventoryBuilder(title, size, previousPageItem, previousPageSlot, nextPageItem, nextPageSlot);
        List<ShopItem> shopItems = DataManager.getInstance().getCache().getShopItems();

        int i = -1;
        String[] slots = FileUtils.get().getString(file, "Inventory.item-slots").replace(" ", "").split(",");
        for (ShopItem item : shopItems) {
            if (item == null) continue;
            if (++i >= slots.length) i = 0;

            ItemStack display = item.getDisplay().clone();
            int slot = Integer.parseInt(slots[i]);

            inventory.addItem(display, slot, () -> {
                player.closeInventory();
                PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(player, item));
                for (String msg : Messages.CHOOSE_AMOUNT) {
                    if (msg == null) continue;

                    player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                            "{item}",
                            "{price}"
                    }, new String[]{
                            item.getDisplay().hasItemMeta() ? item.getDisplay().getItemMeta().hasDisplayName() ? item.getDisplay().getItemMeta().getDisplayName() : item.getDisplay().getType().toString() : item.getDisplay().getType().toString(),
                            NumberFormatter.getInstance().format(item.getPrice())
                    }));
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
            BossKillerEnchant bossKillerEnchant = BossKillerEnchant.getByName(enchantmentName);
            EnchantProperties enchant = bossKillerEnchant == null ? null : bossKillerEnchant.get();
            ItemStack item = null;
            String toGet = getItemStatus(itemToUpgrade, enchant);
            if (fileConfiguration.contains("Inventory.items." + str + "." + toGet)) {
                String[] placeholders = BossKillerUtils.getPlaceholders();
                String[] replacers = BossKillerUtils.getReplacers(itemToUpgrade);

                item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str + "." + toGet, placeholders, replacers).build();
            } else {
                item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            }

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventory.addItem(item, slot, () -> {
                if (enchant == null) return;
                if (BossKillerUtils.canUpgrade(itemToUpgrade, enchant)) return;


            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    private String getItemStatus(@NotNull ItemStack item, @Nullable EnchantProperties enchant) {
        if (enchant == null) return "undefined";
        if (!BossKillerUtils.canUpgrade(item, enchant)) {
            return "locked";
        }

        return "can-upgrade";
    }
}