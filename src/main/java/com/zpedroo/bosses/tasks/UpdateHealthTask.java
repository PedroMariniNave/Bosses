package com.zpedroo.bosses.tasks;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.BossBar;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class UpdateHealthTask extends BukkitRunnable {

    public UpdateHealthTask(Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        BossSpawner bossSpawner = DataManager.getInstance().getLastActiveBossSpawner();
        if (bossSpawner == null) return;

        DataManager.getInstance().getCache().getBossBars().values().forEach(bossBar -> {
            updateHealthDisplay(bossSpawner, bossBar);
        });
    }

    private void updateHealthDisplay(BossSpawner bossSpawner, BossBar bossBar) {
        bossBar.setMessage(bossSpawner.getBossBarDisplay());
        bossBar.setPercentage(bossSpawner.getBossBarPercentage());
        bossBar.update();
    }
}