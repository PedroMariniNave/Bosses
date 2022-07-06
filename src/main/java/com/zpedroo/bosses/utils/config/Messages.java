package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;

public class Messages {

    public static final String NO_BOSS = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.no-boss"));

    public static final String NEED_BOSS_KILLER_ITEM = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-boss-killer-item"));

    public static final String COOLDOWN = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.cooldown"));
}