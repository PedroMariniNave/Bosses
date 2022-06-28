package com.zpedroo.bosses.mobai.listeners;

import com.zpedroo.bosses.mobai.mobs.blaze.BlazeAI;
import com.zpedroo.bosses.mobai.mobs.guardian.GuardianAI;
import com.zpedroo.bosses.mobai.mobs.skeleton.SkeletonAI;
import com.zpedroo.bosses.mobai.mobs.witch.WitchAI;
import com.zpedroo.bosses.mobai.mobs.zombie.ZombieAI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityTargetListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() == null || event.getTarget() == null) return;
        if (!event.getEntity().hasMetadata("Boss") || !(event.getTarget() instanceof Player)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        Player target = (Player) event.getTarget();

        switch (entity.getType()) {
            case BLAZE:
                BlazeAI.trackAndKill(entity, target);
                break;
            case GUARDIAN:
                GuardianAI.trackAndKill(entity, target);
                break;
            case SKELETON:
                SkeletonAI.trackAndKill(entity, target);
                break;
            case WITCH:
                WitchAI.trackAndKill(entity, target);
                break;
            case ZOMBIE:
                ZombieAI.trackAndKill(entity, target);
                break;
        }
    }
}