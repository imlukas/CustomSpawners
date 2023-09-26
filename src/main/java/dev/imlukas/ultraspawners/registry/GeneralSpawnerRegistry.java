package dev.imlukas.ultraspawners.registry;

import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.registry.DefaultRegistry;

import java.util.UUID;

public class GeneralSpawnerRegistry extends DefaultRegistry<UUID, InstancedSpawner> {

    public void addSpawner(InstancedSpawner spawner) {
        if (get(spawner.getSpawnerId()) != null) {
            return;
        }

        register(spawner.getSpawnerId(), spawner);
    }

    public void removeSpawner(InstancedSpawner spawner) {
        unregister(spawner.getSpawnerId());
    }

    public InstancedSpawner getSpawner(UUID spawnerId) {
        return get(spawnerId);
    }
}
