package net.maksim6opka.STVcore.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Example usage:
 *
 * <pre>{@code
 * DiscordWebhook webhook = new DiscordWebhook(
 *         "https://discord.com/api/webhooks/WEBHOOK_ID/WEBHOOK_TOKEN"
 * );
 *
 * DiscordWebhook.Embed embed = new DiscordWebhook.Embed()
 *         .title("Нова подія")
 *         .description("Щось сталося на сервері.")
 *         .color(0x5865F2)
 *         .author("My Bot")
 *         .addField("Користувач", "Player123", true)
 *         .addField("Статус", "Успішно", true)
 *         .thumbnail("https://example.com/image.png")
 *         .footer("My Application");
 *
 * webhook
 *         .username("My Webhook")
 *         .content("Додатковий текст")
 *         .addEmbed(embed)
 *         .send();
 * }</pre>
 */

public class DiscordWebhook {

    private final String webhookUrl;
    private final HttpClient httpClient;

    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;

    private final List<Embed> embeds = new ArrayList<>();

    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public DiscordWebhook content(String content) {
        this.content = content;
        return this;
    }

    public DiscordWebhook username(String username) {
        this.username = username;
        return this;
    }

    public DiscordWebhook avatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public DiscordWebhook tts(boolean tts) {
        this.tts = tts;
        return this;
    }

    public DiscordWebhook addEmbed(Embed embed) {
        this.embeds.add(embed);
        return this;
    }

    public void send() throws IOException, InterruptedException {
        String json = buildJson();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "Discord webhook failed: HTTP "
                            + response.statusCode()
                            + " - "
                            + response.body()
            );
        }
    }

    private String buildJson() {
        StringBuilder json = new StringBuilder("{");

        boolean first = true;

        if (content != null) {
            appendComma(json, first);
            first = false;
            json.append("\"content\":\"")
                    .append(escapeJson(content))
                    .append("\"");
        }

        if (username != null) {
            appendComma(json, first);
            first = false;
            json.append("\"username\":\"")
                    .append(escapeJson(username))
                    .append("\"");
        }

        if (avatarUrl != null) {
            appendComma(json, first);
            first = false;
            json.append("\"avatar_url\":\"")
                    .append(escapeJson(avatarUrl))
                    .append("\"");
        }

        if (tts) {
            appendComma(json, first);
            first = false;
            json.append("\"tts\":true");
        }

        if (!embeds.isEmpty()) {
            appendComma(json, first);
            json.append("\"embeds\":[");

            for (int i = 0; i < embeds.size(); i++) {
                if (i > 0) {
                    json.append(",");
                }

                json.append(embeds.get(i).toJson());
            }

            json.append("]");
        }

        json.append("}");

        return json.toString();
    }

    private static void appendComma(StringBuilder builder, boolean first) {
        if (!first) {
            builder.append(",");
        }
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static class Embed {

        private String title;
        private String description;
        private String url;
        private Integer color;
        private String footerText;
        private String footerIconUrl;
        private String thumbnailUrl;
        private String imageUrl;
        private String authorName;
        private String authorUrl;
        private String authorIconUrl;

        private final List<Field> fields = new ArrayList<>();

        public Embed title(String title) {
            this.title = title;
            return this;
        }

        public Embed description(String description) {
            this.description = description;
            return this;
        }

        public Embed url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Discord color у форматі 0xRRGGBB.
         * Наприклад: 0x5865F2
         */
        public Embed color(int color) {
            this.color = color;
            return this;
        }

        public Embed footer(String text) {
            this.footerText = text;
            return this;
        }

        public Embed footer(String text, String iconUrl) {
            this.footerText = text;
            this.footerIconUrl = iconUrl;
            return this;
        }

        public Embed thumbnail(String url) {
            this.thumbnailUrl = url;
            return this;
        }

        public Embed image(String url) {
            this.imageUrl = url;
            return this;
        }

        public Embed author(String name) {
            this.authorName = name;
            return this;
        }

        public Embed author(String name, String url, String iconUrl) {
            this.authorName = name;
            this.authorUrl = url;
            this.authorIconUrl = iconUrl;
            return this;
        }

        public Embed addField(String name, String value, boolean inline) {
            fields.add(new Field(name, value, inline));
            return this;
        }

        private String toJson() {
            StringBuilder json = new StringBuilder("{");
            boolean first = true;

            if (title != null) {
                appendComma(json, first);
                first = false;
                json.append("\"title\":\"")
                        .append(escapeJson(title))
                        .append("\"");
            }

            if (description != null) {
                appendComma(json, first);
                first = false;
                json.append("\"description\":\"")
                        .append(escapeJson(description))
                        .append("\"");
            }

            if (url != null) {
                appendComma(json, first);
                first = false;
                json.append("\"url\":\"")
                        .append(escapeJson(url))
                        .append("\"");
            }

            if (color != null) {
                appendComma(json, first);
                first = false;
                json.append("\"color\":")
                        .append(color);
            }

            if (authorName != null) {
                appendComma(json, first);
                first = false;

                json.append("\"author\":{")
                        .append("\"name\":\"")
                        .append(escapeJson(authorName))
                        .append("\"");

                if (authorUrl != null) {
                    json.append(",\"url\":\"")
                            .append(escapeJson(authorUrl))
                            .append("\"");
                }

                if (authorIconUrl != null) {
                    json.append(",\"icon_url\":\"")
                            .append(escapeJson(authorIconUrl))
                            .append("\"");
                }

                json.append("}");
            }

            if (footerText != null) {
                appendComma(json, first);
                first = false;

                json.append("\"footer\":{")
                        .append("\"text\":\"")
                        .append(escapeJson(footerText))
                        .append("\"");

                if (footerIconUrl != null) {
                    json.append(",\"icon_url\":\"")
                            .append(escapeJson(footerIconUrl))
                            .append("\"");
                }

                json.append("}");
            }

            if (thumbnailUrl != null) {
                appendComma(json, first);
                first = false;

                json.append("\"thumbnail\":{\"url\":\"")
                        .append(escapeJson(thumbnailUrl))
                        .append("\"}");
            }

            if (imageUrl != null) {
                appendComma(json, first);
                first = false;

                json.append("\"image\":{\"url\":\"")
                        .append(escapeJson(imageUrl))
                        .append("\"}");
            }

            if (!fields.isEmpty()) {
                appendComma(json, first);

                json.append("\"fields\":[");

                for (int i = 0; i < fields.size(); i++) {
                    if (i > 0) {
                        json.append(",");
                    }

                    json.append(fields.get(i).toJson());
                }

                json.append("]");
            }

            json.append("}");

            return json.toString();
        }
    }

    private record Field(String name, String value, boolean inline) {

        private String toJson() {
            return "{"
                    + "\"name\":\"" + escapeJson(name) + "\","
                    + "\"value\":\"" + escapeJson(value) + "\","
                    + "\"inline\":" + inline
                    + "}";
        }
    }
}
