package com.zpedroo.bosses.commands;

import com.zpedroo.bosses.utils.config.Items;
import com.zpedroo.bosses.utils.config.Messages;
import com.zpedroo.bosses.utils.config.Settings;
import com.zpedroo.bosses.utils.cooldown.Cooldown;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BossKillerCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (Cooldown.get().isInCooldown(player, this)) {
            player.sendMessage(StringUtils.replaceEach(Messages.COOLDOWN, new String[]{
                    "{cooldown}"
            }, new String[]{
                    String.valueOf(Cooldown.get().getTimeLeftInSeconds(player, this))
            }));
            return true;
        }

        Cooldown.get().addCooldown(player, this, Settings.BOSS_KILLER_PICKUP_COOLDOWN);

        ItemStack item = Items.getBossKillerItem();
        player.getInventory().addItem(item);
        return false;
    }
}