package net.maksim6opka.STVcore.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Пошук користувача через {@code GET /v2/user/find}.
 * @see <a href="https://api.stvworlds.win/swagger-ui/index.html#/Users/find">Users/find</a>
 */
public final class FindUser {

    public enum FindKey {
        ID,
        USERNAME,
        EMAIL,
        DISCORD_ID,
        UUID_MC,
        NICKNAME_MC
    }

    private static final URI FIND_ENDPOINT = URI.create("https://api.stvworlds.win/v2/user/find");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static final Pattern DISCORD_ID_PATTERN = Pattern.compile("\"discordID\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern UUID_MC_PATTERN = Pattern.compile("\"uuidMC\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern NICKNAME_MC_PATTERN = Pattern.compile("\"nicknameMC\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("\"username\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    private FindUser() {
    }

    public static Optional<UserInfo> find(FindKey key, String value) {
        if (key == null || value == null || value.isBlank()) {
            return Optional.empty();
        }

        String apiKey = Config.getString("api.key");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("[STVcore] API key is not configured (api.key).");
            return Optional.empty();
        }

        String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FIND_ENDPOINT + "?key=" + key.name() + "&value=" + encodedValue))
                .header("X-API-KEY", apiKey)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return Optional.empty();
            }
            if (response.statusCode() != 200) {
                System.err.println("[STVcore] User find failed with HTTP status " + response.statusCode() + '.');
                return Optional.empty();
            }

            String body = response.body();
            if (body == null || body.isBlank() || body.equals("null")) {
                return Optional.empty();
            }

            return parseUser(body);
        } catch (IOException e) {
            System.err.println("[STVcore] Could not find user: " + e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    public static CompletableFuture<Optional<UserInfo>> findAsync(FindKey key, String value) {
        return CompletableFuture.supplyAsync(() -> find(key, value));
    }

    public static Optional<UserInfo> findByMcUuid(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        return find(FindKey.UUID_MC, uuid.toString());
    }

    public static CompletableFuture<Optional<UserInfo>> findByMcUuidAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findByMcUuid(uuid));
    }

    public static Optional<UserInfo> findByPlayer(OfflinePlayer player) {
        if (player == null) {
            return Optional.empty();
        }
        return findByMcUuid(player.getUniqueId());
    }

    public static String getDiscordId(UUID uuid) {
        return findByMcUuid(uuid)
                .map(UserInfo::discordId)
                .filter(id -> id != null && !id.isBlank())
                .orElse(null);
    }

    public static String getDiscordId(OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        return getDiscordId(player.getUniqueId());
    }

    public static String getDiscordId(Player player) {
        if (player == null) {
            return null;
        }
        return getDiscordId(player.getUniqueId());
    }

    public static CompletableFuture<String> getDiscordIdAsync(UUID uuid) {
        return findByMcUuidAsync(uuid).thenApply(opt ->
                opt.map(UserInfo::discordId)
                        .filter(id -> id != null && !id.isBlank())
                        .orElse(null)
        );
    }

    public static CompletableFuture<String> getDiscordIdAsync(OfflinePlayer player) {
        if (player == null) {
            return CompletableFuture.completedFuture(null);
        }
        return getDiscordIdAsync(player.getUniqueId());
    }

    private static Optional<UserInfo> parseUser(String body) {
        String discordId = firstGroup(DISCORD_ID_PATTERN, body);
        if (discordId != null && discordId.isBlank()) {
            discordId = null;
        }

        Long id = null;
        String idRaw = firstGroup(ID_PATTERN, body);
        if (idRaw != null) {
            try {
                id = Long.parseLong(idRaw);
            } catch (NumberFormatException ignored) {
            }
        }

        return Optional.of(new UserInfo(
                id,
                discordId,
                firstGroup(USERNAME_PATTERN, body),
                firstGroup(UUID_MC_PATTERN, body),
                firstGroup(NICKNAME_MC_PATTERN, body)
        ));
    }

    private static String firstGroup(Pattern pattern, String body) {
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public record UserInfo(
            Long id,
            String discordId,
            String username,
            String uuidMc,
            String nicknameMc
    ) {
    }
}
