package dev.imlukas.ultraspawners.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class BlockPlaceListener implements Listener {

    private final UltraSpawnersPlugin plugin;
    private final GeneralSpawnerRegistry spawnerRegistry;
    private final SpawnerDataFactory dataFactory;

    public BlockPlaceListener(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.spawnerRegistry = plugin.getSpawnerRegistry();
        this.dataFactory = plugin.getSpawnerDataRegistry();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Block block = event.getBlock();
        PersistentDataContainer blockData = new CustomBlockData(block, plugin);

        ItemStack itemBlock = event.getItemInHand();

        PDCWrapper wrapper = new PDCWrapper(plugin, itemBlock);
        String identifier = wrapper.getString("spawner");

        int stackAmount = 1;

        if (wrapper.contains("stack-amount")) {
            stackAmount = wrapper.getInteger("stack-amount");
        }

        if (identifier == null) {
            return;
        }

        Block targetBlock = event.getBlockAgainst();

        if (!targetBlock.getType().isBlock()) {
            return;
        }

        PersistentDataContainer targetBlockData = new CustomBlockData(targetBlock, plugin);
        String spawnerId = targetBlockData.get(new NamespacedKey(plugin, "spawner-id"), PersistentDataType.STRING);

        if (spawnerId != null) {
            event.setCancelled(true);
            InstancedSpawner spawner = spawnerRegistry.getSpawner(UUID.fromString(spawnerId));

            if (spawner == null) {
                return;
            }

            if (!identifier.equals(spawner.getSpawnerData().getIdentifier())) {
                return;
            }

            int amount = 1;
            int amountToRemove = 1;

            if (wrapper.contains("stack-amount")) {
                amount = wrapper.getInteger("stack-amount");
            }

            if (player.isSneaking()) {
                amountToRemove = itemBlock.getAmount();
                if (wrapper.contains("stack-amount")) {

                    amount = amount * itemBlock.getAmount();
                } else {
                    amount = itemBlock.getAmount();
                }
            }

            spawner.getSpawnerData().addStack(amount);

            if (amountToRemove == itemBlock.getAmount()) {
                player.getInventory().remove(itemBlock);
            } else {
                itemBlock.setAmount(itemBlock.getAmount() - amountToRemove);
            }

            plugin.getMessages().sendActionbar(player, "spawner.added-stack", new Placeholder<>("amount", String.valueOf(amount)));
            return;
        }

        InstancedSpawner spawner = new InstancedSpawner(plugin, dataFactory.supply(identifier), block.getLocation());
        spawner.getSpawnerData().setStackSize(stackAmount);
        spawnerRegistry.addSpawner(spawner);

        blockData.set(new NamespacedKey(plugin, "spawner-id"), PersistentDataType.STRING, spawner.getSpawnerId().toString());

        if (block.getState() instanceof CreatureSpawner creatureSpawner) {
            creatureSpawner.setSpawnedType(EntityType.valueOf(spawner.getSpawnerData().getEntityType() == null ? "PIG" : spawner.getSpawnerData().getEntityType()));
        }
    }
}
