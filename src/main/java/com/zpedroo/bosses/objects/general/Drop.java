package com.zpedroo.bosses.objects.general;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Drop {

    private final ItemStack displayItem;
    private final ItemStack itemToGive;
    private final List<String> commands;
    private final double chance;

    public String getDisplayName() {
        return displayItem.getItemMeta().hasDisplayName() ? displayItem.getItemMeta().getDisplayName() : null;
    }

    public ItemStack getDisplayItem() {
        return displayItem == null ? null : displayItem.clone();
    }

    public ItemStack getItemToGive() {
        return itemToGive == null ? null : itemToGive.clone();
    }
}
