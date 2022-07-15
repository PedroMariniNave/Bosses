package com.zpedroo.bosses.utils.progress;

import com.google.common.base.Strings;
import com.zpedroo.bosses.utils.config.Quality;
import com.zpedroo.bosses.utils.formula.ExperienceManager;

import static com.zpedroo.bosses.utils.config.Progress.*;

public class ProgressConverter {

    public static String convertExperience(double experience) {
        double percentage = getPercentage(experience) / 100;
        int completedProgressBars = (int) (DISPLAY_AMOUNT * percentage);
        int incompleteProgressBars = DISPLAY_AMOUNT - completedProgressBars;

        return COMPLETE_COLOR + Strings.repeat(SYMBOL, completedProgressBars) +
                INCOMPLETE_COLOR + Strings.repeat(SYMBOL, incompleteProgressBars);
    }

    public static String convertQuality(int qualityLevel) {
        if (qualityLevel >= Quality.MAX) return Quality.COMPLETE_COLOR + Strings.repeat(Quality.SYMBOL, Quality.MAX);

        double percentage = (double) qualityLevel / Quality.MAX;
        int completedProgressBars = (int) (Quality.MAX * percentage);
        int incompleteProgressBars = Quality.MAX - completedProgressBars;

        return Quality.COMPLETE_COLOR + Strings.repeat(Quality.SYMBOL, completedProgressBars) +
                Quality.INCOMPLETE_COLOR + Strings.repeat(Quality.SYMBOL, incompleteProgressBars);
    }

    public static double getPercentage(double experience) {
        int level = ExperienceManager.getLevel(experience);
//       if (level >= Settings.MAX_LEVEL) return 100;

        double xpToActualLevel = ExperienceManager.getFullLevelExperience(level-1);
        double xpToNextLevel = ExperienceManager.getFullLevelExperience(level);

        double requiredXPToUpgradeLevel = xpToNextLevel - xpToActualLevel;
        double has = experience - xpToActualLevel;

        double percentage = (has / requiredXPToUpgradeLevel) * 100;

        return percentage > 0 ? percentage : 0;
    }
}