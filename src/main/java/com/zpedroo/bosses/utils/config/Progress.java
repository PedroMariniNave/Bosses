package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;

public class Progress {

    public static final int DISPLAY_AMOUNT = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Progress.display-amount");

    public static final String SYMBOL = FileUtils.get().getString(FileUtils.Files.CONFIG, "Progress.symbol");

    public static final String COMPLETE_COLOR = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Progress.complete-color"));

    public static final String INCOMPLETE_COLOR = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Progress.incomplete-color"));
}