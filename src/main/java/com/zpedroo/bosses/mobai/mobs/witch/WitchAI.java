package com.zpedroo.bosses.mobai.mobs.witch;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.serialization.LocationSerialization;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WitchAI {

    public static void normalAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline()) return;

        new BukkitRunnable() {
            double i = 0D;
            final double r = 1D;
            int j = 0;

            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || entity.isDead()) {
                    this.cancel();
                    return;
                }

                if (++j < 20) {
                    if (this.i > 3D) this.i = 0D;

                    this.i += 0.19634954084936207;
                    double x = this.r * Math.cos(this.i);
                    double y = 0.5 * this.i;
                    double z = this.r * Math.sin(this.i);
                    Location loc1 = entity.getLocation().clone().add(x, y, z);
                    Location loc2 = entity.getLocation().clone().add(-x, y, -z);
                    Location loc3 = entity.getLocation().clone().add(-x, y, z);
                    Location loc4 = entity.getLocation().clone().add(x, y, -z);
                    entity.getWorld().playEffect(loc1, Effect.MOBSPAWNER_FLAMES, 1);
                    entity.getWorld().playEffect(loc2, Effect.MOBSPAWNER_FLAMES, 1);
                    entity.getWorld().playEffect(loc3, Effect.MOBSPAWNER_FLAMES, 1);
                    entity.getWorld().playEffect(loc4, Effect.MOBSPAWNER_FLAMES, 1);
                    return;
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 20 * 2, 0));
                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
    }

    public static void blackMagicAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline()) return;

        new BukkitRunnable() {
            double i = 0D;
            final double r = 1D;
            int j = 0;

            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || entity.isDead()) {
                    this.cancel();
                    return;
                }

                if (++j < 20) {
                    if (this.i > 3D) this.i = 0D;

                    this.i += 0.19634954084936207;
                    double x = this.r * Math.cos(this.i);
                    double y = 0.5 * this.i;
                    double z = this.r * Math.sin(this.i);
                    Location loc1 = entity.getLocation().clone().add(x, y, z);
                    Location loc2 = entity.getLocation().clone().add(-x, y, -z);
                    Location loc3 = entity.getLocation().clone().add(-x, y, z);
                    Location loc4 = entity.getLocation().clone().add(x, y, -z);
                    entity.getWorld().playEffect(loc1, Effect.PORTAL, 10);
                    entity.getWorld().playEffect(loc2, Effect.PORTAL, 10);
                    entity.getWorld().playEffect(loc3, Effect.PORTAL, 10);
                    entity.getWorld().playEffect(loc4, Effect.PORTAL, 10);
                    return;
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Settings.BLACK_MAGIC_ATTACK_DURATION * 20, 0));
                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
    }

    public static void fireAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline()) return;

        new BukkitRunnable() {
            double i = 0D;
            final double r = 1D;
            int j = 0;

            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || entity.isDead()) {
                    this.cancel();
                    return;
                }

                if (++j < 20) {
                    if (this.i > 3D) this.i = 0D;

                    this.i += 0.19634954084936207;
                    double x = this.r * Math.cos(this.i);
                    double y = 0.5 * this.i;
                    double z = this.r * Math.sin(this.i);
                    Location loc1 = entity.getLocation().clone().add(x, y, z);
                    Location loc2 = entity.getLocation().clone().add(-x, y, -z);
                    Location loc3 = entity.getLocation().clone().add(-x, y, z);
                    Location loc4 = entity.getLocation().clone().add(x, y, -z);
                    entity.getWorld().playEffect(loc1, Effect.SMOKE, 5);
                    entity.getWorld().playEffect(loc2, Effect.SMOKE, 5);
                    entity.getWorld().playEffect(loc3, Effect.SMOKE, 5);
                    entity.getWorld().playEffect(loc4, Effect.SMOKE, 5);
                    return;
                }

                target.setFireTicks(20 * Settings.FIRE_ATTACK_DURATION);
                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
    }

    public static void slownessAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline()) return;

        new BukkitRunnable() {
            double i = 0.39269908169872414;
            int j = 0;

            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || entity.isDead()) {
                    this.cancel();
                    return;
                }

                if (++j < 20) {
                    this.i += 0.3141592653589793;
                    for (double alpha = 0D; alpha <= 6.283185307179586; alpha += 0.39269908169872414) {
                        double x = this.i * Math.cos(alpha);
                        double y = 2.0 * Math.exp(-0.1 * this.i) * Math.sin(this.i) + 1.5;
                        double z = this.i * Math.sin(alpha);
                        Location loc1 = entity.getLocation().clone().add(x, y, z);
                        entity.getWorld().playEffect(loc1, Effect.SNOW_SHOVEL, 1);
                    }
                    return;
                }

                entity.removePotionEffect(PotionEffectType.SLOW);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Settings.SLOWNESS_ATTACK_DURATION * 20, 5));
                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
    }

    public static void poisonAttack(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline()) return;

        new BukkitRunnable() {
            double i = 0.39269908169872414;
            int j = 0;

            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || entity.isDead()) {
                    this.cancel();
                    return;
                }

                if (++j < 20) {
                    this.i += 0.3141592653589793;
                    for (double alpha = 0D; alpha <= 6.283185307179586; alpha += 0.09817477042468103) {
                        double x = this.i * Math.cos(alpha);
                        double y = 2.0 * Math.exp(-0.1 * this.i) * Math.sin(this.i) + 1.5;
                        double z = this.i * Math.sin(alpha);
                        Location loc1 = entity.getLocation().clone().add(x, y, z);
                        entity.getWorld().playEffect(loc1, Effect.VILLAGER_THUNDERCLOUD, 1);
                    }

                    entity.getNearbyEntities(5, 5, 5)
                            .stream().filter(nearbyEntity -> nearbyEntity != null && nearbyEntity.getType() == EntityType.PLAYER)
                            .forEach(nearbyTarget -> {
                                ((LivingEntity) nearbyTarget).addPotionEffect(
                                        new PotionEffect(PotionEffectType.POISON, Settings.POISON_ATTACK_DURATION * 20, 0)
                                );
                            });
                    return;
                }

                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
    }

    public static void spawnMinions(LivingEntity entity, Player target) {
        if (target == null || target.isDead() || !target.isOnline()) return;

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20000, 100, true));
        new BukkitRunnable() {
            double i = 0D;
            final double r = 1D;
            int j = 0;

            @Override
            public void run() {
                if (target.isDead() || !target.isOnline() || entity.isDead()) {
                    this.cancel();
                    return;
                }

                if (++j < 20) {
                    if (this.i > 3D) this.i = 0D;

                    this.i += 0.19634954084936207;
                    double x = this.r * Math.cos(this.i);
                    double y = 0.5 * this.i;
                    double z = this.r * Math.sin(this.i);
                    Location loc1 = entity.getLocation().clone().add(x, y, z);
                    Location loc2 = entity.getLocation().clone().add(-x, y, -z);
                    Location loc3 = entity.getLocation().clone().add(-x, y, z);
                    Location loc4 = entity.getLocation().clone().add(x, y, -z);
                    entity.getWorld().playEffect(loc1, Effect.CLOUD, 1);
                    entity.getWorld().playEffect(loc2, Effect.CLOUD, 1);
                    entity.getWorld().playEffect(loc3, Effect.CLOUD, 1);
                    entity.getWorld().playEffect(loc4, Effect.CLOUD, 1);
                    return;
                }

                entity.removePotionEffect(PotionEffectType.SLOW);
                for (int i = 0; i < new Random().nextInt(3) + 3; ++i) {
                    Location location = entity.getLocation().clone().add(new Random().nextInt(6) - 3, 0, new Random().nextInt(6) - 3);
                    Entity minion = entity.getWorld().spawnEntity(location, Settings.MINION_TYPE);
                    minion.setMetadata("Minion", new FixedMetadataValue(VoltzBosses.get(), true));

                    EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) minion).getHandle();
                    nmsEntity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(Settings.MINION_SPEED);
                    nmsEntity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(Settings.MINION_DAMAGE);
                }
                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
    }

    public static void healEffect(LivingEntity entity) {
        if (!entity.hasMetadata("Boss")) return;

        Location bossSpawnerLocation = LocationSerialization.deserialize(entity.getMetadata("Boss").get(0).asString());
        if (bossSpawnerLocation == null) return;

        BossSpawner bossSpawner = DataManager.getInstance().getBossSpawner(bossSpawnerLocation);
        if (bossSpawner == null) return;

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20000, 100, true));
        new BukkitRunnable() {
            double i = 0D;
            final double r = 1D;
            int j = 0;

            @Override
            public void run() {
                if (++j < 20) {
                    if (this.i > 3D) this.i = 0D;

                    this.i += 0.19634954084936207;
                    double x = this.r * Math.cos(this.i);
                    double y = 0.5 * this.i;
                    double z = this.r * Math.sin(this.i);
                    Location loc1 = entity.getLocation().clone().add(x, y, z);
                    Location loc2 = entity.getLocation().clone().add(-x, y, -z);
                    Location loc3 = entity.getLocation().clone().add(-x, y, z);
                    Location loc4 = entity.getLocation().clone().add(x, y, -z);
                    entity.getWorld().playEffect(loc1, Effect.HEART, 1);
                    entity.getWorld().playEffect(loc2, Effect.HEART, 1);
                    entity.getWorld().playEffect(loc3, Effect.HEART, 1);
                    entity.getWorld().playEffect(loc4, Effect.HEART, 1);

                    int health = bossSpawner.getBossHealth();
                    int toHeal = ThreadLocalRandom.current().nextInt(Settings.HEAL_MIN, Settings.HEAL_MAX);

                    int finalHealth = health + toHeal;
                    if (finalHealth > bossSpawner.getSpawnedBoss().getMaxHealth()) finalHealth = bossSpawner.getSpawnedBoss().getMaxHealth();

                    bossSpawner.damageBoss(null, finalHealth);
                    return;
                }

                entity.removePotionEffect(PotionEffectType.SLOW);
                this.cancel();
            }
        }.runTaskTimer(VoltzBosses.get(), 0L, 2L);
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
        WitchAttack attack = WitchAttack.values()[new Random().nextInt(WitchAttack.values().length)];
        switch (attack) {
            case SLOWNESS_ATTACK: {
                slownessAttack(entity, target);
                break;
            }
            case POISON_ATTACK: {
                poisonAttack(entity, target);
                break;
            }
            case FIRE_ATTACK:
                fireAttack(entity, target);
                break;
            case BLACK_MAGIC_ATTACK: {
                blackMagicAttack(entity, target);
                break;
            }
            case HEAL_EFFECT: {
                Location bossSpawnerLocation = LocationSerialization.deserialize(entity.getMetadata("Boss").get(0).asString());
                if (bossSpawnerLocation == null) break;

                BossSpawner bossSpawner = DataManager.getInstance().getBossSpawner(bossSpawnerLocation);
                if (bossSpawner == null) break;
                if (bossSpawner.getBossHealth() < bossSpawner.getSpawnedBoss().getMaxHealth()) {
                    healEffect(target);
                    break;
                }

                normalAttack(entity, target);
                break;
            }
            case MINION_SPAWN: {
                spawnMinions(entity, target);
                break;
            }
            default: {
                normalAttack(entity, target);
                break;
            }
        }
    }

    static class Settings {

        public static final long ATTACK_DELAY = FileUtils.get().getLong(FileUtils.Files.MOBS_AI, "Mobs.Witch.attack-delay");

        public static final float SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Witch.speed");

        public static final int HEAL_MIN = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.heal.min");

        public static final int HEAL_MAX = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.heal.max");

        public static final int BLACK_MAGIC_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.black-magic-attack.effect-duration");

        public static final int FIRE_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.fire-attack.effect-duration");

        public static final int FIRE_CIRCLE_ATTACK_RADIUS = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.fire-circle-attack.radius");

        public static final int POISON_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.poison-attack.effect-duration");

        public static final int SLOWNESS_ATTACK_DURATION = FileUtils.get().getInt(FileUtils.Files.MOBS_AI, "Mobs.Witch.slowness-attack.effect-duration");

        public static final EntityType MINION_TYPE = EntityType.valueOf(FileUtils.get().getString(FileUtils.Files.MOBS_AI, "Mobs.Witch.minion.type"));

        public static final float MINION_SPEED = FileUtils.get().getFloat(FileUtils.Files.MOBS_AI, "Mobs.Witch.minion.speed");

        public static final double MINION_DAMAGE = FileUtils.get().getDouble(FileUtils.Files.MOBS_AI, "Mobs.Witch.minion.damage");
    }
}