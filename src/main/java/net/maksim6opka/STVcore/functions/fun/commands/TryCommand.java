package net.maksim6opka.STVcore.functions.fun.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.maksim6opka.STVcore.functions.fun.FunCommand;
import net.maksim6opka.STVcore.utils.RateLimiter;
import net.maksim6opka.STVcore.utils.formatting.message.Messages;
import net.maksim6opka.STVcore.utils.formatting.playerInfo.PlayerInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class TryCommand implements FunCommand {

    MiniMessage mm = MiniMessage.miniMessage();
    String usageMSG = "<gray>/</gray><i>try</i> <gray>[</gray><yellow><</yellow><aqua>повідомлення</aqua><yellow>></yellow><gray>]</gray>";
    Component usageText = Component.text("Використання: ").append(mm.deserialize(usageMSG));
    TextColor color = NamedTextColor.LIGHT_PURPLE;

    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("try")
                .executes(this::mainExecute)
                .then(Commands.argument("повідомлення", StringArgumentType.greedyString())
                        .executes(this::argumentExecute))
                .build();
    }

    public int mainExecute(CommandContext<CommandSourceStack> ctx) {
        Messages.sendDM(usageText, ctx.getSource().getSender(), "fun", "info");
        return 1;
    }

    public int argumentExecute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize("<red>Цю команду може використовувати тільки гравець."));
            return 1;
        }

        if (!RateLimiter.tryAcquire(player.getUniqueId(), 5, Duration.ofSeconds(1))) {
            Messages.sendDM(
                    mm.deserialize("<red>Ліміт перевищено. Зачекайте пару секунд..."),
                    sender,
                    "fun",
                    "warn"
            );
            return 1;
        }

        Component text = formattingTryCommand(ctx, player, color, usageText);

        sendToNearbyPlayers(player, text);

        return 1;
    }

    public static Component formattingTryCommand(
            CommandContext<CommandSourceStack> ctx,
            Player player,
            TextColor color,
            Component usageText
    ) {
        String message = ctx.getArgument("повідомлення", String.class);

        boolean success = Math.random() < 0.5;

        Component result = success
                ? Component.text("[успішно]").color(NamedTextColor.GREEN)
                : Component.text("[невдало]").color(NamedTextColor.RED);

        Component prefix = Component.text("⏺ ")
                .color(color)
                .hoverEvent(HoverEvent.showText(usageText))
                .clickEvent(ClickEvent.suggestCommand("/try повідомлення"));

        Component messagePart = Component.text(message)
                .hoverEvent(HoverEvent.showText(PlayerInfo.copyMsg(color)))
                .clickEvent(ClickEvent.copyToClipboard(message));

        Component bep1 = Component.text(" | ")
                .color(color);

        Component bep2 = Component.text(" ⏸ ")
                .color(color);

        Component playerInfoPart = Component.empty()
                .append(PlayerInfo.playerInfo(player, TextColor.fromHexString("#CBF3F5")));

        return Component.empty()
                .append(prefix)
                .append(playerInfoPart)
                .append(bep1)
                .append(messagePart)
                .append(bep2)
                .append(result);
    }

    private void sendToNearbyPlayers(Player player, Component text) {
        for (var entity : player.getNearbyEntities(75, 30, 75)) {
            if (entity instanceof Player nearbyPlayer) {
                nearbyPlayer.sendMessage(text);
            }
        }

        player.sendMessage(text);
    }
}
