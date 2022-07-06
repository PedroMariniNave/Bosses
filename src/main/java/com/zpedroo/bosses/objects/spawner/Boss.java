package com.zpedroo.bosses.objects.spawner;

import com.zpedroo.bosses.objects.general.Drop;
import com.zpedroo.bosses.objects.general.TopDamageSettings;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@Data
public class Boss {

    private final String entityType;
    private final String bossBarDisplay;
    private final ItemStack[] armorContents;
    private final ItemStack itemInHand;
    private final List<String> spawnMessage;
    private final List<String> killMessage;
    private final List<String> hitCommands;
    private final List<Drop> drops;
    private final Map<Integer, TopDamageSettings> topDamageSettings;
    private final double bossKillerXpPerHit;
    private final int maxHealth;

    public int getTopDamagersAmount() {
        return topDamageSettings.size();
    }
}