package com.zpedroo.bosses.objects.general;

import com.zpedroo.bosses.enums.EnchantProperty;
import com.zpedroo.bosses.utils.bosskiller.BossKillerUtils;
import com.zpedroo.bosses.utils.formatter.NumberFormatter;
import com.zpedroo.bosses.utils.roman.NumberConverter;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class Enchant {

    private final String name;
    private final int initialLevel;
    private final int maxLevel;
    private final int requiredLevel;
    private final int costPerLevel;
    private final Map<EnchantProperty, Number> enchantProperties;
    private final Map<EnchantProperty, Number> initialValues;

    public Number getPropertyEffect(EnchantProperty property) {
        return enchantProperties.getOrDefault(property, 0);
    }

    public Number getPropertyInitialValue(EnchantProperty property) {
        return initialValues.getOrDefault(property, 0);
    }

    public List<String> getPlaceholders() {
        Set<EnchantProperty> properties = enchantProperties.keySet();
        List<String> placeholders = new ArrayList<>(properties.size());

        placeholders.add("{" + name + "}");
        placeholders.add("{" + name + "_next}");

        for (EnchantProperty property : properties) {
            placeholders.add("{" + name + "_" + property.name().toLowerCase() + "}");
            placeholders.add("{" + name + "_next_" + property.name().toLowerCase() + "}");
        }

        return placeholders;
    }

    public List<String> getReplacers(@NotNull ItemStack item) {
        Set<EnchantProperty> properties = enchantProperties.keySet();
        List<String> replacers = new ArrayList<>(properties.size());

        int quality = BossKillerUtils.getItemQuality(item);
        int level = BossKillerUtils.getEnchantmentLevel(item, this);
        int nextLevel = level + 1;

        replacers.add(NumberConverter.convertToRoman(level));
        replacers.add(NumberConverter.convertToRoman(nextLevel));

        for (EnchantProperty property : properties) {
            replacers.add(NumberFormatter.getInstance().formatDecimal(BossKillerUtils.getEnchantEffectByLevel(this, property, level, quality)));
            replacers.add(NumberFormatter.getInstance().formatDecimal(BossKillerUtils.getEnchantEffectByLevel(this, property, nextLevel, quality)));
        }

        return replacers;
    }
}