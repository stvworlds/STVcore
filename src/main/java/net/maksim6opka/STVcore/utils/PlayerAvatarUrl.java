package net.maksim6opka.STVcore.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerAvatarUrl {
    private static final List<String> DEFAULT_SKINS = List.of(
            "X-Steve", "X-Alex", "X-Ari", "X-Efe",
            "X-Kai", "X-Makena", "X-Noor", "X-Sunny", "X-Zuri"
    );

    private static final Random RANDOM = new Random();
    private static final Pattern TEXTURE_URL_PATTERN = Pattern.compile(
            "\\\"url\\\"\\s*:\\s*\\\"[^\\\"]*/([^/\\\"]+)\\\""
    );

    /*
        Функції для отримання посилання на аватар гравця
     */

    public static String getAvatarUrl(Player player) {

        String textureId = PlaceholderAPI.setPlaceholders(
                player,
                "%skinsrestorer_texture_id%"
        );

        if (isInvalidTextureId(textureId)) {
            return getRandomAvatarUrl();
        }

        return "https://visage.surgeplay.com/bust/512/" + textureId + "?y=-20";
    }

    public static String getRandomAvatarUrl() {
        String randomSkin = DEFAULT_SKINS.get(
                RANDOM.nextInt(DEFAULT_SKINS.size())
        );
        return "https://visage.surgeplay.com/bust/512/" + randomSkin + "?y=-20";
    }

    /*
        Функції для отримання посилання на скін гравця
     */

    public static String getSkinUrl(Player player) {
        String textureId = PlaceholderAPI.setPlaceholders(
                player,
                "%skinsrestorer_texture_id%"
        );

        if (isInvalidTextureId(textureId)) {
            return getRandomSkinUrl();
        }

        return "https://visage.surgeplay.com/skin/" + textureId;
    }

    public static String getRandomSkinUrl() {
        String randomSkin = DEFAULT_SKINS.get(
                RANDOM.nextInt(DEFAULT_SKINS.size())
        );
        return "https://visage.surgeplay.com/skin/" + randomSkin;
    }

    public static String getSkinUrl(UUID uuid) {
        try {
            Optional<SkinProperty> skinOpt = getSkinProperty(uuid);
            Optional<String> textureId = skinOpt.flatMap(PlayerAvatarUrl::getTextureId);
            if (textureId.isPresent()) {
                return "https://visage.surgeplay.com/skin/" + textureId.get();
            }
        } catch (Exception ignored) {}
        return getRandomSkinUrl();
    }

    public static String getAvatarUrl(UUID uuid) {
        try {
            Optional<SkinProperty> skinOpt = getSkinProperty(uuid);
            Optional<String> textureId = skinOpt.flatMap(PlayerAvatarUrl::getTextureId);
            if (textureId.isPresent()) {
                return "https://visage.surgeplay.com/bust/512/" + textureId.get() + "?y=-20";
            }
        } catch (Exception ignored) {}
        return getRandomAvatarUrl();
    }

    private static Optional<SkinProperty> getSkinProperty(UUID uuid) throws Exception {
        PlayerStorage playerStorage = SkinsRestorerProvider.get().getPlayerStorage();
        Optional<SkinProperty> storedSkin = playerStorage.getSkinOfPlayer(uuid);
        if (storedSkin.isPresent()) {
            return storedSkin;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String playerName = offlinePlayer.getName();
        if (playerName == null || playerName.isBlank()) {
            return Optional.empty();
        }

        return playerStorage.getSkinForPlayer(uuid, playerName);
    }

    private static boolean isInvalidTextureId(String textureId) {
        return textureId == null
                || textureId.isBlank()
                || textureId.equalsIgnoreCase("Error")
                || textureId.equals("%skinsrestorer_texture_id%");
    }

    private static Optional<String> getTextureId(SkinProperty skinProperty) {
        String json = new String(
                Base64.getDecoder().decode(skinProperty.getValue()),
                StandardCharsets.UTF_8
        );
        Matcher matcher = TEXTURE_URL_PATTERN.matcher(json);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }
}
