package dev.imlukas.ultraspawners.handler;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.group.SpawnerGroup;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.utils.storage.FileHandler;
import org.bukkit.configuration.ConfigurationSection;

public class SpawnerHandler extends FileHandler {

    private final UltraSpawnersPlugin plugin;
    private final SpawnerDataFactory spawnerDataFactory;
    private final GeneralSpawnerRegistry spawnerRegistry;

    public SpawnerHandler(UltraSpawnersPlugin plugin) {
        super(plugin, "spawners.yml");
        this.plugin = plugin;
        this.spawnerDataFactory = plugin.getSpawnerDataRegistry();
        this.spawnerRegistry = plugin.getSpawnerRegistry();
        load();
    }

    @Override
    public void load() {
        getConfiguration().getKeys(false).forEach(key -> {
            ConfigurationSection section = getConfiguration().getConfigurationSection(key);

            if (section == null) {
                return;
            }

            SpawnerData spawnerData = new SpawnerData(plugin, section);
            spawnerDataFactory.register(() -> new SpawnerData(plugin, section));
            spawnerRegistry.registerGroup(new SpawnerGroup(plugin, spawnerData));
            System.out.println("[UltraSpawners] Registered spawner data and group for: " + key);
        });
    }
}
