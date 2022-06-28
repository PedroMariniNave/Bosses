package com.zpedroo.bosses.listeners;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.utils.inventory.InventoryChecker;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.general.ShopItem;
import com.zpedroo.bosses.utils.config.Messages;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.*;

public class PlayerChatListener implements Listener {

    private static final Map<Player, PlayerChat> playerChat = new HashMap<>(4);

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!playerChat.containsKey(event.getPlayer())) return;

        Player player = event.getPlayer();
        PlayerData data = DataManager.getInstance().getPlayerData(player);

        event.setCancelled(true);

        PlayerChat playerChat = getPlayerChat().remove(player);
        int amount = NumberFormatter.getInstance().filter(event.getMessage()).intValue();

        if (amount <= 0) {
            player.sendMessage(Messages.INVALID_AMOUNT);
            return;
        }

        ShopItem item = playerChat.getItem();
        int limit = item.getDisplay().getMaxStackSize() == 1 ? 36 : 2304;
        if (amount > limit) amount = limit;

        int freeSpace = InventoryChecker.getFreeSpace(player, item.getDisplay());
        if (freeSpace < amount) {
            player.sendMessage(StringUtils.replaceEach(Messages.NEED_SPACE, new String[]{
                    "{has}",
                    "{need}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(freeSpace),
                    NumberFormatter.getInstance().formatDecimal(amount)
            }));
            return;
        }

        BigInteger points = data.getPointsAmount();
        BigInteger finalPrice = item.getPrice().multiply(BigInteger.valueOf(amount));

        if (points.compareTo(finalPrice) < 0) {
            player.sendMessage(StringUtils.replaceEach(Messages.INSUFFICIENT_POINTS, new String[]{
                    "{has}",
                    "{need}"
            }, new String[]{
                    NumberFormatter.getInstance().format(points),
                    NumberFormatter.getInstance().format(finalPrice)
            }));
            return;
        }

        data.removePoints(finalPrice);
        if (item.getShopItem() != null) {
            ItemStack toGive = item.getShopItem().clone();
            if (toGive.getMaxStackSize() == 64) {
                toGive.setAmount(amount);
                player.getInventory().addItem(toGive);
                return;
            }

            for (int i = 0; i < amount; ++i) {
                player.getInventory().addItem(toGive);
            }
        }

        for (String cmd : item.getCommands()) {
            final int finalAmount = amount * item.getDefaultAmount();
            if (StringUtils.contains(cmd, "{amount}")) {
                VoltzBosses.get().getServer().getScheduler().runTaskLater(VoltzBosses.get(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(cmd, new String[]{
                        "{player}",
                        "{amount}"
                }, new String[]{
                        player.getName(),
                        String.valueOf(finalAmount)
                })), 0L);
            } else {
                for (int i = 0; i < finalAmount; ++i) {
                    VoltzBosses.get().getServer().getScheduler().runTaskLater(VoltzBosses.get(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(cmd, new String[]{
                            "{player}"
                    }, new String[]{
                            player.getName()
                    })), 0L);
                }
            }
        }

        for (String msg : Messages.SUCCESSFUL_PURCHASED) {
            player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                    "{item}",
                    "{amount}",
                    "{price}"
            }, new String[]{
                    item.getDisplay().hasItemMeta() ? item.getDisplay().getItemMeta().hasDisplayName() ? item.getDisplay().getItemMeta().getDisplayName() : item.getDisplay().getType().toString() : item.getDisplay().getType().toString(),
                    NumberFormatter.getInstance().formatDecimal(amount),
                    NumberFormatter.getInstance().format(finalPrice)
            }));
        }

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 100f);
    }

    public static Map<Player, PlayerChat> getPlayerChat() {
        return playerChat;
    }

    public static class PlayerChat {

        private final Player player;
        private final ShopItem item;

        public PlayerChat(Player player, ShopItem item) {
            this.player = player;
            this.item = item;
        }

        public Player getPlayer() {
            return player;
        }

        public ShopItem getItem() {
            return item;
        }
    }
}