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
import net.maksim6opka.STVcore.utils.formatting.message.Messages;
import net.maksim6opka.STVcore.utils.formatting.playerInfo.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeCommand implements FunCommand {

    MiniMessage mm = MiniMessage.miniMessage();
    String usageMSG = "<gray>/</gray><i>me</i> <gray>[</gray><yellow><</yellow><aqua>message</aqua><yellow>></yellow><gray>]</gray>";
    Component usageText = Component.text("Використання: ").append(mm.deserialize(usageMSG));
    TextColor color = TextColor.color(NamedTextColor.AQUA);


    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("me")
                .executes(this::mainExecute)
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::argumentExecute))
                .build();
    }

    public int mainExecute(CommandContext<CommandSourceStack> ctx) {
        Messages.sendDM(usageText, ctx.getSource().getSender(), "fun", "warn");
        return 1;
    }

    public int argumentExecute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (sender instanceof Player player) {

            String message = ctx.getArgument("message", String.class);

            Component text = Component.empty()
                    .append(Component.text("⏺ ")
                            .color(color)
                            .hoverEvent(HoverEvent.showText(usageText))
                    )
                    .append(Component.text(message)
                            .hoverEvent(HoverEvent.showText(PlayerInfo.copyMsg(color)))
                            .clickEvent(ClickEvent.copyToClipboard(message))

                    )
                    .append(Component.text(" ("))
                    .append(PlayerInfo.playerInfo(player, color))
                    .append(Component.text(")"));


            Bukkit.broadcast(text);

        } else {
            sender.sendMessage(mm.deserialize("<red><bold>конч, в консоль незя"));
        }
        return 1;
    }
}
