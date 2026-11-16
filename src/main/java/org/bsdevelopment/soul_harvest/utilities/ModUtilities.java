package org.bsdevelopment.soul_harvest.utilities;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class ModUtilities {
    public static LinkedList<Component> formatMultiLine(String section) {
        LinkedList<Component> components = new LinkedList<>();
        int loreIndex = 1;
        while (true) {
            String key = section + "." + loreIndex;
            Component loreLine = Component.translatable(key);
            if (loreLine.getString().equals(key)) break;
            components.add(loreLine);
            loreIndex++;
        }
        return components;
    }

    public static String formatCompactNumber(long number) {
        if (number < 1000) return String.valueOf(number);

        int exponent = Math.min((int) Math.log10(number) / 3, 4);
        DecimalFormat format = new DecimalFormat("#.#");
        format.setRoundingMode(RoundingMode.FLOOR);

        return format.format(number / Math.pow(1000, exponent)) + "kMBT".charAt(exponent - 1);
    }

    public static int getExperienceForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getExperienceForLevel(int level, float progress) {
        int levelStart = getExperienceForLevel(level);
        return levelStart + Math.round(progress * (getExperienceForLevel(level + 1) - levelStart));
    }

    public static float getExactLevel(int experience) {
        return getLevelForExperience(experience) + getLevelProgress(experience);
    }

    public static int getLevelForExperience(int experience) {
        int level = 0;
        while (getExperienceForLevel(level + 1) <= experience) level++;
        return level;
    }

    public static float getLevelProgress(int experience) {
        int level = getLevelForExperience(experience);
        int levelStart = getExperienceForLevel(level);
        return (float) (experience - levelStart) / (getExperienceForLevel(level + 1) - levelStart);
    }

    public static int getTotalExperience(Player player) {
        return getExperienceForLevel(player.experienceLevel) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }
}
