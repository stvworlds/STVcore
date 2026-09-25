package net.maksim6opka.STVcore.functions.fun.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.maksim6opka.STVcore.functions.fun.FunCommand;
import net.maksim6opka.STVcore.utils.RateLimiter;
import net.maksim6opka.STVcore.utils.formatting.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class GMeCommand implements FunCommand {

    MiniMessage mm = MiniMessage.miniMessage();
    String usageMSG = "<gray>/</gray><i>gme</i> <gray>[</gray><yellow><</yellow><aqua>повідомлення</aqua><yellow>></yellow><gray>]</gray>";
    Component usageText = Component.text("Використання: ").append(mm.deserialize(usageMSG));
    TextColor color = TextColor.color(NamedTextColor.YELLOW);


    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("gme")
                .executes(this::mainExecute)
                .then(Commands.argument("повідомлення", StringArgumentType.greedyString())
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

            if (!RateLimiter.tryAcquire(player.getUniqueId(), 5, Duration.ofSeconds(10))) {
                Messages.sendDM(mm.deserialize("<red>Ліміт перевищено. Зачекайте пару секунд..."), ctx.getSource().getSender(), "fun", "warn");
                return 1;
            }

            Component text = MeCommand.FormattingMeCommand(ctx, player, color, usageText);


            Bukkit.broadcast(text);

        } else {
            sender.sendMessage(mm.deserialize("<red><bold>конч, в консоль незя"));
        }
        return 1;
    }
}
