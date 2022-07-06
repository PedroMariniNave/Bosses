package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;

import java.util.List;

public class Messages {

    public static final String NO_BOSS = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.no-boss"));

    public static final String NEED_BOSS_KILLER_ITEM = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-boss-killer-item"));

    public static final String COOLDOWN = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.cooldown"));

    public static final String INVALID_AMOUNT = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invalid-amount"));

    public static final String INSUFFICIENT_POINTS = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.insufficient-points"));

    public static final String NEED_SPACE = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-space"));

    public static final List<String> CHOOSE_AMOUNT = Colorize.getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.choose-amount"));

    public static final List<String> SUCCESSFUL_PURCHASED = Colorize.getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.successful-purchased"));
}