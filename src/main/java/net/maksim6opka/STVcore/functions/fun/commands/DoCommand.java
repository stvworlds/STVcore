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

public class DoCommand implements FunCommand {

    MiniMessage mm = MiniMessage.miniMessage();
    String usageMSG = "<gray>/</gray><i>do</i> <gray>[</gray><yellow><</yellow><aqua>повідомлення</aqua><yellow>></yellow><gray>]</gray>";
    Component usageText = Component.text("Використання: ").append(mm.deserialize(usageMSG));

    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("do")
                .executes(this::mainExecute)
                .then(Commands.argument("повідомлення", StringArgumentType.greedyString())
                        .executes(this::argumentExecute))
                .build();
    }

    public int mainExecute(CommandContext<CommandSourceStack> ctx) {
        Messages.sendDM(usageText, ctx.getSource().getSender(), "msg", "warn");
        return 1;
    }

    public int argumentExecute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (sender instanceof Player player) {

            if (!RateLimiter.tryAcquire(player.getUniqueId(), 5, Duration.ofSeconds(1))) {
                Messages.sendDM(mm.deserialize("<red>Ліміт перевищено. Зачекайте пару секунд..."), ctx.getSource().getSender(), "fun", "warn");
                return 1;
            }

            String message = ctx.getArgument("повідомлення", String.class);

            Component text = Component.empty()
                    .append(Component.text("⏺ ")
                            .color(TextColor.color(NamedTextColor.GOLD))
                            .hoverEvent(HoverEvent.showText(usageText))
                    )
                    .append(Component.text(message)
                            .hoverEvent(HoverEvent.showText(PlayerInfo.copyMsg(TextColor.color(NamedTextColor.GOLD))))
                            .clickEvent(ClickEvent.copyToClipboard(message))

                    )
                    .append(Component.text(" ("))
                    .append(PlayerInfo.playerInfo(player, TextColor.color(NamedTextColor.GOLD)))
                    .append(Component.text(")"));


            for (var entity : player.getNearbyEntities(75, 30, 75)) {
                if (entity instanceof Player nearbyPlayer) {
                    nearbyPlayer.sendMessage(text);
                }

            }
            player.sendMessage(text);

        } else {
            sender.sendMessage(mm.deserialize("<red><bold>конч, в консоль незя"));
        }
        return 1;
    }
}
