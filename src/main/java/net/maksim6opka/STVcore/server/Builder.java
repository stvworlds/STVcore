package net.maksim6opka.STVcore.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.maksim6opka.STVcore.server.services.playerSkin.PlayerSkinUrlServiceImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Builder {
    public static Server build(JavaPlugin plugin) throws IOException {
        int port = plugin.getConfig().getInt("server.gRPC-port");

        Server server = ServerBuilder.forPort(port)
                .addService(new PlayerSkinUrlServiceImpl())
                .build();

        server.start();
        plugin.getLogger().info("gRPC server started on port " + port);
        return server;
    }
}
