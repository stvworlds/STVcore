package net.maksim6opka.STVcore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.maksim6opka.STVcore.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;


public class SuffixCommand {
    public void register(){

        Main plugin = Main.getPlugin(Main.class);
        FileConfiguration config = plugin.getConfig();

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();

            registrar.register(
                    Commands.literal("suffix")
                            .requires(req -> req.getSender().hasPermission(config.getString("commands.suffix.permission", "stv.t-admin")))
                            .executes( ctx -> {

                                String msg = config.getString("commands.suffix.messages.info", "Usage /suffix set/remove");
                                ctx.getSource().getSender().sendMessage("§a" + msg);
                                return 1;

                            } )
                            .then(Commands.literal("set")
                                    .executes(ctx -> {
                                                String msg = config.getString("commands.suffix.messages.info", "Usage /suffix set [player]");
                                                ctx.getSource().getSender().sendMessage("&a" + msg);
                                                return 1;
                                            }
                                    )
                                    .then(Commands.argument("player", ArgumentTypes.players()

                                                    )
                                            .then(Commands.argument("suffix", StringArgumentType.greedyString()
                                            )
                                            .executes(ctx -> {
                                                        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                                        final List<Player> targets = targetResolver.resolve(ctx.getSource());
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        final CommandSender sender = ctx.getSource().getSender();

                                                        for (final Player target : targets) {
                                                            LuckPerms lp = LuckPermsProvider.get();

                                                            UUID uuid = Bukkit.getOfflinePlayer(target.getName()).getUniqueId();
                                                            User user = lp.getUserManager().loadUser(uuid).join();

                                                            // Видалити старі suffix
                                                            user.data().clear(node ->
                                                                    node instanceof MetaNode meta && meta.getMetaKey().equals("suffix")
                                                            );

                                                            // Додати новий
                                                            MetaNode suffixNode = MetaNode.builder("suffix", " " + suffix).build();
                                                            user.data().add(suffixNode);

                                                            // Зберегти
                                                            lp.getUserManager().saveUser(user);
                                                        }
                                                        return 1;
                                                    }
                                            ))
                                    )
                            )
                            .then(Commands.literal("remove")
                                    .executes(ctx -> {
                                                String msg = config.getString("commands.suffix.messages.info", "Usage /suffix remove [player]");
                                                ctx.getSource().getSender().sendMessage("&a" + msg);
                                                return 1;
                                            }

                                    )
                            )


                            .build()
            );
        });
    }
}
