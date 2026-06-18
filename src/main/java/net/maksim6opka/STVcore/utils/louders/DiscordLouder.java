package net.maksim6opka.STVcore.utils.louders;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordLouder extends ListenerAdapter {

    private static JDA jda;
    private final JavaPlugin plugin;

    public DiscordLouder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        String token = plugin.getConfig().getString("discord.token");
        if (token == null || token.equals("YOUR_BOT_TOKEN_HERE")) {
            plugin.getLogger().warning("Discord token not set in config.yml");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(this)
                    .build();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to Discord: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
    }

    public static JDA getJDA() {
        return jda;
    }

    @Override
    public void onReady(ReadyEvent event) {
        plugin.getLogger().info("Discord bot connected as " + event.getJDA().getSelfUser().getAsTag());
    }
}
