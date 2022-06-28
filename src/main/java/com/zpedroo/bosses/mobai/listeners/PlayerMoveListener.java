package com.zpedroo.bosses.mobai.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Player player = event.getPlayer();

        for (Entity entity : player.getNearbyEntities(4D, 4D, 4D)) {
            if (!entity.hasMetadata("Boss")) return;

            double distance2D = Math.sqrt(Math.pow(player.getLocation().getX() - entity.getLocation().getX(), 2D) +
                    Math.pow(player.getLocation().getZ() - entity.getLocation().getZ(), 2D));
            if (entity.getLocation().getWorld() != player.getWorld()) continue;

            Vector vector = entity.getLocation().subtract(player.getLocation()).toVector().setY(-1D);
            vector = vector.multiply(0.03D * Math.sqrt(Math.abs(10D - distance2D)));
            vector.setY(-2D);

            if ((entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WITCH) &&
                    !((LivingEntity) entity).hasPotionEffect(PotionEffectType.SLOW)) {
                entity.setVelocity(vector);
            }
        }
    }
}