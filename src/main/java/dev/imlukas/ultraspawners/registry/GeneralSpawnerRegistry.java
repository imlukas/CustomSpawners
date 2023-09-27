package dev.imlukas.ultraspawners.registry;

import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.registry.DefaultRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeneralSpawnerRegistry {

    private final List<InstancedSpawner> spawnerList = new ArrayList<>();

    public void addSpawner(InstancedSpawner spawner) {
        spawnerList.add(spawner);
    }

    public void removeSpawner(InstancedSpawner spawner) {
        spawnerList.remove(spawner);
    }

    public InstancedSpawner getSpawner(UUID spawnerId) {
        for (InstancedSpawner spawner : spawnerList) {
            if (spawner.getSpawnerId().equals(spawnerId)) {
                return spawner;
            }
        }

        return null;
    }

    public List<InstancedSpawner> getSpawnerList() {
        return spawnerList;
    }
}
