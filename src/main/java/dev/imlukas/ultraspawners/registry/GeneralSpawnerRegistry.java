package dev.imlukas.ultraspawners.registry;

import dev.imlukas.ultraspawners.group.SpawnerGroup;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.registry.DefaultRegistry;

import java.util.*;

/**
 * Not the best approach to this problem as this can be a potential memory leak. If you're a developer working on top of
 * this base keep that in mind and if you can, try to find a better solution. ;)
 */
public class GeneralSpawnerRegistry {

    private final Map<String, SpawnerGroup> groups = new HashMap<>();

    public void registerGroup(SpawnerGroup group) {
        groups.put(group.getGroupIdentifier(), group);
    }

    public void addSpawner(InstancedSpawner spawner) {
        for (SpawnerGroup group : groups.values()) {
            if (group.getGroupIdentifier().equals(spawner.getSpawnerData().getIdentifier())) {
                group.addSpawner(spawner);
                return;
            }
        }
    }

    public void removeSpawner(InstancedSpawner spawner) {
        for (SpawnerGroup group : groups.values()) {
            if (group.getGroupIdentifier().equals(spawner.getSpawnerData().getIdentifier())) {
                group.removeSpawner(spawner);
                return;
            }
        }
    }

    public InstancedSpawner getSpawner(UUID spawnerId) {
        for (SpawnerGroup group : groups.values()) {
            for (InstancedSpawner spawner : group.getSpawnerList()) {
                if (spawner.getSpawnerId().equals(spawnerId)) {
                    return spawner;
                }
            }
        }

        return null;
    }

    public void clear() {
        for (SpawnerGroup group : groups.values()) {
            group.getSpawnerList().clear();
        }
    }

    public List<InstancedSpawner> getAllSpawners() {
        List<InstancedSpawner> spawners = new ArrayList<>();
        for (SpawnerGroup group : groups.values()) {
            spawners.addAll(group.getSpawnerList());
        }
        return spawners;
    }

    public void getGroup(String identifier) {
        groups.get(identifier);
    }

    public Map<String, SpawnerGroup> getGroups() {
        return groups;
    }
}
