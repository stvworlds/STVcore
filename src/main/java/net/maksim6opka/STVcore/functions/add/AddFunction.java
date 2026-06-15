package net.maksim6opka.STVcore.functions.add;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.maksim6opka.STVcore.functions.PluginFunction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class AddFunction implements PluginFunction {

    private final JavaPlugin plugin;

    public AddFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        FileConfiguration config = plugin.getConfig();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("add")
                            .executes(ctx -> {
                                var sender = ctx.getSource().getSender();

                                String msg = config.getString("messages.command.add.info", "Додає одного вказаного гравця до білого списку. Перезавантажується раз в 14 днів");
                                sender.sendMessage("§a" + msg);
                                return 1;
                            })
                            .then(Commands.argument("player", ArgumentTypes.player())
                                    .executes(ctx -> {
                                        var sender = ctx.getSource().getSender();
                                        String msg = config.getString("messages.command.add.added", "Успішно додано");
                                        PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                        Player target = targetResolver.resolve(ctx.getSource()).getFirst();

                                        Bukkit.dispatchCommand(
                                                Bukkit.getConsoleSender(),
                                                "twl add " + target.getName() + " permanent"
                                        );

                                        sender.sendMessage(msg);
                                        return 1;
                                    })
                            )
                            .build()
            );
        });
    }
}
