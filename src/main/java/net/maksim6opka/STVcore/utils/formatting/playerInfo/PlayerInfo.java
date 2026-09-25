package net.maksim6opka.STVcore.utils.formatting.playerInfo;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;

public class PlayerInfo {

    public static Component playerInfo(OfflinePlayer player, TextColor color) {
        if (player == null) {
            return Component.text("невідомо").color(color);
        }

        String name = player.getName();
        if (name == null) {
            name = "невідомо";
        }

        Component text = Component.empty()
                .append(Component.text("✎ ").color(color))
                .append(Component.text("Натисни, ").color(TextColor.fromHexString("#AAAAAA")))
                .append(Component.text("щоб надіслати повідомлення.").color(color))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("☠ ⏸ " + PlaceholderAPI.setPlaceholders(player, "%statistic_DEATHS%"))
                        .color(TextColor.fromHexString("#AAAAAA")))
                .append(Component.newline())
                .append(Component.text("⌚ ⏸ " + PlaceholderAPI.setPlaceholders(player, "%statistic_hours_played%") + "г.")
                        .color(TextColor.fromHexString("#AAAAAA")))
                .append(Component.newline())
                .append(Component.text("✉ ⏸ @" + PlaceholderAPI.setPlaceholders(player, "%stv_user_discord_username%"))
                        .color(TextColor.fromHexString("#AAAAAA")));

        return Component.text(name).color(color)
                .clickEvent(ClickEvent.suggestCommand("/msg" + name))
                .hoverEvent(HoverEvent.showText(text));
    }

    public static Component copyMsg(TextColor color) {
        return Component.empty()
                .append(Component
                        .text("✎ ")
                        .color(color)
                )
                .append(Component
                        .text("Натисни, ")
                        .color(TextColor.fromHexString("#AAAAAA"))
                )
                .append(Component
                        .text("щоб скопіювати.")
                        .color(color)
                );
    }

    public static Component parseMsg(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }
}
