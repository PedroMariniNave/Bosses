package com.zpedroo.bosses.mobai.mobs.blaze;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.mobai.managers.MobManager;
import com.zpedroo.bosses.mobai.mobs.zombie.ZombieAI;
import com.zpedroo.bosses.utils.FileUtils;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BlazeAI {

    public static void normalAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        MobManager.moveTo(entity, target.getLocation(), Settings.MOVE_SPEED);
        target.damage(Settings.NORMAL_ATTACK_DAMAGE, entity);
    }

    public static void fireAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        target.setFireTicks(20 * Settings.FIRE_ATTACK_DURATION);
        entity.setFireTicks(20 * Settings.FIRE_ATTACK_DURATION);
    }

    public static void smokeAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * Settings.SMOKE_ATTACK_DURATION, 5));
    }

    public static void trackAndKill(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead() || target.hasMetadata("vanished")) return;

        EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) entity).getHandle();
        nmsEntity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(Settings.SPEED);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || target.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.SPECTATOR
                        || entity.isDead() || target.getWorld() != entity.getWorld() || target.getLocation().distance(entity.getLocation()) > 15 || target.hasMetadata("vanished")) {
                    this.cancel();
                    return;
                }

                randomAttack(entity, target);
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 20 * Settings.ATTACK_DELAY);
    }

    private static void randomAttack(LivingEntity entity, Player target) {
        BlazeAttack attack = BlazeAttack.values()[new Random().nextInt(BlazeAttack.values().length)];
        switch (attack) {
            case FIRE_ATTACK: {
                fireAttack(entity, target);
                break;
            }
            case SMOKE_ATTACK:
                smokeAttack(entity, target);
                break;
            default: {
                normalAttack(entity, target);
                break;
            }
        }
    }

    static class Settings {

        public static final float SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Blaze.speed");

        public static final float MOVE_SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Blaze.move-speed");

        public static final long ATTACK_DELAY = FileUtils.get().getLong(FileUtils.Files.MOBS_AI, "Mobs.Blaze.attack-delay");

        public static final long NORMAL_ATTACK_DAMAGE = FileUtils.get().getLong(FileUtils.Files.MOBS_AI, "Mobs.Blaze.normal-attack.damage");

        public static final int FIRE_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Blaze.fire-attack.duration");

        public static final int SMOKE_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Blaze.smoke-attack.effect-duration");
    }
}