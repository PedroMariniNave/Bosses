package com.zpedroo.bosses.mobai.listeners;

import com.zpedroo.bosses.mobai.mobs.skeleton.SkeletonAI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Entity damager = event.getDamager();

        switch (damager.getType()) {
            case ARROW:
                if (damager.hasMetadata("NormalArrow")) {
                    event.setDamage(SkeletonAI.Settings.NORMAL_ARROW_DAMAGE);
                }
                else if (damager.hasMetadata("PoisonedArrow")) {
                    event.setDamage(SkeletonAI.Settings.POISON_ARROW_DAMAGE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * SkeletonAI.Settings.POISON_ARROW_DURATION, 0));
                }
                else if (damager.hasMetadata("SlownessArrow")) {
                    event.setDamage(SkeletonAI.Settings.SLOWNESS_ARROW_DAMAGE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * SkeletonAI.Settings.SLOWNESS_ARROW_DURATION, 3));
                }
                break;
        }
    }
}