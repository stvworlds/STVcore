package net.maksim6opka.STVcore.server.services.playerSkin;

import io.grpc.stub.StreamObserver;
import net.maksim6opka.STVcore.utils.PlayerAvatarUrl;
import net.maksim6opka.grpc.PlayerService;
import net.maksim6opka.grpc.PlayerSkinUrlServiceGrpc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerSkinUrlServiceImpl extends PlayerSkinUrlServiceGrpc.PlayerSkinUrlServiceImplBase {

    @Override
    public void playerSkinUrl(PlayerService.PlayerSkinUrlRequest request, StreamObserver<PlayerService.PlayerSkinUrlResponse> responseObserver) {
        String status;
        String url = PlayerAvatarUrl.getRandomSkinUrl();

        Player player = Bukkit.getOfflinePlayer(request.getUuid()).getPlayer();
        if(player == null) {
            status = "Error";
        } else {
            status = "Ok";
            url = PlayerAvatarUrl.getSkinUrl(player);
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
