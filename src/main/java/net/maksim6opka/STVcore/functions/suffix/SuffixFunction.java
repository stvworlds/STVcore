package net.maksim6opka.STVcore.functions.suffix;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.SuffixNode;
import net.maksim6opka.STVcore.functions.PluginFunction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class SuffixFunction implements PluginFunction {

    private final JavaPlugin plugin;

    public SuffixFunction(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        FileConfiguration config = plugin.getConfig();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("suffix")
                            .requires(req -> req.getSender().hasPermission(config.getString("commands.suffix.permission", "stv.t-admin")))
                            .executes(ctx -> {
                                String msg = config.getString("commands.suffix.messages.info", "Usage /suffix set/remove");
                                ctx.getSource().getSender().sendMessage("§a" + msg);
                                return 1;
                            })
                            .then(Commands.literal("set")
                                    .executes(ctx -> {
                                                String msg = config.getString("commands.suffix.messages.info", "Usage /suffix set [player] [suffix]");
                                                ctx.getSource().getSender().sendMessage("§a" + msg);
                                                return 1;
                                            }
                                    )
                                    .then(Commands.argument("player", ArgumentTypes.players())
                                            .executes(ctx -> {
                                                        String msg = config.getString("commands.suffix.messages.info", "Usage /suffix set [player] [suffix]");
                                                        ctx.getSource().getSender().sendMessage("§a" + msg);
                                                        return 1;
                                                    }
                                            )
                                            .then(Commands.argument("suffix", StringArgumentType.greedyString())

                                                    .executes(ctx -> {
                                                        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                                        final List<Player> targets = targetResolver.resolve(ctx.getSource());
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");

                                                        for (final Player target : targets) {
                                                            try {
                                                                setSuffix(suffix, target.getUniqueId());
                                                            } catch (Exception e) {
                                                                return 0;
                                                            }
                                                        }
                                                        return 1;

                                                    })
                                            )
                                    )
                            )
                            .then(Commands.literal("remove")
                                    .executes(ctx -> {
                                                String msg = config.getString("commands.suffix.messages.info", "Usage /suffix remove [player]");
                                                ctx.getSource().getSender().sendMessage("§a" + msg);
                                                return 1;
                                            }
                                    )
                                    .then(Commands.argument("player", ArgumentTypes.players())
                                            .executes(ctx -> {
                                                final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                                final List<Player> targets = targetResolver.resolve(ctx.getSource());
                                                String suffix = "";
                                                final CommandSender sender = ctx.getSource().getSender();

                                                for (final Player target : targets) {
                                                    try {

                                                        OfflinePlayer player = Bukkit.getOfflinePlayer(target.getName());

                                                        setSuffix(suffix, player.getUniqueId());
                                                        sender.sendMessage("Успішно видалено суфікс гравцю " + target.getName());
                                                        target.sendMessage("Ваш суфікс видалено!");
                                                    } catch (Exception e) {
                                                        sender.sendMessage(Component.text("§cВиникла проблема під час виконання команди, натисніть, щоб скопіювати.")
                                                                .clickEvent(ClickEvent.suggestCommand("Помилка : " + e)));
                                                        return 1;
                                                    }
                                                }
                                                return 1;
                                            })
                                    )
                            )
                            .then(Commands.literal("test")
                                    .then(Commands.argument("player", ArgumentTypes.player())
                                            .executes(ctx -> {
                                                        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                                        final Player target = targetResolver.resolve(ctx.getSource()).getFirst();

                                                        ctx.getSource().getSender().sendMessage(target.getName() + " " + target.getUniqueId());
                                                        setSuffix("", target.getUniqueId());

                                                        return 1;
                                                    }
                                            )
                                    )
                            )
                            .build()
            );
        });
    }

    private void setSuffix(String suffix, UUID player) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();

            User user = api.getUserManager().getUser(player);

            if (user != null) {
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
    }
}
