package com.zpedroo.bosses.commands;

import com.zpedroo.bosses.utils.config.Items;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.menu.Menus;
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
            BigInteger damage = null;
            BigInteger amount = null;
            ItemStack item = null;
            switch (args[0].toUpperCase()) {
                case "SHOP":
                    if (player == null) break;

                    Menus.getInstance().openShopMenu(player);
                    return true;
                case "TOP":
                    if (player == null) break;

                    Menus.getInstance().openTopMenu(player);
                    return true;
                case "SPAWNER":
                    if (!sender.hasPermission("bosses.admin")) break;
                    if (player == null) break;

                    player.getInventory().addItem(Items.getBossSpawnerItem());
                    return true;
                case "KILLER":
                    if (!sender.hasPermission("bosses.admin")) break;
                    if (args.length < 3) break;

                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) break;

                    damage = NumberFormatter.getInstance().filter(args[2]);
                    if (damage.signum() <= 0) break;

                    item = Items.getBossKillerItem(damage.doubleValue());
                    if (target.getInventory().firstEmpty() != -1) {
                        target.getInventory().addItem(item);
                    } else {
                        target.getWorld().dropItemNaturally(target.getLocation(), item);
                    }
                    return true;
                case "ARROW":
                    if (!sender.hasPermission("bosses.admin")) break;
                    if (args.length < 4) break;

                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) break;

                    damage = NumberFormatter.getInstance().filter(args[2]);
                    if (damage.signum() <= 0) break;

                    amount = NumberFormatter.getInstance().filter(args[3]);
                    if (amount.signum() <= 0) break;

                    item = Items.getBossKillerArrow(damage.doubleValue());
                    item.setAmount(amount.intValue());

                    if (target.getInventory().firstEmpty() != -1) {
                        target.getInventory().addItem(item);
                    } else {
                        target.getWorld().dropItemNaturally(target.getLocation(), item);
                    }
                    return true;
                case "ITEM":
                    if (!sender.hasPermission("bosses.admin")) break;
                    if (args.length < 3) break;

                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) break;

                    amount = NumberFormatter.getInstance().filter(args[2]);
                    if (amount.signum() <= 0) break;

                    item = Items.getBossPointsItem(amount);
                    if (target.getInventory().firstEmpty() != -1) {
                        target.getInventory().addItem(item);
                    } else {
                        target.getWorld().dropItemNaturally(target.getLocation(), item);
                    }
                    return true;
            }
        }

        if (player == null) return true;

        Menus.getInstance().openMainMenu(player);
        return false;
    }
}