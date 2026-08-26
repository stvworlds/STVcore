package net.maksim6opka.STVcore.functions.commandLog;

import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.Config;
import net.maksim6opka.STVcore.utils.DiscordWebhook;
import net.maksim6opka.STVcore.utils.PlayerAvatarUrl;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLog implements PluginFunction, Listener {

    private final JavaPlugin plugin;

    public CommandLog(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        // Player commands already go through PlayerCommandPreprocessEvent
        if (sender instanceof Player) {
            return;
        }

        String source;
        String thumbnail;
        if (sender instanceof ConsoleCommandSender) {
            source = "Консоль :desktop:";
            thumbnail = Config.getString("commandlog.avatars.console");
        } else if (sender instanceof BlockCommandSender blockSender) {
            var location = blockSender.getBlock().getLocation();
            source = "Командний блок [%d, %d, %d] :bricks:".formatted(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            );
            thumbnail = Config.getString("commandlog.avatars.commandBlock");
        } else {
            source = sender.getName() + " :gear:";
            thumbnail = PlayerAvatarUrl.getRandomAvatarUrl();
        }

        logCommand(source, event.getCommand(), thumbnail);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String player = event.getPlayer().getName() + " :grinning:";
        String command = event.getMessage();
        String thumbnail = PlayerAvatarUrl.getAvatarUrl(event.getPlayer());

        logCommand(player, command, thumbnail);
    }

    private void logCommand(String source, String command, String thumbnail) {
        String webhookUrl = Config.getString("commandlog.webhook.url");
        if (webhookUrl == null || webhookUrl.isBlank()
                || webhookUrl.contains("WEBHOOK_ID")
                || webhookUrl.contains("WEBHOOK_TOKEN")) {
            plugin.getLogger().warning("CommandLog: webhook URL is not configured in config.yml");
            return;
        }

        String normalizedCommand = normalizeCommand(command);

        String description = """
                Гравець **%s**.
                Використав команду:
                » `/%s`
                """.formatted(source, normalizedCommand);

        DiscordWebhook.Embed embed = new DiscordWebhook.Embed()
                .title("Логер команд :alien:")
                .description(description)
                .color(0x5865F2)
                .thumbnail(thumbnail);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                new DiscordWebhook(webhookUrl)
                        .username("Command Logger")
                        .addEmbed(embed)
                        .send();
            } catch (Exception e) {
                plugin.getLogger().warning("CommandLog: failed to send webhook: " + e.getMessage());
            }
        });
    }

    private static String normalizeCommand(String command) {
        if (command == null) {
            return "";
        }
        String trimmed = command.trim();
        return trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
    }
}
