package net.nekoepisode.tankserver.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    /**
     * Extracts all player names mentioned in the message.
     */
    public static List<String> parseMentions(String message) {
        List<String> mentions = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(message);
        while (matcher.find()) {
            mentions.add(matcher.group(1));
        }
        return mentions;
    }
}