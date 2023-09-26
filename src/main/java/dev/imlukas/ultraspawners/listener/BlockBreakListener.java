package dev.imlukas.ultraspawners.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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

            if (itemInHand.getType().isAir()) {
                event.setCancelled(true);
                return;
            }

            UUID spawnerId = UUID.fromString(blockData.get(new NamespacedKey(plugin, "spawner-id"), PersistentDataType.STRING));

            if (itemInHand.getType() != plugin.getPluginSettings().getPickaxeType()) {
                event.setCancelled(true);
                return;
            }

            InstancedSpawner spawner = spawnerRegistry.get(spawnerId);

            if (spawner == null) {
                return;
            }

            SpawnerData spawnerData = spawner.getSpawnerData();
            spawnerData.setActive(false);

            ItemStack spawnerItem = spawner.getSpawnerData().getBlockItem();

            PDCWrapper.modifyItem(plugin, spawnerItem, wrapper -> {
                wrapper.setBoolean("broken", true);
                wrapper.setInteger("stack-amount", spawnerData.getStackSize());
                wrapper.setDouble("storage", spawnerData.getStorage());
                wrapper.setDouble("xp", spawnerData.getStoredXp());
                wrapper.setString("spawner-id", spawnerId.toString());
            });

            if (block.getState() instanceof CreatureSpawner) {
                player.getWorld().dropItem(block.getLocation(), spawnerItem);
            }

            spawnerRegistry.removeSpawner(spawner);
        }
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
