package net.maksim6opka.STVcore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Аналог Denizen task {@code text_typing_task}: поступово друкує текст у actionbar.
 */
public final class TextTypingTask {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public enum Type {
        WARNING,
        MESSAGE
    }

    private TextTypingTask() {
    }

    public static Component prefix(Type type) {
        return switch (type) {
            case WARNING -> MINI.deserialize(
                    "<gradient:#E53B3B:#FFFFFF>Увага!</gradient><dark_gray> »</dark_gray><reset> "
            );
            case MESSAGE -> MINI.deserialize(
                    "<gradient:#9863E7:#578D70>Оповіщення</gradient><dark_gray> »</dark_gray><reset> "
            );
        };
    }

    /**
     * @param speedTicks пауза між символами (у Denizen — {@code spead}, наприклад {@code 1t})
     * @param onComplete викликається на головному потоці після останнього символу; може бути {@code null}
     */
    public static void run(
            JavaPlugin plugin,
            Player player,
            String text,
            Type type,
            long speedTicks,
            Runnable onComplete
    ) {
        if (!player.isOnline()) {
            return;
        }

        Component formattedName = prefix(type);
        char[] chars = text.toCharArray();

        if (chars.length == 0) {
            player.sendActionBar(formattedName);
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        new BukkitRunnable() {
            private int index = 0;
            private final StringBuilder current = new StringBuilder();

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                current.append(chars[index]);
                player.sendActionBar(formattedName.append(Component.text(current.toString())));
                player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_STOP_USING, 1.0f, 1.85f);

                index++;
                if (index >= chars.length) {
                    cancel();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, Math.max(1L, speedTicks));
    }
}
