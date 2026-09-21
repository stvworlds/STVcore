package net.maksim6opka.STVcore.functions.whitelist;

import net.kyori.adventure.text.Component;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.backend.CheckPermit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public class WhiteListFunction implements PluginFunction, Listener {

    private final JavaPlugin plugin;

    public WhiteListFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getConfig().getBoolean("whitelist.enabled", false)){
            return;
        }
        UUID player = Objects.requireNonNull(event.getPlayerProfile().getId());

        if (!CheckPermit.checkPermit(player)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    Component.text("Ви не гравець! \n\nСтаньте гравцем на https://stvworlds.win,\nабо якщо вважаєте це за помилку зверніться до адміністрації."));
        }
    }

}
