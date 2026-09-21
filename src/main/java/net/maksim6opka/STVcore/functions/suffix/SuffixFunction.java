package net.maksim6opka.STVcore.functions.suffix;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.maksim6opka.STVcore.functions.PluginFunction;
import net.maksim6opka.STVcore.utils.formatting.message.Messages;
import net.maksim6opka.STVcore.utils.suggestions.AllPlayersSuggestions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SuffixFunction implements PluginFunction {

    private final JavaPlugin plugin;

    public SuffixFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("suffix")
                            .requires(req -> req.getSender().hasPermission("stv.admin"))
                            .executes(ctx -> {
                                Component text = Component.text(plugin.getConfig().getString("commands.suffix.messages.info", "Usage /suffix set/remove/info"));
                                Messages.sendDM(text, ctx.getSource().getSender(), "suffix", "info");
                                return 1;
                            })
                            .then(Commands.literal("set")
                                    .executes(ctx -> {
                                        Component text = Component.text(plugin.getConfig().getString("commands.suffix.messages.info.set", "Usage /suffix set [player] [suffix]"));
                                        Messages.sendDM(text, ctx.getSource().getSender(), "suffix", "warn");
                                        return 1;
                                    })
                                    .then(Commands.argument("player", StringArgumentType.word()).suggests(AllPlayersSuggestions.suggestion())
                                            .executes(ctx -> {
                                                Component text = Component.text(plugin.getConfig().getString("commands.suffix.messages.info.set", "Usage /suffix set [player] [suffix]"));
                                                Messages.sendDM(text, ctx.getSource().getSender(), "suffix", "warn");
                                                return 1;
                                            })
                                            .then(Commands.argument("suffix", StringArgumentType.greedyString())
                                                    .executes(ctx -> {
                                                        String playerName = StringArgumentType.getString(ctx, "player");
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        final CommandSender sender = ctx.getSource().getSender();

                                                        LuckPerms api = getLuckPerms(sender);
                                                        if (api == null) return 0;

                                                        UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();

                                                        api.getUserManager().loadUser(uuid).thenAccept(user -> {
                                                            if (user == null) {
                                                                Messages.sendDM(Component.text("Гравця " + playerName + " не знайдено"), sender, "suffix", "error");
                                                                return;
                                                            }

                                                            applySuffix(suffix, user, api);

                                                            Component parsed = MiniMessage.miniMessage().deserialize(suffix);
                                                            Messages.sendDM(Component
                                                                    .text("Успішно встановлено суфікс гравцю " + playerName + " | ")
                                                                    .append(parsed)
                                                                    .clickEvent(ClickEvent.suggestCommand(suffix))
                                                                    .hoverEvent(HoverEvent.showText(Component.text("Натисніть, щоб скопіювати суфікс"))),
                                                                    sender, "suffix", "info");

                                                            Player onlineTarget = Bukkit.getPlayer(uuid);
                                                            if (onlineTarget != null) {
                                                                Messages.sendDM(Component
                                                                        .text("Ваш суфікс оновлено! | ")
                                                                        .append(parsed)
                                                                        .clickEvent(ClickEvent.suggestCommand(suffix))
                                                                        .hoverEvent(HoverEvent.showText(Component.text("Натисніть, щоб скопіювати суфікс"))),
                                                                        onlineTarget, "suffix", "info");
                                                            }
                                                        }).exceptionally(e -> {
                                                            Component errorText = Component.text("Виникла проблема під час виконання команди, натисніть, щоб скопіювати.")
                                                                    .clickEvent(ClickEvent.suggestCommand("Помилка : " + e));
                                                            Messages.sendDM(errorText, sender, "suffix", "error");
                                                            return null;
                                                        });

                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(Commands.literal("remove")
                                    .executes(ctx -> {
                                        Component text = Component.text(plugin.getConfig().getString("commands.suffix.messages.info.remove", "Usage /suffix remove [player]"));
                                        Messages.sendDM(text, ctx.getSource().getSender(), "suffix", "warn");
                                        return 1;
                                    })
                                    .then(Commands.argument("player", StringArgumentType.word()).suggests(AllPlayersSuggestions.suggestion())
                                            .executes(ctx -> {
                                                String playerName = StringArgumentType.getString(ctx, "player");
                                                final CommandSender sender = ctx.getSource().getSender();

                                                LuckPerms api = getLuckPerms(sender);
                                                if (api == null) return 0;

                                                UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();

                                                api.getUserManager().loadUser(uuid).thenAccept(user -> {
                                                    if (user == null) {
                                                        Messages.sendDM(Component.text("Гравця " + playerName + " не знайдено"), sender, "suffix", "error");
                                                        return;
                                                    }

                                                    applySuffix("", user, api);

                                                    Messages.sendDM(Component.text("Успішно видалено суфікс гравцю " + playerName), sender, "suffix", "info");

                                                    Player onlineTarget = Bukkit.getPlayer(uuid);
                                                    if (onlineTarget != null) {
                                                        Messages.sendDM(Component.text("Ваш суфікс видалено!"), onlineTarget, "suffix", "info");
                                                    }
                                                }).exceptionally(e -> {
                                                    Component errorText = Component.text("Виникла проблема під час виконання команди, натисніть, щоб скопіювати.")
                                                            .clickEvent(ClickEvent.suggestCommand("Помилка : " + e));
                                                    Messages.sendDM(errorText, sender, "suffix", "error");
                                                    return null;
                                                });

                                                return 1;
                                            })
                                    )
                            )
                            .then(Commands.literal("info")
                                    .executes(ctx -> {
                                        Component text = Component.text(plugin.getConfig().getString("commands.suffix.messages.info.info", "Usage /suffix info [player]"));
                                        Messages.sendDM(text, ctx.getSource().getSender(), "suffix", "warn");
                                        return 1;
                                    })
                                    .then(Commands.argument("player", StringArgumentType.word()).suggests(AllPlayersSuggestions.suggestion())
                                            .executes(ctx -> {
                                                String playerName = StringArgumentType.getString(ctx, "player");
                                                final CommandSender sender = ctx.getSource().getSender();

                                                LuckPerms api = getLuckPerms(sender);
                                                if (api == null) return 0;

                                                UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();

                                                api.getUserManager().loadUser(uuid).thenAccept(user -> {
                                                    if (user == null) {
                                                        Messages.sendDM(Component.text("Гравця " + playerName + " не знайдено"), sender, "suffix", "error");
                                                        return;
                                                    }

                                                    String suffix = user.getCachedData()
                                                            .getMetaData(QueryOptions.nonContextual())
                                                            .getSuffix();

                                                    if (suffix == null || suffix.isBlank()) {
                                                        Messages.sendDM(Component.text("Гравець " + playerName + " не має суфіксу"), sender, "suffix", "info");
                                                    } else {
                                                        Component suffixComponent = MiniMessage.miniMessage().deserialize(suffix.trim());
                                                        Messages.sendDM(
                                                                Component
                                                                        .text("Суфікс гравця " + playerName + ": ")
                                                                        .append(suffixComponent)
                                                                        .clickEvent(ClickEvent.suggestCommand(suffix))
                                                                        .hoverEvent(HoverEvent.showText(Component.text("Натисніть, щоб скопіювати суфікс"))),
                                                                sender, "suffix", "info"
                                                        );
                                                    }
                                                });

                                                return 1;
                                            })
                                    )
                            )
                            .build()
            );
        });
    }



    private LuckPerms getLuckPerms(CommandSender sender) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            Messages.sendDM(Component.text("LuckPerms недоступний"), sender, "suffix", "error");
            return null;
        }
        return provider.getProvider();
    }

    private void applySuffix(String suffix, User user, LuckPerms api) {
        user.data().clear(NodeType.SUFFIX::matches);
        if (!suffix.isBlank()) {
            SuffixNode suffixNode = SuffixNode.builder()
                    .suffix(" " + suffix)
                    .priority(1)
                    .build();
            user.data().add(suffixNode);
        }
        api.getUserManager().saveUser(user);
    }
}
