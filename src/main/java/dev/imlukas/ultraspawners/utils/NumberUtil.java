package dev.imlukas.ultraspawners.utils;

import java.text.DecimalFormat;

public class NumberUtil {

    public static String formatDouble(double count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        DecimalFormat format = new DecimalFormat("0.#");
        String value =
                format.format(count / Math.pow(1000, exp));
        return String.format("%s%c", value, "kMBTPE".charAt(exp - 1));
    }
}
