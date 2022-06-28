package com.zpedroo.bosses.utils.config;

import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.color.Colorize;

public class Titles {

    public static final String[] ITEM_ACTIVATED = new String[] {
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.item-activated.title")),
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.item-activated.subtitle"))
    };
}