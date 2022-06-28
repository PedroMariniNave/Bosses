package com.zpedroo.bosses.tasks;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.zpedroo.bosses.utils.config.Settings.FIND_TARGET_INTERVAL;

public class FindTargetTask extends BukkitRunnable {

    public FindTargetTask(Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, FIND_TARGET_INTERVAL * 20L, FIND_TARGET_INTERVAL * 20L);
    }

    @Override
    public void run() {
        DataManager.getInstance().getCache().getBossSpawners().values().forEach(BossSpawner::updateTarget);
    }
}