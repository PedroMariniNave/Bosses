package com.zpedroo.bosses.mobai.mobs.skeleton;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.mobai.mobs.zombie.ZombieAI;
import com.zpedroo.bosses.utils.FileUtils;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class SkeletonAI {

    public static void normalAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        shoot(entity, target, "NormalArrow", "Boss");
    }

    public static void arrowRainAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        for (int i = 0; i < 5; ++i) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    shoot(entity, target, "NormalArrow", "Boss");
                }
            }.runTaskLater(VoltzBosses.get(), i*2);
        }
    }

    public static void poisonedArrowAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        shoot(entity, target, "PoisonedArrow", "Boss");
    }

    public static void slownessArrowAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        shoot(entity, target, "SlownessArrow", "Boss");
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
        SkeletonAttack attack = SkeletonAttack.values()[new Random().nextInt(SkeletonAttack.values().length)];
        switch (attack) {
            case ARROW_RAIN: {
                arrowRainAttack(entity, target);
                break;
            }
            case POISONED_ARROW: {
                poisonedArrowAttack(entity, target);
                break;
            }
            case SLOWNESS_ARROW:
                slownessArrowAttack(entity, target);
                break;
            default: {
                normalAttack(entity, target);
                break;
            }
        }
    }

    private static void shoot(LivingEntity shooter, LivingEntity target, String... metadata) {
        Vector vector = target.getLocation().subtract(shooter.getEyeLocation()).toVector().normalize().multiply(1.5);

        Arrow originalArrow = (Arrow) shooter.getWorld().spawnEntity(shooter.getEyeLocation(), EntityType.ARROW);
        originalArrow.setShooter(shooter);
        originalArrow.setVelocity(vector);
        originalArrow.setFireTicks(Integer.MAX_VALUE);

        for (String metadataName : metadata) {
            originalArrow.setMetadata(metadataName, new FixedMetadataValue(VoltzBosses.get(), true));
        }

        Vector xVec = vector;
        Vector zVec = vector;

        int toShoot = Settings.ARROW_AMOUNT;

        int shotsAmount = 0;
        while (shotsAmount < toShoot/2) {
            xVec = new Vector(xVec.getX() - (xVec.getZ() / toShoot), xVec.getY(), xVec.getZ() + (xVec.getX() / toShoot));
            zVec = new Vector(zVec.getX() + (zVec.getZ() / toShoot), zVec.getY(), zVec.getZ() - (zVec.getX() / toShoot));

            Arrow firstExtraArrow = (Arrow) shooter.getWorld().spawnEntity(shooter.getEyeLocation(), EntityType.ARROW);
            firstExtraArrow.setShooter(shooter);
            firstExtraArrow.setVelocity(xVec);
            firstExtraArrow.setFireTicks(Integer.MAX_VALUE);

            Arrow secondExtraArrow = (Arrow) shooter.getWorld().spawnEntity(shooter.getEyeLocation(), EntityType.ARROW);
            secondExtraArrow.setShooter(shooter);
            secondExtraArrow.setVelocity(zVec);
            secondExtraArrow.setFireTicks(Integer.MAX_VALUE);

            for (String metadataName : metadata) {
                firstExtraArrow.setMetadata(metadataName, new FixedMetadataValue(VoltzBosses.get(), true));
                secondExtraArrow.setMetadata(metadataName, new FixedMetadataValue(VoltzBosses.get(), true));
            }
            ++shotsAmount;
        }
    }

    public static class Settings {

        public static final long ATTACK_DELAY = FileUtils.get().getLong(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.attack-delay");

        public static final float SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.speed");

        public static final int ARROW_AMOUNT = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.arrow-amount");

        public static final double NORMAL_ARROW_DAMAGE = FileUtils.get().getDouble(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.normal-arrow.damage");

        public static final double POISON_ARROW_DAMAGE = FileUtils.get().getDouble(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.poison-arrow.damage");

        public static final int POISON_ARROW_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.poison-arrow.effect-duration");

        public static final double SLOWNESS_ARROW_DAMAGE = FileUtils.get().getDouble(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.slowness-arrow.damage");

        public static final int SLOWNESS_ARROW_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Skeleton.slowness-arrow.effect-duration");
    }
}