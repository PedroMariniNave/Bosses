package com.zpedroo.bosses.mobai.managers;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class MobManager {

    public static void moveTo(LivingEntity entity, Location location, float speed) {
        Vector pos = entity.getLocation().toVector();
        Vector target = location.toVector();
        Vector velocity = target.subtract(pos);

        entity.setVelocity(velocity.normalize().multiply(speed));
    }
}