package com.zpedroo.bosses.mobai.mobs.zombie;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.mobai.managers.MobManager;
import com.zpedroo.bosses.utils.FileUtils;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ZombieAI {

    public static void normalAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        MobManager.moveTo(entity, target.getLocation(), Settings.MOVE_SPEED);
        target.damage(Settings.NORMAL_ATTACK_DAMAGE, entity);
    }

    public static void bloodRushAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        for (double j = 0.0; j <= 6.283185307179586; j += 0.19634954084936207) {
            for (int k = 0; k <= 1; ++k) {
                double x = 0.3 * (6.283185307179586 - j) * 0.5 * Math.cos(j + 0.19634954084936207 + k * 3.141592653589793);
                double y = 0.5 * j;
                double z = 0.3 * (6.283185307179586 - j) * 0.5 * Math.sin(j + 0.19634954084936207 + k * 3.141592653589793);
                Location location = entity.getLocation().clone().add(x, y, z);
                location.getWorld().playEffect(location, Effect.COLOURED_DUST, 1);
            }
        }

        MobManager.moveTo(entity, target.getLocation(), Settings.MOVE_SPEED);
        target.damage(Settings.BLOOD_RUSH_ATTACK_DAMAGE, entity);
    }

    public static void spawnMinions(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline() || entity.isDead()) return;

        for (int i = 0; i < new Random().nextInt(1) + 1; ++i) {
            Location location = entity.getLocation().clone().add(new Random().nextInt(6) - 3, 0D, new Random().nextInt(6) - 3);
            Zombie zombie = (Zombie) entity.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.setMetadata("Minion", new FixedMetadataValue(VoltzBosses.get(), true));
            zombie.setTarget(target);
            zombie.getEquipment().setItemInHand(null);
            ((CraftZombie) zombie).getHandle().setBaby(true);
            ((CraftZombie) zombie).getHandle().setVillager(false);

            EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) zombie).getHandle();
            nmsEntity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(Settings.MINION_SPEED);
            nmsEntity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(Settings.MINION_DAMAGE);
        }
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
        ZombieAttack attack = ZombieAttack.values()[new Random().nextInt(ZombieAttack.values().length)];
        switch (attack) {
            case BLOOD_RUSH_ATTACK: {
                bloodRushAttack(entity, target);
                break;
            }
            case MINION_SPAWN:
                spawnMinions(entity, target);
                break;
            default: {
                normalAttack(entity, target);
                break;
            }
        }
    }

    static class Settings {

        public static final long ATTACK_DELAY = FileUtils.get().getLong(FileUtils.Files.MOBS_AI, "Mobs.Zombie.attack-delay");

        public static final float SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Zombie.speed");

        public static final float MOVE_SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Zombie.move-speed");

        public static final double NORMAL_ATTACK_DAMAGE = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Zombie.normal-attack.damage");

        public static final double BLOOD_RUSH_ATTACK_DAMAGE = FileUtils.get().getDouble(FileUtils.Files.MOBS_AI, "Mobs.Zombie.blood-rush-attack.damage");

        public static final float MINION_SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Zombie.minion.speed");

        public static final double MINION_DAMAGE = FileUtils.get().getDouble(FileUtils.Files.MOBS_AI, "Mobs.Zombie.minion.damage");
    }
}