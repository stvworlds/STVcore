package net.maksim6opka.STVcore.server.services.playerSkin;

import io.grpc.stub.StreamObserver;
import net.maksim6opka.STVcore.utils.formatting.playerInfo.PlayerAvatarUrl;
import net.maksim6opka.grpc.PlayerService;
import net.maksim6opka.grpc.PlayerSkinUrlServiceGrpc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerSkinUrlServiceImpl extends PlayerSkinUrlServiceGrpc.PlayerSkinUrlServiceImplBase {

    @Override
    public void playerSkinUrl(PlayerService.PlayerSkinUrlRequest request, StreamObserver<PlayerService.PlayerSkinUrlResponse> responseObserver) {
        String status;
        String url = PlayerAvatarUrl.getRandomSkinUrl();

        try {
            UUID uuid = UUID.fromString(request.getUuid());
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                url = PlayerAvatarUrl.getSkinUrl(player);
            } else {
                url = PlayerAvatarUrl.getSkinUrl(uuid);
            }
            status = "Ok";
        } catch (IllegalArgumentException e) {
            status = "Error";
        }


        PlayerService.PlayerSkinUrlResponse response = PlayerService.PlayerSkinUrlResponse.newBuilder()
                .setStatus(status)
                .setUrl(url)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        //super.playerSkinUrl(request, responseObserver);
    }
}
