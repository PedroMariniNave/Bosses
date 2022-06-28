package com.zpedroo.bosses.tasks;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.mysql.DBConnection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.zpedroo.bosses.utils.config.Settings.*;

public class SaveTask extends BukkitRunnable {

    public SaveTask(Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, SAVE_INTERVAL * 20L, SAVE_INTERVAL * 20L);
    }

    @Override
    public void run() {
        DataManager.getInstance().saveAllData();
        DataManager.getInstance().getCache().setTopBosses(DBConnection.getInstance().getDBManager().getTopBosses());
    }
}