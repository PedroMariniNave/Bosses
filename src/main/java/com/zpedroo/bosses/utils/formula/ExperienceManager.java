package com.zpedroo.bosses.utils.formula;

import java.util.HashMap;
import java.util.Map;

import static com.zpedroo.bosses.utils.config.ExperienceFormula.BASE_EXP;
import static com.zpedroo.bosses.utils.config.ExperienceFormula.EXPONENT;

public class ExperienceManager {

    private static final Map<Integer, Double> levelExperienceCache = new HashMap<>();
    private static final Map<Integer, Double> levelFullExperienceCache = new HashMap<>();

    public static int getLevel(double experience) {
        int level = 1;
        while (experience >= getFullLevelExperience(level)) {
            ++level;
        }

        return level;
    }

    public static double getFullLevelExperience(int level) {
        if (!levelFullExperienceCache.containsKey(level)) {
            double requiredXP = 0;
            for (int i = 1; i <= level; i++) {
                requiredXP += getUpgradeLevelExperience(i);
            }

            levelFullExperienceCache.put(level, requiredXP);
        }

        return levelFullExperienceCache.get(level);
    }

    public static double getUpgradeLevelExperience(int level) {
        return getLevelExperience(++level);
    }

    public static double getLevelExperience(int level) {
        if (level <= 1) return 0;
        if (!levelExperienceCache.containsKey(level)) {
            double experience = Math.floor(BASE_EXP + (BASE_EXP * Math.pow(level, EXPONENT)));
            levelExperienceCache.put(level, experience);
        }

        return levelExperienceCache.get(level);
    }

}