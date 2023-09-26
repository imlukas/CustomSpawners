package dev.imlukas.ultraspawners.utils.text;


import dev.imlukas.ultraspawners.utils.component.ComponentUtil;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextUtils {

    private TextUtils() {}

    private static final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}");

    public static String color(String message) {
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, ChatColor.of(color) + "");
            matcher = hexPattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String enumToText(Enum<?> enumToText) {
        return capitalize(enumToText.toString().replace("_", " "));
    }

    public static String capitalize(String toCapitalize) {
        return toCapitalize.substring(0, 1).toUpperCase() + toCapitalize.substring(1).toLowerCase();
    }

    public static TextComponent toComponent(String message) {
        return ComponentUtil.create(message);
    }

    /**
     * Parses a String to an integer, throwing an IllegalArgumentException if the String is not a valid integer.
     *
     * @param stringToParse The String to parse
     * @param predicate     A Predicate to test the parsed integer against
     * @return The parsed integer
     */
    public static Optional<Integer> parseInt(String stringToParse, Predicate<Integer> predicate) {
        int parsed;
        try {
            parsed = Integer.parseInt(stringToParse);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + stringToParse);
            return Optional.of(1);
        }

        if (!predicate.test(parsed)) {
            System.err.println("Invalid number: " + stringToParse);
            return Optional.of(1);
        }

        return Optional.of(parsed);
    }

    public static Optional<Integer> parseInt(String stringToParse) {
        return parseInt(stringToParse, ignored -> true);
    }

}

