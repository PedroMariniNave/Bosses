package com.zpedroo.bosses.objects.spawner;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.enums.EnchantProperty;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.Drop;
import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.general.TopDamageSettings;
import com.zpedroo.bosses.tasks.BossSpawnerTask;
import com.zpedroo.bosses.enums.Enchants;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.config.Messages;
import com.zpedroo.bosses.utils.config.Settings;
import com.zpedroo.bosses.utils.config.Titles;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.formatter.TimeFormatter;
import com.zpedroo.bosses.utils.offlineapi.OfflinePlayerAPI;
import com.zpedroo.bosses.utils.serialization.LocationSerialization;
import net.minecraft.server.v1_8_R3.EntityWolf;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.zpedroo.bosses.utils.config.Settings.NULL_DAMAGER;

public class BossSpawner {

    private final Location location;
    private final Entity spawnerCrystal;
    private final BossSpawnerHologram spawnerHologram = new BossSpawnerHologram(this);;
    private final BossSpawnerTask spawnerTask = new BossSpawnerTask(this);
    private final Map<UUID, Integer> damagers = new HashMap<>(8);
    private Boss spawnedBoss;
    private Entity bossEntity;

    public BossSpawner(Location location) {
        this.location = location;
        this.loadBossSpawnerChunk();
        this.spawnerCrystal = location.getWorld().spawn(location, EnderCrystal.class);
        this.spawnerCrystal.setMetadata("***", new FixedMetadataValue(VoltzBosses.get(), true));
    }

    public Location getLocation() {
        return location;
    }

    public Entity getBossEntity() {
        return bossEntity;
    }

    public BossSpawnerHologram getSpawnerHologram() {
        return spawnerHologram;
    }

    public Entity getSpawnerCrystal() {
        return spawnerCrystal;
    }

    public Boss getSpawnedBoss() {
        return spawnedBoss;
    }

    public void spawnBoss() {
        if (bossEntity != null && !bossEntity.isDead()) return;

        damagers.clear();
        Random random = new Random();
        double xExtra =  random.nextInt(5) + 0.5D;
        double zExtra =  random.nextInt(5) + 0.5D;

        Location spawnLocation = location.clone().add(xExtra, 0, zExtra);

        List<Boss> bosses = DataManager.getInstance().getCache().getBosses();
        spawnedBoss = bosses.get(new Random().nextInt(bosses.size())); // random boss
        loadBossSpawnerChunk();
        String entityType = spawnedBoss.getEntityType();
        setCustomMob(spawnLocation, entityType);
        setBossMetadata();

        LivingEntity livingEntity = (LivingEntity) bossEntity;
        livingEntity.setRemoveWhenFarAway(false);

        setBossEquipments(livingEntity);

        switch (bossEntity.getType()) {
            case ZOMBIE:
                ((Zombie) bossEntity).setVillager(false);
                ((Zombie) bossEntity).setBaby(false);
                break;
            case WOLF:
                ((Wolf) bossEntity).setAngry(true);
                ((Wolf) bossEntity).setCollarColor(DyeColor.ORANGE);
                break;
        }

        spawnedBoss.getSpawnMessage().forEach(Bukkit::broadcastMessage);
        DataManager.getInstance().getCache().setLastActiveBossSpawner(this);
    }

    public void killBoss() {
        if (bossEntity == null) return;

        for (String msg : spawnedBoss.getKillMessage()) {
            Bukkit.broadcastMessage(StringUtils.replaceEach(msg, new String[]{
                    "{damagers}"
            }, new String[]{
                    getListedDamagers()
            }));
        }

        giveTopDamagersRewards();
        dropBossItems();

        bossEntity.remove();

        for (int position = 1; position < spawnedBoss.getTopDamagersAmount(); ++position) {
            if (position > damagers.size()) break;

            UUID uuid = (UUID) damagers.keySet().toArray()[position-1];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            Player player = OfflinePlayerAPI.getPlayer(offlinePlayer.getName());
            if (player == null) continue;

            PlayerData killerData = DataManager.getInstance().getPlayerData(player);
            killerData.addKilledBossesAmount(1);
        }
    }

