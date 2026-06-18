package net.maksim6opka.STVcore;

import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.functions.add.AddFunction;
import net.maksim6opka.STVcore.functions.anvilrepair.AnvilRepairFunction;
import net.maksim6opka.STVcore.functions.core.CoreFunction;
import net.maksim6opka.STVcore.functions.msg.MsgFunction;
import net.maksim6opka.STVcore.functions.suffix.SuffixFunction;
import net.maksim6opka.STVcore.utils.louders.DiscordLouder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public final class Main extends JavaPlugin {

    private DiscordLouder discordLouder;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        List<PluginFunction> functions = List.of(
                new CoreFunction(this),
                new AnvilRepairFunction(this),
                new SuffixFunction(this),
                new AddFunction(this),
                new MsgFunction(this)
        );
        functions.forEach(PluginFunction::register);

        discordLouder = new DiscordLouder(this);
        discordLouder.connect();

        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        if (discordLouder != null) {
            discordLouder.disconnect();
        }
        getLogger().info("Plugin disabled");
    }

    public DiscordLouder getDiscordLouder() {
        return discordLouder;
    }
}

