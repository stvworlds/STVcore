package net.maksim6opka.STVcore.functions.fun;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface FunCommand {
    LiteralCommandNode<CommandSourceStack> node();
}