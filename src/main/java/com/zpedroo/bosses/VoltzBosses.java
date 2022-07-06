package com.zpedroo.bosses;

import com.zpedroo.bosses.commands.BossKillerCmd;
import com.zpedroo.bosses.commands.BossesCmd;
import com.zpedroo.bosses.hooks.PlaceholderAPIHook;
import com.zpedroo.bosses.listeners.*;
import com.zpedroo.bosses.managers.BossSpawnerManager;
import com.zpedroo.bosses.managers.DataManager;
import com.zpedroo.bosses.mobai.listeners.EntityDamageListener;
import com.zpedroo.bosses.mobai.listeners.EntityShootBowListener;
import com.zpedroo.bosses.mobai.listeners.EntityTargetListener;
import com.zpedroo.bosses.mobai.listeners.PlayerMoveListener;
import com.zpedroo.bosses.mysql.DBConnection;
import com.zpedroo.bosses.tasks.FindTargetTask;
import com.zpedroo.bosses.tasks.SaveTask;
import com.zpedroo.bosses.tasks.UpdateHealthTask;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.cooldown.Cooldown;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.menu.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static com.zpedroo.bosses.utils.config.Settings.*;

public class VoltzBosses extends JavaPlugin {

    private static VoltzBosses instance;
    public static VoltzBosses get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);

        if (!isMySQLEnabled(getConfig())) {
            getLogger().log(Level.SEVERE, "MySQL are disabled! You need to enable it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new DBConnection(getConfig());
        new NumberFormatter(getConfig());
        new DataManager();
        new BossSpawnerManager();
        new Menus();
        new Cooldown();
        new FindTargetTask(this);
        new SaveTask(this);
        new UpdateHealthTask(this);

        registerHooks();
        registerListeners();
        registerCommand(COMMAND, ALIASES, new BossesCmd());
        registerCommand("matadora", Arrays.asList("matadoradeboss"), new BossKillerCmd());
    }

    public void onDisable() {
        if (!isMySQLEnabled(getConfig())) return;

        try {
            DataManager.getInstance().saveAllData();
            BossSpawnerManager.getInstance().clearAll();
            DBConnection.getInstance().closeConnection();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An error occurred while trying to save data!");
            ex.printStackTrace();
        }
    }

    private void registerHooks() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this);
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BossGeneralListeners(), this);
        getServer().getPluginManager().registerEvents(new DropListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(), this);
        getServer().getPluginManager().registerEvents(new RegionListeners(), this);

        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new EntityShootBowListener(), this);
        getServer().getPluginManager().registerEvents(new EntityTargetListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
    }

    private void registerCommand(String command, List<String> aliases, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCmd = constructor.newInstance(command, this);
            pluginCmd.setAliases(aliases);
            pluginCmd.setExecutor(executor);

            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(getName().toLowerCase(), pluginCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isMySQLEnabled(FileConfiguration file) {
        if (!file.contains("MySQL.enabled")) return false;

        return file.getBoolean("MySQL.enabled");
    }
}