package com.zpedroo.bosses.mobai.mobs.guardian;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.mobai.managers.MobManager;
import com.zpedroo.bosses.utils.FileUtils;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftGuardian;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GuardianAI {

    public static void normalAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        MobManager.moveTo(entity, target.getLocation(), Settings.MOVE_SPEED);
        ((CraftGuardian) entity).getHandle().a(((CraftPlayer) target).getHandle());
    }

    public static void instantLaserAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        ((CraftGuardian) entity).getHandle().a(((CraftPlayer) target).getHandle(), 0f, 0f);
    }

    public static void suffocationAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        MobManager.moveTo(entity, target.getLocation(), Settings.MOVE_SPEED);
        target.setRemainingAir(0);
    }

    public static void nailingAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        MobManager.moveTo(entity, target.getLocation(), Settings.MOVE_SPEED);
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * Settings.NAILING_ATTACK_DURATION, 1));
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
        GuardianAttack attack = GuardianAttack.values()[new Random().nextInt(GuardianAttack.values().length)];
        switch (attack) {
            case INSTANT_LASER_ATTACK: {
                instantLaserAttack(entity, target);
                break;
            }
            case SUFFOCATION_ATTACK:
                suffocationAttack(entity, target);
                break;
            case NAILING_ATTACK: {
                nailingAttack(entity, target);
                break;
            }
            default: {
                normalAttack(entity, target);
                break;
            }
        }
    }

    static class Settings {

        public static final float SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Guardian.speed");

        public static final float MOVE_SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Guardian.move-speed");

        public static final long ATTACK_DELAY = FileUtils.get().getLong(FileUtils.Files.MOBS_AI, "Mobs.Guardian.attack-delay");

        public static final int NAILING_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Guardian.nailing-attack.effect-duration");
    }
}