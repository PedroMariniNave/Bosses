package com.zpedroo.bosses.listeners;

import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PointsListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSwap(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
        if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) return;

        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;

        ItemStack cursor = event.getCursor();

        NBTItem cursorNBT = new NBTItem(cursor);
        if (!cursorNBT.hasKey(BossKillerUtils.POINTS_NBT)) return;

        ItemStack item = event.getCurrentItem().clone();
        if (!BossKillerUtils.isBossKiller(item)) return;

        event.setCancelled(true);

        cursor.setAmount(cursor.getAmount() - 1);
        event.setCursor(cursor);

        int pointsAmount = cursorNBT.getInteger(BossKillerUtils.POINTS_NBT);
        ItemStack newItem = BossKillerUtils.addItemPoints(item, pointsAmount);

        player.getInventory().setItem(event.getSlot(), newItem);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 10f);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getItem();
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasKey(BossKillerUtils.POINTS_NBT)) event.setCancelled(true);
    }
}