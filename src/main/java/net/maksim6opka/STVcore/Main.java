package net.maksim6opka.STVcore;

import io.grpc.Server;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.functions.add.AddFunction;
import net.maksim6opka.STVcore.functions.anvilrepair.AnvilRepairFunction;
import net.maksim6opka.STVcore.functions.commandLog.CommandLog;
import net.maksim6opka.STVcore.functions.core.CoreFunction;
import net.maksim6opka.STVcore.functions.msg.MsgFunction;
import net.maksim6opka.STVcore.functions.suffix.SuffixFunction;
import net.maksim6opka.STVcore.functions.whitelist.WhiteListFunction;
import net.maksim6opka.STVcore.server.Builder;
import net.maksim6opka.STVcore.utils.Config;
import net.maksim6opka.STVcore.utils.louders.DiscordLouder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;


public final class Main extends JavaPlugin {

    private DiscordLouder discordLouder;
    private Server grpcServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config.init(this);


        List<PluginFunction> functions = List.of(
                new CoreFunction(this),
                new AnvilRepairFunction(this),
                new SuffixFunction(this),
                new AddFunction(this),
                new WhiteListFunction(this),
                new MsgFunction(this),
                new CommandLog(this)
        );
        functions.forEach(PluginFunction::register);

        discordLouder = new DiscordLouder(this);
        discordLouder.connect();

        try {
            grpcServer = Builder.build(this);
        } catch (IOException e) {
            getLogger().warning("Failed to build gRPC server: " + e.getMessage());
            throw new RuntimeException(e);
        }

        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        if (grpcServer != null) {
            grpcServer.shutdownNow();
            grpcServer = null;
        }
        if (discordLouder != null) {
            discordLouder.disconnect();
        }
        getLogger().info("Plugin disabled");
    }

    public DiscordLouder getDiscordLouder() {
        return discordLouder;
    }
}
