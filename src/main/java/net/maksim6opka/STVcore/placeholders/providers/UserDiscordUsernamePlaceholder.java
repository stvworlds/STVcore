package net.maksim6opka.STVcore.placeholders.providers;

import net.maksim6opka.STVcore.placeholders.StvPlaceholder;
import net.maksim6opka.STVcore.utils.FindUser;
import org.bukkit.entity.Player;

public class UserDiscordUsernamePlaceholder implements StvPlaceholder {

    @Override
    public String getIdentifier() {
        return "user_discord_username";
    }

    @Override
    public String resolve(Player player, FindUser.UserInfo userInfo) {
        String username = userInfo.username();
        return username != null ? username : onNotFound();
    }
}
