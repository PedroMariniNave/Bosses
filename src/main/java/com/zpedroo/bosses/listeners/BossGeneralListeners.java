package com.zpedroo.bosses.listeners;

import com.zpedroo.bosses.enums.EnchantProperty;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.enums.Enchants;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.config.Messages;
import com.zpedroo.bosses.utils.serialization.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.Sound;
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

        if (!(event.getDamager() instanceof Player)) return;

        Location bossSpawnerLocation = LocationSerialization.deserialize(event.getEntity().getMetadata("Boss").get(0).asString());
        if (bossSpawnerLocation == null) return;

        BossSpawner bossSpawner = DataManager.getInstance().getBossSpawner(bossSpawnerLocation);
        if (bossSpawner == null) return;

        Player player = (Player) event.getDamager();
        ItemStack item = player.getItemInHand();
        if (!BossKillerUtils.isBossKiller(item)) {
            player.sendMessage(Messages.NEED_BOSS_KILLER_ITEM);
            return;
        }

        int damage = (int) BossKillerUtils.getEnchantEffectByItem(item, Enchants.DAMAGE.get(), EnchantProperty.DAMAGE);
        if (damage <= 0) return;

        bossSpawner.damageBoss(player, damage);
        bossSpawner.updateTarget();

        Entity entity = bossSpawner.getBossEntity();
        entity.getWorld().playSound(entity.getLocation(), Sound.ANVIL_BREAK, 10f, 10f);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMinionDamage(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata("Minion")) return;

        double damage = event.getDamage();

        // todo changes

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity.getHealth() - damage <= 0) {
            event.setDamage(0);
            entity.remove();
            return;
        }

        event.setDamage(damage);
    }
}