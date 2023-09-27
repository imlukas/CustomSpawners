package dev.imlukas.ultraspawners.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BlockBreakListener implements Listener {

    private final UltraSpawnersPlugin plugin;
    private final GeneralSpawnerRegistry spawnerRegistry;

    public BlockBreakListener(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.spawnerRegistry = plugin.getSpawnerRegistry();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        Block block = event.getBlock();
        CustomBlockData blockData = new CustomBlockData(block, plugin);

        if (blockData.has(new NamespacedKey(plugin, "spawner-id"))) {
            event.setCancelled(true);
            UUID spawnerId = UUID.fromString(blockData.get(new NamespacedKey(plugin, "spawner-id"), PersistentDataType.STRING));

            if (itemInHand.getType() != plugin.getPluginSettings().getPickaxeType()) {
                return;
            }

            InstancedSpawner spawner = spawnerRegistry.getSpawner(spawnerId);

            if (spawner == null) {
                System.err.println("[BreakEvent] Spawner with id " + spawnerId + " not found!");
                return;
            }

            ItemStack spawnerItem = getItemStack(spawner, player);

            if (block.getState() instanceof CreatureSpawner) {
                player.getWorld().dropItem(block.getLocation(), spawnerItem);
            }

            if (spawner.getSpawnerData().getStackSize() <= 0) {
                block.setType(Material.AIR);
                spawnerRegistry.removeSpawner(spawner);
            }
        }
    }

    @NotNull
    private static ItemStack getItemStack(InstancedSpawner spawner, Player player) {
        SpawnerData spawnerData = spawner.getSpawnerData();

        int stackToRemove = 1;

        if (player.isSneaking()) {
            stackToRemove = 64;
        }

        int stackSize = spawnerData.getStackSize();

        if (stackSize < stackToRemove) {
            stackToRemove = stackSize;
        }

        int finalStackSize = stackToRemove;


        spawnerData.setStackSize(stackSize - finalStackSize);

        ItemStack spawnerItem = spawner.getSpawnerData().getBlockItem();
        spawnerItem.setAmount(finalStackSize);
        return spawnerItem;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().forEach(block -> {
            CustomBlockData blockData = new CustomBlockData(block, plugin);

            if (blockData.has(new NamespacedKey(plugin, "spawner-id"))) {
                event.blockList().remove(block);
            }
        });
    }
}
