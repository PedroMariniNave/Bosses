package com.zpedroo.bosses.utils.loader;

import com.zpedroo.bosses.enums.EnchantProperty;
import com.zpedroo.bosses.objects.general.Enchant;
import com.zpedroo.bosses.utils.FileUtils;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class EnchantLoader {

    public static Enchant load(String enchantName) {
        FileUtils.Files file = FileUtils.Files.CONFIG;
        if (!FileUtils.get().getFile(file).get().contains("Enchants." + enchantName)) return null;

        int initialLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.initial");
        int maxLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.max");
        int requiredLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.requirement-per-upgrade");
        BigInteger costPerLevel = NumberFormatter.getInstance().filter(FileUtils.get().getString(file, "Enchants." + enchantName + ".cost-per-level", "0"));
        double damageInitialValue = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".damage.initial-value");
        double damagePerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".damage.upgrade-per-level");
        double damageMultiplier = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".damage-multiplier");
        double chanceInitialValue = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".chance.initial-value");
        double chancePerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".chance.upgrade-per-level");
        double multiplierPerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".multiplier-per-level");
        int radiusInitialValue = FileUtils.get().getInt(file, "Enchants." + enchantName + ".radius.initial-value");
        int radiusPerLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".radius.upgrade-per-level");

        Map<EnchantProperty, Number> enchantProperties = new HashMap<>(EnchantProperty.values().length);
        enchantProperties.put(EnchantProperty.DAMAGE, damagePerLevel);
        enchantProperties.put(EnchantProperty.DAMAGE_MULTIPLIER, damageMultiplier);
        enchantProperties.put(EnchantProperty.CHANCE, chancePerLevel);
        enchantProperties.put(EnchantProperty.MULTIPLIER, multiplierPerLevel);
        enchantProperties.put(EnchantProperty.RADIUS, radiusPerLevel);

        Map<EnchantProperty, Number> initialValues = new HashMap<>(EnchantProperty.values().length);
        initialValues.put(EnchantProperty.DAMAGE, damageInitialValue);
        initialValues.put(EnchantProperty.CHANCE, chanceInitialValue);
        initialValues.put(EnchantProperty.RADIUS, radiusInitialValue);

        return new Enchant(enchantName, initialLevel, maxLevel, requiredLevel, costPerLevel, enchantProperties, initialValues);
    }
}