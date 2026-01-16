package net.maksim6opka.STVcore.commands;


import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.maksim6opka.STVcore.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class CoreCommand {

    public void register(){

        Main plugin = Main.getPlugin(Main.class);
        FileConfiguration config = plugin.getConfig();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("stvcore")
                            .executes( ctx -> {

                                var sender = ctx.getSource().getSender();

                                String msg = config.getString("messages.system.info", "Private Stevious plugins system by Maksim6opka");
                                sender.sendMessage("§a" + msg);
                                return 1;
                            } )
                            .then(Commands.literal("reload")
                                    .requires(req -> req.getSender().hasPermission("lejo.reload"))
                                    .executes(ctx -> {

                                        var sender = ctx.getSource().getSender();

                                        plugin.reloadConfig();
                                        String msg = config.getString("messages.system.reload", "STVcore reloaded");
                                        sender.sendMessage("§a" + msg);

                                        return 1;
                                    })
                            )
                            .build()
            );
        });
    }
}

