package com.zpedroo.bosses.listeners;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.serialization.LocationSerialization;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class BossGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBossDamage(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata("Boss")) return;

        event.setCancelled(true);

        Location bossSpawnerLocation = LocationSerialization.deserialize(event.getEntity().getMetadata("Boss").get(0).asString());
        if (bossSpawnerLocation == null) return;

        BossSpawner bossSpawner = DataManager.getInstance().getBossSpawner(bossSpawnerLocation);
        if (bossSpawner == null) return;

        Player player = null;
        double damage = event.getDamage();

        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (!(arrow.getShooter() instanceof Player)) return;

            player = (Player) arrow.getShooter();
            if (arrow.hasMetadata("BossKillerArrowDamage")) damage = arrow.getMetadata("BossKillerArrowDamage").get(0).asDouble();

            arrow.remove();
        } else if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)) return;

            ItemStack item = player.getItemInHand().clone();
//            if (item.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) > 0) return;

            NBTItem nbt = new NBTItem(item);
            if (nbt.hasKey("BossKillerDamage")) damage = nbt.getDouble("BossKillerDamage");
        }

        Entity entity = bossSpawner.getBossEntity();
        entity.getWorld().playSound(entity.getLocation(), Sound.ANVIL_BREAK, 0.2f, 10f);
        bossSpawner.damageBoss(player, (int) damage);
        bossSpawner.updateTarget();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMinionDamage(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata("Minion")) return;

        double damage = event.getDamage();

        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (!(arrow.getShooter() instanceof Player)) return;

            if (arrow.hasMetadata("BossKillerArrowDamage")) damage = arrow.getMetadata("BossKillerArrowDamage").get(0).asDouble();

            arrow.remove();
        } else if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)) return;

            ItemStack item = player.getItemInHand().clone();
//            if (item.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) > 0) return;

            NBTItem nbt = new NBTItem(item);
            if (nbt.hasKey("BossKillerDamage")) damage = nbt.getDouble("BossKillerDamage");
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity.getHealth() - damage <= 0) {
            event.setDamage(0);
            entity.remove();
            return;
        }

        event.setDamage(damage);
    }
}