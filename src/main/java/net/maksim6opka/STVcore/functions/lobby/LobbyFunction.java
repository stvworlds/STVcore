package net.maksim6opka.STVcore.functions.lobby;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.formatting.message.Messages;
import net.maksim6opka.STVcore.utils.TextTypingTask;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Команда {@code /lobby} — еквівалент Denizen {@code LobbyCommand} з misc/123.dsc.
 */
public class LobbyFunction implements PluginFunction {

    private static final String BUNGEE_CHANNEL = "BungeeCord";
    private static final String LOBBY_SERVER = "lobby";
    private static final String TELEPORT_TEXT = "Телепортація до лоббі";

    private final JavaPlugin plugin;

    public LobbyFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("lobby")
                            .requires(stack -> stack.getExecutor() instanceof Player)
                            .executes(ctx -> {
                                Player player = (Player) ctx.getSource().getExecutor();
                                if (player == null) {
                                    Messages.sendDM(
                                            Component.text("Цю команду може використати лише гравець."),
                                            ctx.getSource().getSender(),
                                            "lobby",
                                            "error"
                                    );
                                    return 0;
                                }

                                startLobbySequence(player);
                                return 1;
                            })
                            .build(),
                    "Телепортує до лоббі"
            );
        });
    }

    private void startLobbySequence(Player player) {
        TextTypingTask.run(
                plugin,
                player,
                TELEPORT_TEXT,
                TextTypingTask.Type.WARNING,
                1L,
                () -> runCountdown(player)
        );
    }

    /**
     * Послідовність після typing-task:
     * actionbar + sound, wait 10t (×3 з наростанням крапок), «Готово!» + amethyst, wait 5t, send_to lobby.
     */
    private void runCountdown(Player player) {
        Component prefix = TextTypingTask.prefix(TextTypingTask.Type.WARNING);

        showStep(player, prefix, TELEPORT_TEXT + ".");
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }
            showStep(player, prefix, TELEPORT_TEXT + "..");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }
                showStep(player, prefix, TELEPORT_TEXT + "...");
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    player.sendActionBar(prefix.append(Component.text("Готово!")));
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.85f);
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> connectToLobby(player), 5L);
                }, 10L);
            }, 10L);
        }, 10L);
    }

    private void showStep(Player player, Component prefix, String text) {
        player.sendActionBar(prefix.append(Component.text(text)));
        player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_STOP_USING, 1.0f, 1.85f);
    }

    private void connectToLobby(Player player) {
        if (!player.isOnline()) {
            return;
        }

        // Denizen: adjust <player> send_to:lobby (BungeeCord / Velocity plugin message)
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            out.writeUTF("Connect");
            out.writeUTF(LOBBY_SERVER);
            player.sendPluginMessage(plugin, BUNGEE_CHANNEL, stream.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to send player " + player.getName() + " to lobby: " + e.getMessage());
            Messages.sendDM(
                    Component.text("Не вдалося підключитися до лоббі."),
                    player,
                    "lobby",
                    "error"
            );
        }
    }
}
