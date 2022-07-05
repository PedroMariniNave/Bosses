package com.zpedroo.bosses.utils.offlineapi;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class OfflinePlayerAPI {

    private static Object worldType;
    private static Method getServer;
    private static Method getWorldServer;
    private static Method getBukkitEntity;
    private static Constructor<?> entityPlayerConstructor;
    private static Constructor<?> playerInteractManagerConstructor;

    @SuppressWarnings("deprecation")
    public static Player getPlayer(String playerName) {
        Player testPlayer = Bukkit.getPlayerExact(playerName);
        if (testPlayer != null) return testPlayer;

        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) return null;

            Object minecraftServer = getServer.invoke(null);
            Object worldServer = getWorldServer.invoke(minecraftServer, worldType);
            Object playerInteractManager = playerInteractManagerConstructor.newInstance(worldServer);
            Object entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServer, playerName, playerInteractManager);

            Player player = (Player) getBukkitEntity.invoke(entityPlayer);
            player.loadData();

            return player;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        try
        {
            Class<?> PlayerInteractManagerClass = ReflectionUtils.getNMSClass("PlayerInteractManager");
            Class<?> MinecraftServerClass = ReflectionUtils.getNMSClass("MinecraftServer");
            Class<?> EntityPlayerClass = ReflectionUtils.getNMSClass("EntityPlayer");
            Class<?> WorldServerClass = ReflectionUtils.getNMSClass("WorldServer");
            Class<?> WorldClass = ReflectionUtils.getNMSClass("World");
            Class<?> WorldTypeClass;
            Class<?> gameProfileClass;

            WorldTypeClass = int.class;
            worldType = 0;

            gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");

            getServer = MinecraftServerClass.getMethod("getServer");
            getWorldServer = MinecraftServerClass.getMethod("getWorldServer", WorldTypeClass);
            getBukkitEntity = EntityPlayerClass.getMethod("getBukkitEntity");
            entityPlayerConstructor = EntityPlayerClass.getConstructor(MinecraftServerClass, WorldServerClass, gameProfileClass, PlayerInteractManagerClass);
            playerInteractManagerConstructor = PlayerInteractManagerClass.getConstructor(WorldClass);
        } catch (Throwable e) {

        }
    }
}