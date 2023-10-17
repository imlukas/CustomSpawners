package dev.imlukas.ultraspawners.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.PluginSettings;
import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
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
    private final PluginSettings pluginSettings;
    private final GeneralSpawnerRegistry spawnerRegistry;

    public BlockBreakListener(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.pluginSettings = plugin.getPluginSettings();
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
            Material itemType = itemInHand.getType();


            if (pluginSettings.isMultiPick() && !(itemType.toString().contains("PICKAXE"))) {
                return;
            } else {
                if (!pluginSettings.getPickaxeTypes().contains(itemType)) {
                    return;
                }
            }

            if (!itemInHand.getItemMeta().getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
                return;
            }

            InstancedSpawner spawner = spawnerRegistry.getSpawner(spawnerId);

            if (spawner == null) {
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
