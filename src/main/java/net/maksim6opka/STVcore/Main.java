package net.maksim6opka.STVcore;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin {


    @Override
    public void onEnable() {



        PluginManager pm = getServer().getPluginManager();

        saveDefaultConfig();

        try {
            PluginCommands.registerCommands();
            getLogger().info("Commands registered");
        } catch (Exception e) {
            getLogger().warning("Problem with commands registration" + e.getMessage());
        }


        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }
}
