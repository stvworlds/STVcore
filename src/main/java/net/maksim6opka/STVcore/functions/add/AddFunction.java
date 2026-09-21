package net.maksim6opka.STVcore.functions.add;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.formatting.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Deprecated(since = "30.08.2026")
public class AddFunction implements PluginFunction {

    private final JavaPlugin plugin;

    public AddFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("add")
                            .executes(ctx -> {
                                var sender = ctx.getSource().getSender();
                                String msg = plugin.getConfig().getString("messages.command.add.info", "Додає одного вказаного гравця до білого списку. Перезавантажується раз в 14 днів");
                                Messages.sendDM(Component.text(msg), sender, "add", "info");
                                return 1;
                            })
                            .then(Commands.argument("player", StringArgumentType.word())
                                    .executes(ctx -> {
                                        var sender = ctx.getSource().getSender();
                                        String msg = plugin.getConfig().getString("messages.command.add.added", "Ви додали гравця ");

                                        String username = ctx.getArgument("player", String.class);

                                        Bukkit.dispatchCommand(
                                                Bukkit.getConsoleSender(),
                                                "twl add " + username + " permanent"
                                        );

                                        Messages.sendDM(Component
                                                .text(msg)
                                                .append(Component.text(username))
                                                        .color(TextColor.color(123)),
                                                sender, "add", "info"
                                        );
                                        return 1;
                                    })
                            )
                            .build()
            );
        });
    }
}
