package com.zpedroo.bosses.managers;

import com.zpedroo.bosses.managers.cache.DataCache;
import com.zpedroo.bosses.mysql.DBConnection;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.objects.spawner.BossSpawner;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.offlineapi.OfflinePlayerAPI;
import com.zpedroo.bosses.utils.serialization.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private final DataCache dataCache = new DataCache();

    public DataManager() {
        instance = this;
    }

    public PlayerData getPlayerData(@NotNull Player player) {
        return getPlayerDataByName(player.getName());
    }

    public PlayerData getPlayerDataByName(String playerName) {
        Player player = OfflinePlayerAPI.getPlayer(playerName);
        if (player == null) return null;

        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) {
            data = DBConnection.getInstance().getDBManager().getPlayerDataFromDatabase(player);
            dataCache.getPlayerData().put(player, data);
        }

        return data;
    }

    public BossSpawner getLastActiveBossSpawner() {
        return dataCache.getLastActiveBossSpawner();
    }

    public BossSpawner getBossSpawner(Location location) {
        return dataCache.getBossSpawners().get(location);
    }

    public void savePlayerData(Player player) {
        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) return;
        if (!data.isQueueUpdate()) return;

        DBConnection.getInstance().getDBManager().savePlayerData(data);
        data.setUpdate(false);
    }

    public void saveBossSpawnerLocation(BossSpawner bossSpawner) {
        FileUtils.Files file = FileUtils.Files.LOCATIONS;

        List<String> serializedLocations = FileUtils.get().getStringList(file, "Locations");
        String serializedLocation = LocationSerialization.serialize(bossSpawner.getLocation());
        if (serializedLocations.contains(serializedLocation)) return;

        serializedLocations.add(serializedLocation);

        FileUtils.FileManager fileManager = FileUtils.get().getFile(file);
        fileManager.get().set("Locations", serializedLocations);
        fileManager.save();
    }

    public void deleteBossSpawnerLocation(Location location) {
        FileUtils.Files file = FileUtils.Files.LOCATIONS;

        List<String> serializedLocations = FileUtils.get().getStringList(file, "Locations");
        String serializedLocation = LocationSerialization.serialize(location);
        if (!serializedLocations.contains(serializedLocation)) return;

        serializedLocations.remove(serializedLocation);

        FileUtils.FileManager fileManager = FileUtils.get().getFile(file);
        fileManager.get().set("Locations", serializedLocations);
        fileManager.save();
    }

    public void saveAllData() {
        dataCache.getPlayerData().keySet().forEach(this::savePlayerData);
        dataCache.getDeletedBossSpawners().forEach(this::deleteBossSpawnerLocation);
        dataCache.getBossSpawners().values().forEach(this::saveBossSpawnerLocation);
    }

    public DataCache getCache() {
        return dataCache;
    }
}