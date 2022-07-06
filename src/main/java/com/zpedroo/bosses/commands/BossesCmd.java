package com.zpedroo.bosses.commands;

import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.objects.general.PlayerData;
import com.zpedroo.bosses.utils.config.Items;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.menu.Menus;
import com.zpedroo.bosses.utils.offlineapi.OfflinePlayerAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;

public class BossesCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        if (args.length > 0) {
            Player target = null;
            BigInteger amount = null;
            ItemStack item = null;
            switch (args[0].toUpperCase()) {
                case "SHOP":
                    if (player != null) Menus.getInstance().openShopMenu(player);
                    return true;
                case "TOP":
                    if (player != null) Menus.getInstance().openTopMenu(player);
                    return true;
                case "SPAWNER":
                    if (player == null || !sender.hasPermission("bosses.admin")) break;

                    giveItemToPlayer(player, Items.getBossSpawnerItem());
                    return true;
                case "GIVE":
                    if (!sender.hasPermission("bosses.admin") || args.length < 3) break;

                    target = OfflinePlayerAPI.getPlayer(args[1]);
                    if (target == null) break;

                    amount = NumberFormatter.getInstance().filter(args[2]);
                    if (amount.signum() <= 0) break;

                    item = Items.getBossPointsItem(amount);
                    giveItemToPlayer(target, item);
                    return true;
            }
        }

        if (player != null) Menus.getInstance().openMainMenu(player);
        return false;
    }

    private void giveItemToPlayer(Player target, ItemStack item) {
        if (target.getInventory().firstEmpty() != -1) {
            target.getInventory().addItem(item);
        } else {
            target.getWorld().dropItemNaturally(target.getLocation(), item);
        }
    }
}