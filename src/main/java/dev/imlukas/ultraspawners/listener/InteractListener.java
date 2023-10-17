package dev.imlukas.ultraspawners.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.menu.GenericSpawnerMenu;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.storage.FileDatabase;
import dev.imlukas.ultraspawners.storage.SpawnerFile;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.storage.Messages;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InteractListener implements Listener {

    private final UltraSpawnersPlugin plugin;
    private final Messages messages;
    private final SpawnerDataFactory spawnerDataFactory;
    private final GeneralSpawnerRegistry spawnerRegistry;
    private final PlayerDataRegistry playerDataRegistry;
    private final FileDatabase fileDatabase;

    public InteractListener(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.spawnerDataFactory = plugin.getSpawnerDataRegistry();
        this.spawnerRegistry = plugin.getSpawnerRegistry();
        this.playerDataRegistry = plugin.getPlayerDataRegistry();
        this.fileDatabase = plugin.getFileDatabase();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action clickType = event.getAction();
        ItemStack itemStack = event.getItem();

        if (itemStack != null && itemStack.getType() == Material.SPAWNER) {
            return;
        }

        if (!clickType.isRightClick()) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null || block.getType().isAir()) {
            return;
        }

        CustomBlockData customBlockData = new CustomBlockData(block, plugin);
        String spawnerId = customBlockData.get(new NamespacedKey(plugin, "spawner-id"), PersistentDataType.STRING);

        if (spawnerId == null || spawnerId.isEmpty()) {
            return;
        }

        UUID spawnerUUID = UUID.fromString(spawnerId);

        InstancedSpawner spawner = spawnerRegistry.getSpawner(spawnerUUID);

        if (spawner == null) {
            SpawnerFile file = fileDatabase.getFileManager().getSpawnerFile();
            spawner = fetchSpawner(spawnerId, file);
            spawnerRegistry.addSpawner(spawner);
        }

        if (spawner == null) {
            System.err.println("[InteractEvent] Spawner with id " + spawnerId + " not found!");
            return;
        }

        // Here we can assume the owner of the spawner is not online, so we instantiate a new spawner so the player can interact with it
        new GenericSpawnerMenu(plugin, player, spawner).open();

    }

    public InstancedSpawner fetchSpawner(String spawnerId, SpawnerFile file) {
        FileConfiguration config = file.getConfiguration();
        UUID spawnerUUID = UUID.fromString(spawnerId);
        ConfigurationSection spawnerSection = config.getConfigurationSection(spawnerId);

        if (spawnerSection == null) {
            System.out.println("Spawner section is null for " + spawnerId);
            return null;
        }

        SpawnerData spawnerData = spawnerDataFactory.supply(spawnerSection.getString("spawner-id"));
        spawnerData.setStackSize(spawnerSection.getInt("spawner-stack"));
        spawnerData.setStorage(spawnerSection.getInt("spawner-storage"));
        spawnerData.setStoredXp(spawnerSection.getInt("spawner-xp"));

        Location location = parseLocation(spawnerSection.getConfigurationSection("location"));
        return new InstancedSpawner(spawnerUUID, spawnerData, location);
    }

    public Location parseLocation(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        return new Location(Bukkit.getWorld(section.getString("location-world")), section.getDouble("location-x"), section.getDouble("location-y"), section.getDouble("location-z"));
    }
}
