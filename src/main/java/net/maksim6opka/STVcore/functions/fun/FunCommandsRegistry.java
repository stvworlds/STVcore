package net.maksim6opka.STVcore.functions.fun;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.functions.fun.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FunCommandsRegistry implements PluginFunction {

    private final JavaPlugin plugin;

    public FunCommandsRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        List<FunCommand> commands = List.of(
                new DoCommand(),
                new MeCommand(),
                new GMeCommand(),
                new RollCommand(),
                new TryCommand()
        );

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            commands.forEach(cmd -> registrar.register(cmd.node()));
        });
    }
}
