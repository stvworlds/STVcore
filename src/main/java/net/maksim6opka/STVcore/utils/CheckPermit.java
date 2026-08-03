package net.maksim6opka.STVcore.utils;

import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

public final class CheckPermit {

    private static final URI HAS_PERMIT_ENDPOINT = URI.create("https://api.stvworlds.win/user/hasPermit");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private CheckPermit() {
    }

    public static boolean checkPermit(Player player) {
        UUID uuid = player.getUniqueId();
        String encodedUuid = URLEncoder.encode(uuid.toString(), StandardCharsets.UTF_8);
        String apiKey = Config.getString("api.key");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("[STVcore] API key is not configured (api.key).");
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HAS_PERMIT_ENDPOINT + "?searchType=UUID_MC&value=" + encodedUuid))
                .header("X-API-KEY", apiKey)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[STVcore] Permit check failed with HTTP status " + response.statusCode() + '.');
                return false;
            }

            String body = response.body().trim();
            if (!body.equalsIgnoreCase("true") && !body.equalsIgnoreCase("false")) {
                System.err.println("[STVcore] Permit check returned an invalid response: " + body);
                return false;
            }

            return Boolean.parseBoolean(body);
        } catch (IOException e) {
            System.err.println("[STVcore] Could not check player permit: " + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
