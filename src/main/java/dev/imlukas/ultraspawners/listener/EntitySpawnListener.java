package dev.imlukas.ultraspawners.listener;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntitySpawnListener implements Listener {

    private final GeneralSpawnerRegistry spawnerRegistry;

    public EntitySpawnListener(UltraSpawnersPlugin plugin) {
        this.spawnerRegistry = plugin.getSpawnerRegistry();
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

        if (spawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }

        Entity entity = event.getEntity();
        Location location = entity.getLocation();

        for (InstancedSpawner value : spawnerRegistry.getAllSpawners()) {
            if (value.getBlockLocation().distance(location) >= 5) {
                continue;
            }
            event.setCancelled(true);
        }
    }
}
