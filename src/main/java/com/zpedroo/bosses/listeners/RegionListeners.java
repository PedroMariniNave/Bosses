package com.zpedroo.bosses.listeners;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.BossBar;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.config.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RegionListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionEnter(RegionEnterEvent event) {
        if (!Settings.BOSS_REGIONS.contains(event.getRegion().getId())) return;

        BossSpawner bossSpawner = DataManager.getInstance().getLastActiveBossSpawner();
        if (bossSpawner == null) return;

        Player player = event.getPlayer();
        BossBar bossBar = new BossBar(player, bossSpawner.getBossBarDisplay(), bossSpawner.getBossBarPercentage());
        bossBar.cache();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionLeave(RegionLeaveEvent event) {
        if (!Settings.BOSS_REGIONS.contains(event.getRegion().getId())) return;

        BossBar bossBar = DataManager.getInstance().getCache().getBossBars().remove(event.getPlayer());
        if (bossBar != null) bossBar.sendDestroyPacket();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player == null || !player.isOnline()) return;

                ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
                if (regions.getRegions().stream().anyMatch(protectedRegion -> Settings.BOSS_REGIONS.contains(protectedRegion.getId()))) return;

                BossBar bossBar = DataManager.getInstance().getCache().getBossBars().remove(event.getPlayer());
                if (bossBar != null) bossBar.sendDestroyPacket();
            }
        }.runTaskLaterAsynchronously(VoltzBosses.get(), 5L);
    }
}