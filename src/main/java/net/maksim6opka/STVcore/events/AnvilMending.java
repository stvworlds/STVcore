package net.maksim6opka.STVcore.events;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class AnvilMending implements Listener {

    private final HashMap<UUID, Long> rateLimit = new HashMap<>();

    @EventHandler
    public void onAnvilMending(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.IRON_BLOCK) return;

        Material type = block.getType();
        if (type != Material.CHIPPED_ANVIL &&
            type != Material.DAMAGED_ANVIL) return;

        if (player.isSneaking()) return;

        long now = System.currentTimeMillis();
        long last = rateLimit.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < 200) return;
        rateLimit.put(player.getUniqueId(), now);

        event.setCancelled(true);

        block.getWorld().playSound(
                block.getLocation(),
                Sound.ENTITY_IRON_GOLEM_DAMAGE,
                1f,
                1f
        );

        block.getWorld().spawnParticle(
                Particle.ELECTRIC_SPARK,
                block.getLocation().add(0.5, 1.1, 0.5),
                25,
                0.2, 0.2, 0.2,
                0.05
        );


        BlockData data = block.getBlockData();
        if (!(data instanceof Directional directional)) return;
        BlockFace face = directional.getFacing();

        item.setAmount(item.getAmount() - 1);

        Material newType;
        if (type == Material.DAMAGED_ANVIL) {
            newType = Material.CHIPPED_ANVIL;
        } else {
            newType = Material.ANVIL;
        }

        block.setType(newType);

        BlockData newData = block.getBlockData();
        if (newData instanceof  Directional newDir) {
            newDir.setFacing(face);
            block.setBlockData(newDir);
        }
    }
}
