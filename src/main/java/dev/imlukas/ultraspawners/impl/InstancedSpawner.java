package dev.imlukas.ultraspawners.impl;

import com.google.common.collect.Sets;
import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class InstancedSpawner {

    private final UUID spawnerId;
    private final SpawnerData spawnerData;
    private final Location blockLocation;
    private final Set<PlayerData> playersInRange = Sets.newConcurrentHashSet();

    public InstancedSpawner(UltraSpawnersPlugin plugin, SpawnerData data, Location blockLocation) {
        this(plugin, UUID.randomUUID(), data, blockLocation);
    }

    public InstancedSpawner(UltraSpawnersPlugin plugin, UUID spawnerId, SpawnerData spawnerData, Location blockLocation) {
        this.spawnerId = spawnerId;
        this.spawnerData = spawnerData;
        this.blockLocation = blockLocation;

        spawnerData.setActive(true);
    }

    public Set<PlayerData> getPlayersInRange() {
        return playersInRange;
    }

    public void addPlayerInRange(PlayerData playerData) {
        playersInRange.add(playerData);
    }

    public void removePlayerInRange(PlayerData playerData) {
        if (!playersInRange.contains(playerData)) {
            return;
        }

        playersInRange.remove(playerData);
    }

    public UUID getSpawnerId() {
        return spawnerId;
    }

    public SpawnerData getSpawnerData() {
        return spawnerData;
    }

    public Location getBlockLocation() {
        return blockLocation;
    }
}
