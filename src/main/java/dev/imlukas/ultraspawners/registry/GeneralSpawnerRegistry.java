package dev.imlukas.ultraspawners.registry;

import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.registry.DefaultRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Not the best approach to this problem as this can be a potential memory leak. If you're a developer working on top of
 * this base keep that in mind and if you can, try to find a better solution. ;)
 */
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

    public void clear() {
        spawnerList.clear();
    }

    public List<InstancedSpawner> getSpawnersByIdentifier(String identifier) {
        List<InstancedSpawner> spawners = new ArrayList<>();

        for (InstancedSpawner spawner : spawnerList) {
            if (spawner.getSpawnerData().getIdentifier().equals(identifier)) {
                spawners.add(spawner);
            }
        }

        return spawners;
    }

    public List<InstancedSpawner> getSpawnerList() {
        return spawnerList;
    }
}
