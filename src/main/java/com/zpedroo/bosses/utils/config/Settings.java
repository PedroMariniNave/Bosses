package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;
import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.general.Currency;

import java.util.List;

public class Settings {

    public static final String COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.command");

    public static final List<String> ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.aliases");

    public static final List<String> BOSS_REGIONS = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.boss-regions");

    public static final String NULL_DAMAGER = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.null-damager");

    public static final long SAVE_INTERVAL = FileUtils.get().getLong(FileUtils.Files.CONFIG, "Settings.save-interval");

    public static final long FIND_TARGET_INTERVAL = FileUtils.get().getLong(FileUtils.Files.CONFIG, "Settings.find-target-interval");

    public static final int TARGET_RADIUS = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.target-radius");

    public static final int BOSS_KILLER_PICKUP_COOLDOWN = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.boss-killer-pickup-cooldown");

    public static final long BOSS_SPAWN_TIMER = FileUtils.get().getLong(FileUtils.Files.CONFIG, "Settings.boss-spawn-timer");

    public static final Currency QUALITY_CURRENCY = CurrencyAPI.getCurrency(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.quality-currency"));

    public static final String[] BOSS_SPAWNER_HOLOGRAM = Colorize.getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.boss-spawner-hologram"))
            .toArray(new String[1]);
}