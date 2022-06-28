package com.zpedroo.bosses.listeners;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.BossBar;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.config.Items;
import com.zpedroo.bosses.utils.config.Messages;
import com.zpedroo.bosses.utils.config.Settings;
import com.zpedroo.bosses.utils.config.Titles;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigInteger;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionEnter(RegionEnterEvent event) {
        if (!Settings.BOSS_REGIONS.contains(event.getRegion().getId())) return;

        BossSpawner bossSpawner = DataManager.getInstance().getLastActiveBossSpawner();
        if (bossSpawner == null) return;

        Player player = event.getPlayer();
        BossBar bossBar = new BossBar(player, bossSpawner.getBossBarDisplay(), bossSpawner.getBossBarPercentage());

        DataManager.getInstance().getCache().getBossBars().put(player, bossBar);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionLeave(RegionLeaveEvent event) {
        if (!Settings.BOSS_REGIONS.contains(event.getRegion().getId())) return;

        BossBar bossBar = DataManager.getInstance().getCache().getBossBars().remove(event.getPlayer());
        if (bossBar == null) return;

        bossBar.sendDestroyPacket();
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
                if (bossBar == null) return;

                bossBar.sendDestroyPacket();
            }
        }.runTaskLaterAsynchronously(VoltzBosses.get(), 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        DataManager.getInstance().savePlayerData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand().clone();
        if (item == null || item.getType().equals(Material.AIR)) return;

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("BossSpawner")) return;

        event.setCancelled(true);

        Block block = event.getBlock();
        Location location = block.getLocation().clone().add(0D, 1D, 0D);

        BossSpawner bossSpawner = new BossSpawner(location);
        bossSpawner.cache();

        Player player = event.getPlayer();

        item.setAmount(1);
        player.getInventory().removeItem(item);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getItem().clone();
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("BossPointsAmount")) return;

        event.setCancelled(true);

        BigInteger amount = new BigInteger(nbt.getString("BossPointsAmount"));
        if (amount.signum() <= 0) return;

        Player player = event.getPlayer();
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionQuery query = container.createQuery();
        if (query.testState(player.getLocation(), player, DefaultFlag.PVP)) {
            player.sendMessage(Messages.PVP_AREA);
            return;
        }

        PlayerData data = DataManager.getInstance().getPlayerData(player);
        if (data == null) return;

        item.setAmount(1);
        player.getInventory().removeItem(item);

        data.addPoints(amount);

        String[] titles = Titles.ITEM_ACTIVATED;
        String title = StringUtils.replaceEach(titles[0], new String[] { "{amount}" }, new String[] { NumberFormatter.getInstance().format(amount) });
        String subtitle = StringUtils.replaceEach(titles[1], new String[] { "{amount}" }, new String[] { NumberFormatter.getInstance().format(amount) });

        player.sendTitle(title, subtitle);
        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 10f);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        int slot = player.getInventory().first(Material.ARROW);
        if (slot == -1) return;
        if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType().equals(Material.AIR)) return;

        ItemStack item = player.getInventory().getItem(slot).clone();
        Entity projectile = event.getProjectile();
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasKey("BossKillerArrowDamage")) {
            projectile.setMetadata("BossKillerArrowDamage", new FixedMetadataValue(VoltzBosses.get(), nbt.getDouble("BossKillerArrowDamage")));
        }

        if (event.getBow().getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0 || player.getGameMode() == GameMode.CREATIVE) {
            item.setAmount(1);
            player.getInventory().removeItem(item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal)) return;

        BossSpawner bossSpawner = DataManager.getInstance().getBossSpawner(event.getEntity().getLocation());
        if (bossSpawner == null) return;

        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        if (!player.isSneaking() || !player.hasPermission("bosses.admin")) return;

        bossSpawner.delete();
        player.getInventory().addItem(Items.getBossSpawnerItem());
    }
}