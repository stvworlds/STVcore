package net.maksim6opka.STVcore.functions.whitelist;

import net.kyori.adventure.text.Component;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.CheckPermit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("whitelist.enabled", false)){
            return;
        }
        Player player = event.getPlayer();

        if (!CheckPermit.checkPermit(player)) {
            player.kick(Component.text("Ви не гравець! \n\nСтаньте гравцем на https://stvworlds.win,\nабо якщо вважаєте це за помилку зверніться до адміністрації."));
        }
    }

}
