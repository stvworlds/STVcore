package net.maksim6opka.STVcore.placeholders.providers;

import net.maksim6opka.STVcore.placeholders.StvPlaceholder;
import net.maksim6opka.STVcore.utils.backend.FindUser;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class UserWorldIconPlaceholder implements StvPlaceholder {

    @Override
    public String getIdentifier() {
        return "user_world_icon";
    }

    @Override
    public String resolve(Player player, FindUser.UserInfo userInfo) {
        String icon = "<gray>*<reset>";

        World.Environment type = player.getWorld().getEnvironment();

        icon = switch (type) {
            case NORMAL -> "<green>☁<reset>";
            case NETHER  -> "<color:#ef532c>\uD83D\uDD25<reset>";
            case THE_END -> "<color:#c57cfc>☄<reset>";
            default -> icon;
        };
        return icon;
    }
}
