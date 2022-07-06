package com.zpedroo.bosses.listeners;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.bosskiller.BossKillerEnchant;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.config.Items;
import com.zpedroo.bosses.utils.menu.Menus;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        DataManager.getInstance().savePlayerData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand().clone();
        if (item == null || item.getType().equals(Material.AIR)) return;

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("BossSpawner")) return;

        event.setCancelled(true);

        Block block = event.getBlock();
        Location location = block.getLocation().clone().add(0D, 1D, 0D);

        BossSpawner bossSpawner = new BossSpawner(location);
        bossSpawner.cache();

        Player player = event.getPlayer();

        item.setAmount(1);
        player.getInventory().removeItem(item);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal)) return;

        BossSpawner bossSpawner = DataManager.getInstance().getBossSpawner(event.getEntity().getLocation());
        if (bossSpawner == null) return;

        event.setCancelled(true);

        Player player = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;
        if (player == null || !player.isSneaking() || !player.hasPermission("bosses.admin")) return;

        bossSpawner.delete();
        player.getInventory().addItem(Items.getBossSpawnerItem());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!BossKillerUtils.isBossKiller(event.getItem())) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Menus.getInstance().openUpgradeMenu(player, item);
    }
}