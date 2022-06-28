package com.zpedroo.bosses.listeners;

import com.zpedroo.bosses.objects.general.Drop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class DropListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickupDrop(PlayerPickupItemEvent event) {
        if (!event.getItem().hasMetadata("BossDrop")) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        Item item = event.getItem();
        Drop drop = (Drop) item.getMetadata("BossDrop").get(0).value();
        if (drop == null || player.getInventory().firstEmpty() == -1) return;

        item.remove();

        ItemStack itemToGive = drop.getItemToGive();
        if (itemToGive != null) {
            player.getInventory().addItem(itemToGive);
        }

        for (String command : drop.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(command, new String[]{
                    "{player}"
            }, new String[]{
                    player.getName()
            }));
        }

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1f, 1f);
    }
}