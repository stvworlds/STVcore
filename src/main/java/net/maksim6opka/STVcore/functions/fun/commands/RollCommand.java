package net.maksim6opka.STVcore.functions.fun.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.maksim6opka.STVcore.functions.fun.FunCommand;
// import net.maksim6opka.STVcore.utils.RateLimiter;
import net.maksim6opka.STVcore.utils.formatting.message.Messages;

// import java.time.Duration;

// ⚀|⚁|⚂|⚃|⚄|⚅

public class RollCommand implements FunCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("roll")
                .executes(this::mainExecute)
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::argumentExecute))
                .build();
    }

    public int mainExecute(CommandContext<CommandSourceStack> ctx) {
        Component text = Component.text("Roll");
        Messages.sendDM(text, ctx.getSource().getSender(), "fun", "info");
        return 1;
    }

    public int argumentExecute(CommandContext<CommandSourceStack> ctx) {
        String message = ctx.getArgument("message", String.class);
        /*
        if (!RateLimiter.tryAcquire(player.getUniqueId(), 5, Duration.ofSeconds(1))) {
            Messages.sendDM(mm.deserialize("<red>Ліміт перевищено. Зачекайте пару секунд..."), ctx.getSource().getSender(), "fun", "warn");
            return 1;
        }
        */
        Component text = Component.text("Roll" + message);
        Messages.sendDM(text, ctx.getSource().getSender(), "fun", "info");
        return 1;
    }
}
