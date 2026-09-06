package net.maksim6opka.STVcore;

import io.grpc.Server;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.functions.anvilrepair.AnvilRepairFunction;
import net.maksim6opka.STVcore.functions.commandLog.CommandLog;
import net.maksim6opka.STVcore.functions.core.CoreFunction;
import net.maksim6opka.STVcore.functions.lobby.LobbyFunction;
import net.maksim6opka.STVcore.functions.msg.MsgFunction;
import net.maksim6opka.STVcore.functions.suffix.SuffixFunction;
import net.maksim6opka.STVcore.functions.whitelist.WhiteListFunction;
import net.maksim6opka.STVcore.placeholders.StvExpansion;
import net.maksim6opka.STVcore.placeholders.providers.UserDiscordUsernamePlaceholder;
import net.maksim6opka.STVcore.server.Builder;
import net.maksim6opka.STVcore.utils.Config;
import net.maksim6opka.STVcore.utils.louders.DiscordLouder;
import org.bukkit.Bukkit;
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
                new WhiteListFunction(this),
                new MsgFunction(this),
                new CommandLog(this),
                new LobbyFunction(this)
        );
        functions.forEach(PluginFunction::register);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            StvExpansion expansion = new StvExpansion(this, List.of(
                    new UserDiscordUsernamePlaceholder()
            ));
            boolean registered = expansion.register();
            getLogger().info("PlaceholderAPI expansion registered: " + registered);
            getServer().getPluginManager().registerEvents(expansion, this);
        }

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
