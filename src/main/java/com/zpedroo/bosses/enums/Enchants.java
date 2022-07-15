package com.zpedroo.bosses.enums;

import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.utils.loader.EnchantLoader;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

public enum Enchants {
    DAMAGE(EnchantLoader.load("damage")),
    CRIT_DAMAGE(EnchantLoader.load("crit-damage")),
    AREA_DAMAGE(EnchantLoader.load("area-damage")),
    EXP(EnchantLoader.load("exp"));

    private final Enchant enchant;

    Enchants(Enchant enchant) {
        this.enchant = enchant;
    }

    public Enchant get() {
        return enchant;
    }

    @Nullable
    public static Enchants getByName(String enchantName) {
        for (Enchants enchants : values()) {
            if (enchants.get() == null) continue;
            if (StringUtils.equalsIgnoreCase(enchants.get().getName(), enchantName)) return enchants;
        }

        return null;
    }

    @Nullable
    public static Enchant getEnchantByName(String enchantName) {
        for (Enchants enchants : values()) {
            if (enchants.get() == null) continue;
            if (StringUtils.equalsIgnoreCase(enchants.get().getName(), enchantName)) return enchants.get();
        }

        return null;
    }
}