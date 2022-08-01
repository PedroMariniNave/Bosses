package com.zpedroo.bosses.objects.general;

import com.zpedroo.bosses.managers.DataManager;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BossBar {
    
    private final Player player;
    private String message;
    private float percentage;
    private EntityWither wither;

    public BossBar(Player player, String message, float percentage) {
        this.player = player;
        this.message = message;
        this.percentage = percentage;
        this.update();
    }

    public void update() {
        sendDestroyPacket();

        Vector vector = player.getLocation().getDirection();
        Location location = player.getLocation().clone().add(vector.multiply(25));
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        wither = new EntityWither(world);
        wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        wither.setCustomName(message);
        wither.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
        wither.setHealth(percentage * 3); // x3 because wither max health is 300
        wither.setInvisible(true);
        wither.setSize(0, 0);

        Entity nmsEntity = wither;
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) tag = new NBTTagCompound();

        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public void sendDestroyPacket() {
        if (wither == null) return;

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void cache() {
        DataManager.getInstance().getCache().getBossBars().put(player, this);
    }
}