    public void damageBoss(@Nullable Player damager, int damage) {
        if (bossEntity == null || bossEntity.isDead()) return;

        if (damager != null) {
            int actualDamage = damagers.getOrDefault(damager.getUniqueId(), 0);
            int newPlayerDamage = Math.min(actualDamage + damage, spawnedBoss.getMaxHealth());
            damagers.put(damager.getUniqueId(), newPlayerDamage);

            for (String command : spawnedBoss.getHitCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(command, new String[]{
                        "{player}",
                        "{damage}"
                }, new String[]{
                        damager.getName(),
                        String.valueOf(damage)
                }));
            }

            ItemStack item = damager.getItemInHand();
            if (item != null) {
                giveItemExp(damager, damage, item);

                damage = handleCritDamageEnchant(damage, item);
                handleAreaDamageEnchant(damage, item);
            }
        }

        int newBossHealth = getBossHealth() - damage;
        setBossHealth(newBossHealth);
    }

    public void setBossHealth(int health) {
        if (bossEntity == null || bossEntity.isDead()) return;

        bossEntity.setMetadata("BossHealth", new FixedMetadataValue(VoltzBosses.get(), health));
        if (health <= 0) {
            killBoss();
        }
    }

    private String getListedDamagers() {
        StringBuilder builder = new StringBuilder();

        int topDamagersAmount = spawnedBoss.getTopDamagersAmount();
        List<Map.Entry<UUID, Integer>> topDamagers = getTopDamagers(topDamagersAmount);

        for (int position = 1; position <= topDamagersAmount; ++position) {
            TopDamageSettings topDamageSettings = spawnedBoss.getTopDamageSettings().get(position);
            if (topDamageSettings == null) continue;
            if (builder.length() > 0) builder.append("\n");

            Map.Entry<UUID, Integer> entry = topDamagers.size() >= position ? topDamagers.get(position-1) : null;
            UUID uuid = entry == null ? null : entry.getKey();
            int damageAmount = entry == null ? 0 : entry.getValue();

            OfflinePlayer player = uuid == null ? Bukkit.getOfflinePlayer(NULL_DAMAGER) : Bukkit.getOfflinePlayer(uuid);

            builder.append(StringUtils.replaceEach(topDamageSettings.getDisplay(), new String[]{
                    "{position}",
                    "{player}",
                    "{damage}"
            }, new String[]{
                    String.valueOf(position),
                    player.getName(),
                    NumberFormatter.getInstance().formatThousand(damageAmount)
            }));
        }

        return builder.toString();
    }

    private void giveTopDamagersRewards() {
        int topDamagersAmount = spawnedBoss.getTopDamagersAmount();
        List<Map.Entry<UUID, Integer>> topDamagers = getTopDamagers(topDamagersAmount);

        for (int position = 1; position <= topDamagersAmount; ++position) {
            TopDamageSettings topDamageSettings = spawnedBoss.getTopDamageSettings().get(position);
            if (topDamageSettings == null) continue;
            if (position > topDamagers.size()) break;

            Map.Entry<UUID, Integer> entry = topDamagers.get(position-1);
            UUID uuid = entry.getKey();

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            for (String command : topDamageSettings.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(command, new String[]{
                        "{player}"
                }, new String[]{
                        player.getName()
                }));
            }
        }
    }

    private void dropBossItems() {
        for (Drop drop : spawnedBoss.getDrops()) {
            double randomNumber = ThreadLocalRandom.current().nextDouble(0, 100);
            if (randomNumber > drop.getChance()) continue;

            dropBossItem(drop);
        }
    }

    private void dropBossItem(Drop drop) {
        Item item = bossEntity.getWorld().dropItem(bossEntity.getLocation(), drop.getDisplayItem());
        item.setMetadata("BossDrop", new FixedMetadataValue(VoltzBosses.get(), drop));
        item.setMetadata("***", new FixedMetadataValue(VoltzBosses.get(), true));
        if (drop.getDisplayName() != null) {
            item.setCustomName(drop.getDisplayName());
            item.setCustomNameVisible(true);
        }
    }

    private List<Map.Entry<UUID, Integer>> getTopDamagers(int topLimit) {
        return damagers.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(topLimit).collect(Collectors.toList());
    }

    public String getBossBarDisplay() {
        if (bossEntity != null && !bossEntity.isDead()) {
            return StringUtils.replaceEach(spawnedBoss.getBossBarDisplay(), new String[]{
                    "{health}"
            }, new String[]{
                    NumberFormatter.getInstance().formatThousand(getBossHealth())
            });
        }

        return Messages.NO_BOSS;
    }

    public float getBossBarPercentage() {
        if (bossEntity == null || bossEntity.isDead()) return 0.01f;

        return (float) getBossHealth() / spawnedBoss.getMaxHealth() <= 0 ? (float) 0.01 : (float) getBossHealth() / spawnedBoss.getMaxHealth() * 100;
    }

    public void updateTarget() {
        if (bossEntity == null || bossEntity.isDead()) return;

        Optional<Entity> nearbyEntities = bossEntity.getNearbyEntities(Settings.TARGET_RADIUS, Settings.TARGET_RADIUS, Settings.TARGET_RADIUS)
                .stream().filter(entity -> entity != null && !entity.hasMetadata("vanished") && entity instanceof Player &&
                        ((Player) entity).getGameMode().equals(GameMode.SURVIVAL)).collect(Collectors.toList()).stream().findAny();
        if (!nearbyEntities.isPresent()) return;

        LivingEntity selectedEntity = (LivingEntity) nearbyEntities.get();
        if (selectedEntity == null || selectedEntity.isDead()) return;

        if (bossEntity instanceof Creature) {
            Creature creature = (Creature) bossEntity;
            if (creature.getTarget() != null && creature.getTarget().getLocation().distance(creature.getLocation()) < 10) return;

            creature.setTarget(selectedEntity);
            Bukkit.getPluginManager().callEvent(new EntityTargetEvent(bossEntity, selectedEntity, EntityTargetEvent.TargetReason.CUSTOM));
            return;
        }

        if (bossEntity instanceof EntityWolf) {
            Wolf wolf = (Wolf) bossEntity;
            if (wolf.getTarget() != null && wolf.getTarget().getLocation().distance(wolf.getLocation()) < 10) return;

            wolf.setTarget(selectedEntity);
            wolf.setAngry(true);
            Bukkit.getPluginManager().callEvent(new EntityTargetEvent(bossEntity, selectedEntity, EntityTargetEvent.TargetReason.CUSTOM));
//          return;
        }
    }

    public int getBossHealth() {
        if (bossEntity == null || bossEntity.isDead() || !bossEntity.hasMetadata("BossHealth")) return 0;

        return bossEntity.getMetadata("BossHealth").get(0).asInt();
    }

    public String replace(String str) {
        return StringUtils.replaceEach(str, new String[]{
                "{timer}"
        }, new String[]{
                TimeFormatter.format(spawnerTask.getNextSpawnInMillis() - System.currentTimeMillis())
        });
    }

    private void handleAreaDamageEnchant(int damage, ItemStack item) {
        Enchant areaDamageEnchant = Enchants.AREA_DAMAGE.get();
        EnchantProperty chanceProperty = EnchantProperty.CHANCE;
        double areaDamageChance = BossKillerUtils.getEnchantEffectByItem(item, areaDamageEnchant, chanceProperty);
        double randomNumber = ThreadLocalRandom.current().nextDouble(0, 100);
        if (randomNumber < areaDamageChance) {
            int radius = (int) BossKillerUtils.getEnchantEffectByItem(item, areaDamageEnchant, EnchantProperty.RADIUS);
            for (Entity entity : bossEntity.getNearbyEntities(radius, radius, radius)) {
                if (!(entity instanceof LivingEntity) || !entity.hasMetadata("Minion")) continue;

                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(damage);
            }
        }
    }

    private int handleCritDamageEnchant(int damage, ItemStack item) {
        Enchant critDamageEnchant = Enchants.CRIT_DAMAGE.get();
        EnchantProperty chanceProperty = EnchantProperty.CHANCE;
        double critDamageChance = BossKillerUtils.getEnchantEffectByItem(item, critDamageEnchant, chanceProperty);
        double randomNumber = ThreadLocalRandom.current().nextDouble(0, 100);
        if (randomNumber < critDamageChance) {
            damage *= critDamageEnchant.getPropertyEffect(EnchantProperty.CHANCE).doubleValue();
        }

        return damage;
    }

    private void setBossMetadata() {
        bossEntity.setMetadata("Boss", new FixedMetadataValue(VoltzBosses.get(), LocationSerialization.serialize(location)));
        bossEntity.setMetadata("BossHealth", new FixedMetadataValue(VoltzBosses.get(), spawnedBoss.getMaxHealth()));
        bossEntity.setMetadata("***", new FixedMetadataValue(VoltzBosses.get(), true));
    }

    private void setBossEquipments(LivingEntity livingEntity) {
        if (spawnedBoss.getItemInHand() != null) livingEntity.getEquipment().setItemInHand(spawnedBoss.getItemInHand());
        if (spawnedBoss.getArmorContents() != null) livingEntity.getEquipment().setArmorContents(spawnedBoss.getArmorContents());
    }

    private void setCustomMob(Location spawnLocation, String entityType) {
        switch (entityType) {
            case "ELDER_GUARDIAN":
                this.bossEntity = location.getWorld().spawnEntity(spawnLocation, EntityType.GUARDIAN);
                ((Guardian) bossEntity).setElder(true);
                break;
            case "WITHER_SKELETON":
                this.bossEntity = location.getWorld().spawnEntity(spawnLocation, EntityType.SKELETON);
                ((Skeleton) bossEntity).setSkeletonType(Skeleton.SkeletonType.WITHER);
                break;
            default:
                this.bossEntity = location.getWorld().spawnEntity(spawnLocation, EntityType.valueOf(entityType));
                break;
        }
    }

    private void giveItemExp(Player damager, int damage, ItemStack item) {
        final int oldLevel = BossKillerUtils.getItemLevel(item);
        double bonus = 1 + BossKillerUtils.getEnchantEffectByItem(item, Enchants.EXP.get(), EnchantProperty.MULTIPLIER);
        double experienceToAdd = spawnedBoss.getBossKillerXpPerHit() * damage * bonus;
        ItemStack newItem = BossKillerUtils.addItemExperience(item, experienceToAdd);
        damager.setItemInHand(newItem);

        int newLevel = BossKillerUtils.getItemLevel(newItem);
        if (isNewLevel(oldLevel, newLevel)) {
            sendUpgradeTitle(damager, oldLevel, newLevel);
        }
    }

    private boolean isNewLevel(int oldLevel, int newLevel) {
        return oldLevel != newLevel;
    }

    private void sendUpgradeTitle(Player damager, int oldLevel, int newLevel) {
        String[] placeholders = new String[]{ "{old_level}", "{new_level}" };
        String[] replacers = new String[]{
                NumberFormatter.getInstance().formatThousand(oldLevel), NumberFormatter.getInstance().formatThousand(newLevel)
        };
        damager.sendTitle(
                StringUtils.replaceEach(Titles.BOSS_KILLER_UPGRADE[0], placeholders, replacers),
                StringUtils.replaceEach(Titles.BOSS_KILLER_UPGRADE[1], placeholders, replacers)
        );
    }

    public void delete() {
        DataManager.getInstance().getCache().getBossSpawners().remove(location);
        DataManager.getInstance().getCache().getDeletedBossSpawners().add(location);

        spawnerHologram.removeHologram();
        spawnerCrystal.remove();
        spawnerTask.cancel();
        spawnerHologram.cancel();
        killBoss();
    }

    public void cache() {
        DataManager.getInstance().getCache().getBossSpawners().put(location, this);
    }

    private void loadBossSpawnerChunk() {
        location.getBlock().getChunk().load(true);
    }
}