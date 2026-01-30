package net.maksim6opka.STVcore;

import net.maksim6opka.STVcore.events.AnvilMending;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginEvents {

    private final JavaPlugin plugin;

    public PluginEvents(JavaPlugin plugin) {
        this.plugin = plugin;
        register();
    }

    public void register(){
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new AnvilMending(), plugin);
    }
}
