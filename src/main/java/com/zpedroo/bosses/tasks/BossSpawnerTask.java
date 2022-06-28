package com.zpedroo.bosses.tasks;

import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

import static com.zpedroo.bosses.utils.config.Settings.BOSS_SPAWN_TIMER;

public class BossSpawnerTask extends BukkitRunnable {

    private final BossSpawner bossSpawner;
    private long nextSpawnInMillis;

    public BossSpawnerTask(BossSpawner bossSpawner) {
        this.bossSpawner = bossSpawner;
        startTask();
    }

    @Override
    public void run() {
        this.bossSpawner.spawnBoss();
        this.nextSpawnInMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(BOSS_SPAWN_TIMER);
    }

    public long getNextSpawnInMillis() {
        return nextSpawnInMillis;
    }

    private void startTask() {
        this.runTaskTimer(VoltzBosses.get(), 0L, BOSS_SPAWN_TIMER * 20L);
    }

}