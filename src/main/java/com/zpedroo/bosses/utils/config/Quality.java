package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;

import java.math.BigInteger;

public class Quality {

    public static final int INITIAL = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Quality.initial");

    public static final int MAX = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Quality.max");

    public static final int ITEM_LEVEL_PER_QUALITY = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Quality.item-level-per-quality");

    public static final BigInteger COST_PER_QUALITY = NumberFormatter.getInstance().filter(FileUtils.get().getString(FileUtils.Files.CONFIG, "Quality.cost-per-quality"));

    public static final double BONUS_PER_QUALITY = FileUtils.get().getDouble(FileUtils.Files.CONFIG, "Quality.bonus-per-quality");

    public static final String SYMBOL = FileUtils.get().getString(FileUtils.Files.CONFIG, "Quality.symbol");

    public static final String COMPLETE_COLOR = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Quality.complete-color"));

    public static final String INCOMPLETE_COLOR = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Quality.incomplete-color"));
}