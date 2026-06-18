package net.maksim6opka.STVcore.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class PlayerAvatarUrl {
    private static final List<String> DEFAULT_SKINS = List.of(
            "X-Steve", "X-Alex", "X-Ari", "X-Efe",
            "X-Kai", "X-Makena", "X-Noor", "X-Sunny", "X-Zuri"
    );

    private static final Random RANDOM = new Random();

    public static String getAvatarUrl(Player player) {

        String textureId = PlaceholderAPI.setPlaceholders(
                player,
                "%skinsrestorer_texture_id%"
        );

        if (textureId.equalsIgnoreCase("Error")) {
            String randomSkin = DEFAULT_SKINS.get(
                    RANDOM.nextInt(DEFAULT_SKINS.size())
            );
            return "https://visage.surgeplay.com/bust/512/" + randomSkin + "?y=-20";
        }

        return "https://visage.surgeplay.com/bust/512/" + textureId + "?y=-20";
    }
}
