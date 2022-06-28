package com.zpedroo.bosses.objects.general;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.List;

@Data
public class ShopItem {

    private final BigInteger price;
    private final int defaultAmount;
    private final ItemStack display;
    private final ItemStack shopItem;
    private final List<String> commands;
}