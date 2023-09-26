package dev.imlukas.ultraspawners.handler;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.utils.storage.FileHandler;
import org.bukkit.configuration.ConfigurationSection;

public class SpawnerHandler extends FileHandler {

    private final UltraSpawnersPlugin plugin;
    private final SpawnerDataFactory registry;

    public SpawnerHandler(UltraSpawnersPlugin plugin) {
        super(plugin, "spawners.yml");
        this.plugin = plugin;
        this.registry = plugin.getSpawnerDataRegistry();
        load();
    }

    @Override
    public void load() {
        getConfiguration().getKeys(false).forEach(key -> {
            ConfigurationSection section = getConfiguration().getConfigurationSection(key);

            if (section == null) {
                return;
            }

            registry.register(() -> new SpawnerData(plugin, section));
            System.out.println("Registered spawner data: " + key);
        });
    }
}
