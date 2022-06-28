package com.zpedroo.bosses.managers;

import com.zpedroo.bosses.objects.general.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class BossSpawnerManager {

    private static BossSpawnerManager instance;
    public static BossSpawnerManager getInstance() {
        return instance;
    }

    public BossSpawnerManager() {
        instance = this;
    }

    public void clearAll() {
        removeAllBossSpawners();
        removeAllBossBars();
        removeAllMinions();
    }

    private void removeAllBossSpawners() {
        DataManager.getInstance().getCache().getBossSpawners().values().forEach(bossSpawner -> {
            bossSpawner.getBossEntity().remove();
            bossSpawner.getSpawnerCrystal().remove();
            bossSpawner.getSpawnerHologram().removeHologram();
        });
    }

    private void removeAllBossBars() {
        DataManager.getInstance().getCache().getBossBars().values().forEach(BossBar::sendDestroyPacket);
    }

    private void removeAllMinions() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(entity -> entity != null && entity.hasMetadata("Minion")).forEach(Entity::remove));
    }
}