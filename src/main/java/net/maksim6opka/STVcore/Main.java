package net.maksim6opka.STVcore;

import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.functions.add.AddFunction;
import net.maksim6opka.STVcore.functions.anvilrepair.AnvilRepairFunction;
import net.maksim6opka.STVcore.functions.core.CoreFunction;
import net.maksim6opka.STVcore.functions.suffix.SuffixFunction;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public final class Main extends JavaPlugin {


    @Override
    public void onEnable() {


        saveDefaultConfig();

        List<PluginFunction> functions = List.of(
                new CoreFunction(this),
                new AnvilRepairFunction(this),
                new SuffixFunction(this),
                new AddFunction(this)
        );
        functions.forEach(PluginFunction::register);

        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }
}
