package com.zpedroo.bosses.mobai.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class EntityShootBowListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShoot(EntityShootBowEvent event) {
        if (!event.getEntity().hasMetadata("Boss")) return;

        event.setCancelled(true);
    }
}