package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;

public class Titles {

    public static final String[] BOSS_KILLER_UPGRADE = new String[]{
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.boss-killer-upgrade.title")),
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.boss-killer-upgrade.subtitle"))
    };
}