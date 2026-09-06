package net.maksim6opka.STVcore.placeholders;

import net.maksim6opka.STVcore.utils.FindUser;
import org.bukkit.entity.Player;

public interface StvPlaceholder {

    String getIdentifier();

    String resolve(Player player, FindUser.UserInfo userInfo);

    default String onNotFound() {
        return "error";
    }
}
