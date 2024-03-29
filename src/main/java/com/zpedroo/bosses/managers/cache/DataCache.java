package com.zpedroo.bosses.managers.cache;

import com.zpedroo.bosses.mysql.DBConnection;
import com.zpedroo.bosses.objects.general.*;
import com.zpedroo.bosses.objects.spawner.Boss;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.builder.ItemBuilder;
import com.zpedroo.bosses.utils.color.Colorize;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.serialization.LocationSerialization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.*;

@Getter
@Setter
public class DataCache {

    private final Set<Location> deletedBossSpawners = new HashSet<>(2);
    private final Map<Player, PlayerData> playerData = new HashMap<>(64);
    private final Map<Player, BossBar> bossBars = new HashMap<>(16);
    private final Map<Location, BossSpawner> bossSpawners = getBossSpawnersFromFile();
    private final List<Boss> bosses = getBossesFromConfig();
    private List<PlayerData> topBosses = DBConnection.getInstance().getDBManager().getTopBosses();
    private BossSpawner lastActiveBossSpawner;

    private Map<Location, BossSpawner> getBossSpawnersFromFile() {
        FileUtils.Files file = FileUtils.Files.LOCATIONS;

        List<String> serializedLocations = FileUtils.get().getStringList(file, "Locations");
        Map<Location, BossSpawner> ret = new HashMap<>(serializedLocations.size());

        for (String serializedLocation : serializedLocations) {
            Location location = LocationSerialization.deserialize(serializedLocation);

            ret.put(location, new BossSpawner(location));
        }

        return ret;
    }

    private List<Boss> getBossesFromConfig() {
        FileUtils.Files file = FileUtils.Files.CONFIG;

        Set<String> bosses = FileUtils.get().getSection(file, "Bosses");
        List<Boss> ret = new ArrayList<>(bosses.size());

        for (String boss : bosses) {
            String entityType = FileUtils.get().getString(file, "Bosses." + boss + ".entity-type");
            String bossBar = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Bosses." + boss + ".boss-bar"));
            ItemStack itemInHand = null;
            if (FileUtils.get().getFile(file).get().contains("Bosses." + boss + ".item-in-hand")) {
                itemInHand = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Bosses." + boss + ".item-in-hand").build();
            }

            List<ItemStack> equipments = new ArrayList<>(4);
            if (FileUtils.get().getFile(file).get().contains("Bosses." + boss + ".equipments")) {
                for (String equipment : FileUtils.get().getSection(file, "Bosses." + boss + ".equipments")) {
                    ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Bosses." + boss + ".equipments." + equipment).build();
                    equipments.add(item);
                }
            }

            List<String> spawnMessage = Colorize.getColored(FileUtils.get().getStringList(file, "Bosses." + boss + ".spawn-message"));
            List<String> killMessage = Colorize.getColored(FileUtils.get().getStringList(file, "Bosses." + boss + ".kill-message"));
            List<String> hitCommands = FileUtils.get().getStringList(file, "Bosses." + boss + ".hit-commands");
            List<Drop> drops = getDropsFromFile(file, "Bosses." + boss + ".drops");
            Map<Integer, TopDamageSettings> topDamageSettings = getTopDamageSettingsFromFile(file, "Bosses." + boss + ".top-damage");
            double bossKillerXpPerHit = FileUtils.get().getDouble(file, "Bosses." + boss + ".xp-per-damage");
            int maxHealth = FileUtils.get().getInt(file, "Bosses." + boss + ".max-health");

            ret.add(new Boss(entityType, bossBar, equipments.toArray(new ItemStack[4]), itemInHand, spawnMessage, killMessage, hitCommands, drops, topDamageSettings, bossKillerXpPerHit, maxHealth));
        }

        return ret;
    }

    private List<Drop> getDropsFromFile(FileUtils.Files file, String where) {
        List<Drop> ret = new ArrayList<>(2);
        if (where == null) return ret;

        for (String str : FileUtils.get().getSection(file, where)) {
            ItemStack displayItem = ItemBuilder.build(FileUtils.get().getFile(file).get(), where + "." + str + ".display-item").build();
            ItemStack itemToGive = null;
            if (FileUtils.get().getFile(file).get().contains(where + "." + str + ".item-to-give")) {
                itemToGive = ItemBuilder.build(FileUtils.get().getFile(file).get(), where + "." + str + ".item-to-give").build();
            }
            List<String> commands = FileUtils.get().getStringList(file, where + "." + str + ".commands");
            double chance = FileUtils.get().getDouble(file, where + "." + str + ".chance");

            ret.add(new Drop(displayItem, itemToGive, commands, chance));
        }

        return ret;
    }

    private Map<Integer, TopDamageSettings> getTopDamageSettingsFromFile(FileUtils.Files file, String where) {
        Map<Integer, TopDamageSettings> ret = new HashMap<>(4);
        if (where == null) return ret;

        for (String position : FileUtils.get().getSection(file, where)) {
            int pos = Integer.parseInt(position);

            String display = Colorize.getColored(FileUtils.get().getString(file, where + "." + position + ".display"));
            List<String> rewards = FileUtils.get().getStringList(file, where + "." + position + ".commands");

            ret.put(pos, new TopDamageSettings(display, rewards));
        }

        return ret;
    }
}