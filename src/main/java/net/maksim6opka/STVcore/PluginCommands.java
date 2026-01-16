package net.maksim6opka.STVcore;

import net.maksim6opka.STVcore.commands.CoreCommand;
import net.maksim6opka.STVcore.commands.SuffixCommand;

public class PluginCommands{



    public static void registerCommands() {

        new CoreCommand().register();
        new SuffixCommand().register();


    }
}
