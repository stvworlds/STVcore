package net.maksim6opka.STVcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.maksim6opka.STVcore.Main;
import net.maksim6opka.STVcore.utils.Config;
import net.maksim6opka.STVcore.utils.backend.FindUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StvExpansion extends PlaceholderExpansion implements Listener {

    private final Main plugin;
    private final Map<String, StvPlaceholder> providers;
    private final Map<UUID, CompletableFuture<Optional<FindUser.UserInfo>>> cache = new ConcurrentHashMap<>();

    public StvExpansion(Main plugin, List<StvPlaceholder> placeholders) {
        this.plugin = plugin;
        this.providers = placeholders.stream()
                .collect(Collectors.toMap(p -> p.getIdentifier().toLowerCase(), p -> p));
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "stv";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!Config.getBoolean("placeholders.prefetch", true)) {
            return;
        }
        fetchOrGet(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cache.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "error";
        }

        StvPlaceholder placeholder = providers.get(identifier.toLowerCase());
        if (placeholder == null) {
            return null;
        }

        CompletableFuture<Optional<FindUser.UserInfo>> future = fetchOrGet(player.getUniqueId());
        if (!future.isDone()) {
            return "...";
        }

        return future.getNow(Optional.empty())
                .map(info -> placeholder.resolve(player, info))
                .orElse(placeholder.onNotFound());
    }

    private CompletableFuture<Optional<FindUser.UserInfo>> fetchOrGet(UUID uuid) {
        return cache.computeIfAbsent(uuid, FindUser::findByMcUuidAsync);
    }
}
