package net.maksim6opka.STVcore.functions.msg;

import com.mojang.brigadier.context.CommandContext;
import github.scarsz.discordsrv.DiscordSRV;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.clip.placeholderapi.PlaceholderAPI;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.Messages;
import net.maksim6opka.STVcore.utils.PlayerAvatarUrl;
import net.maksim6opka.STVcore.utils.louders.DiscordLouder;
import net.maksim6opka.STVcore.utils.suggestions.AllPlayersSuggestions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MsgFunction implements PluginFunction {

    private final JavaPlugin plugin;

    public MsgFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static Component playerInfo(OfflinePlayer player, TextColor color) {
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
                        .color(TextColor.fromHexString("#AAAAAA")));

        return Component.text(name).color(color)
                .clickEvent(ClickEvent.suggestCommand(name))
                .hoverEvent(HoverEvent.showText(text));
    }

    private static Component copyMsg(TextColor color) {
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

    private static Component parseMsg(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    private CompletableFuture<String> sendDiscordMsg(String message, User user, CommandSender sender) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String senderName;
        String senderAvatarUrl;
        if (sender instanceof Player) {
            String senderDiscordID = DiscordSRV.getPlugin()
                    .getAccountLinkManager()
                    .getDiscordIdBypassCache(((Player) sender).getUniqueId());
            if (senderDiscordID != null) {
                senderName = "<@" + senderDiscordID + "> (*" + sender.getName() + "*)";
            } else {
                senderName ="**" + sender.getName() + "**";
            }
            senderAvatarUrl = PlayerAvatarUrl.getAvatarUrl((Player) sender);
        } else {
            senderName = "**Консоль**";
            senderAvatarUrl = "https://visage.surgeplay.com/bust/512/f78a4d8dd51b4b3998a3230f2de0c670?y=-20";
        }

        Container container = Container.of(
            Section.of(
                    Thumbnail.fromUrl(senderAvatarUrl),
                    TextDisplay.of(
                            "### 💻 Ви отримали повідомлення!\n" +
                                    "📨 Поки ви були не в мережі " +senderName + ",\n" +
                                    "відправив(-ла) вам **приватне повідомлення**!"
                    )
            ),
            Separator.createDivider(Separator.Spacing.SMALL),
            TextDisplay.of("```\n" + message + "\n```")

        );

        MessageCreateData finalMessage = new MessageCreateBuilder()
                .useComponentsV2()
                .setComponents(container)
                .build();

        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(finalMessage).useComponentsV2(true))
                .queue(
                        success -> future.complete("success"),
                        error -> future.complete("error")
                );

        return future;
    }

    MiniMessage mm = MiniMessage.miniMessage();
    String usageMSG = "<gray>/</gray><i>msg</i> <gray>[</gray><aqua>player</aqua><gray>]</gray> <gray>[</gray><yellow><</yellow><aqua>message</aqua><yellow>></yellow><gray>]</gray>";
    Component usageText = Component.text("Використання ").append(mm.deserialize(usageMSG));

    TextColor nameColor = TextColor.fromHexString("#BCCFE2");
    TextColor selfColor = TextColor.fromHexString("#98D6D3");
    TextColor arrowColor = TextColor.fromHexString("#888888");
    TextColor msgColor = TextColor.fromHexString("#F0F0F0");

    @Override
    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            registrar
                    .register(Commands
                            .literal("msg")
                            .executes(ctx -> {
                                Messages.sendDM(usageText, ctx.getSource().getSender(), "msg", "warn");
                                return 1;
                            })
                            .then(Commands.argument("player", StringArgumentType.word()).suggests(AllPlayersSuggestions.suggestion())
                                    .executes(ctx -> {
                                        Messages.sendDM(usageText, ctx.getSource().getSender(), "msg", "warn");
                                        return 1;
                                        }
                                    )
                                    .then(Commands.argument("message", StringArgumentType.greedyString())
                                            .executes(this::MessageHandler)
                                    )
                            )
                            .build(),
                            "Приватні повідомлення.",
                            List.of("tell", "w", "pm", "t", "message")
                    );
        });
    }

    private int MessageHandler(CommandContext<CommandSourceStack> ctx) {

        CommandSender sender = ctx.getSource().getSender();
        final CommandSender fixedSender = sender;

        String receiverName = StringArgumentType.getString(ctx, "player");
        String message = StringArgumentType.getString(ctx, "message");

        OfflinePlayer offlineReceiver = Bukkit.getOfflinePlayerIfCached(receiverName);

        if (offlineReceiver == null) {
            Messages.sendDM(
                    Component.text("Гравця не знайдено."),
                    fixedSender, "msg", "error"
            );
            return 0;
        }

        Component result = sender.hasPermission("stv.sponsor")
                ? parseMsg(message)
                : Component.text(message);

        if (offlineReceiver.isOnline()) {

            Player receiver = offlineReceiver.getPlayer();
            if (receiver == null) {
                return 0;
            }

            Component senderInfo;
            if (sender instanceof Player player) {
                senderInfo = playerInfo(player, nameColor);
            } else {
                senderInfo = Component.text("консоль").color(nameColor);
            }

            Component receiverInfo = playerInfo(offlineReceiver, nameColor);


            Messages.sendDM(
                    Component.empty()
                            .append(senderInfo)
                            .append(Component
                                    .text(" ⏵ ")
                                    .color(arrowColor)
                            )
                            .append(Component
                                    .text("ви")
                                    .color(nameColor)
                            )
                            .append(Component
                                    .text(": ")
                                    .color(arrowColor)
                            )
                            .append(result
                                .clickEvent(ClickEvent.suggestCommand(message))
                                .hoverEvent(copyMsg(msgColor))
                            ),
                    receiver, "msg", "info"
            );

            Messages.sendDM(
                    Component.empty()
                            .append(Component
                                    .text("ви")
                                    .color(nameColor)
                            )
                            .append(Component
                                    .text(" ⏵ ")
                                    .color(arrowColor)
                            )
                            .append(receiverInfo)
                            .append(Component
                                    .text(": ")
                                    .color(arrowColor)
                            )
                            .append(result
                                .clickEvent(ClickEvent.suggestCommand(message))
                                .hoverEvent(copyMsg(msgColor))
                            ),
                    sender, "msg", "info"
            );

            return 1;
        }

        String receiverDiscordId = DiscordSRV.getPlugin()
                .getAccountLinkManager()
                .getDiscordIdBypassCache(offlineReceiver.getUniqueId());

        if (receiverDiscordId == null) {
            Messages.sendDM(
                    Component.text("Гравець офлайн і не прив'язаний до Discord."),
                    fixedSender, "msg", "error"
            );
            return 0;
        }

        final Component finalResult = result;
        final String fixedMessage = message;
        final Component fixedPlayerInfo = playerInfo(offlineReceiver, nameColor);
        DiscordLouder.getJDA()
                .retrieveUserById(receiverDiscordId)
                .submit()
                .thenCompose(user -> sendDiscordMsg(message, user, fixedSender))
                .thenAccept(status -> {

                    Component statusComponent = switch (status) {
                        case "success" -> Component.text("Повідомлення надіслано в Discord");
                        case "error" -> Component.text("Помилка надсилання в Discord");
                        default -> Component.text("Невідомий статус");
                    };

                    Bukkit.getScheduler().runTask(plugin, () -> Messages.sendDM(
                            Component.empty()
                                    .append(Component
                                            .text("ви")
                                            .color(nameColor)
                                    )
                                    .append(Component
                                            .text(" ⏵ ")
                                            .color(arrowColor)
                                    )
                                    .append(fixedPlayerInfo)
                                    .append(Component
                                            .text(" (офлайн)")
                                            .color(selfColor)
                                            .hoverEvent(statusComponent)
                                    )
                                    .append(Component
                                            .text(": ")
                                            .color(arrowColor)
                                    )
                                    .append(finalResult
                                            .clickEvent(ClickEvent.suggestCommand(fixedMessage))
                                            .hoverEvent(copyMsg(msgColor))
                                    ),
                            fixedSender, "msg", "info"
                    ));
                });
        return 1;
    }
}
