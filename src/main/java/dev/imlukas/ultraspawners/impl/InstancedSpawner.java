package dev.imlukas.ultraspawners.impl;

import com.google.common.collect.Sets;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.data.SpawnerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class InstancedSpawner {

    private final UUID spawnerId;
    private final SpawnerData spawnerData;
    private final Location blockLocation;
    private final Set<PlayerData> playersInRange = Sets.newConcurrentHashSet();

    public InstancedSpawner(SpawnerData data, Location blockLocation) {
        this(UUID.randomUUID(), data, blockLocation);
    }

    public InstancedSpawner(UUID spawnerId, SpawnerData spawnerData, Location blockLocation) {
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

    public boolean collect(Player player) {
        double amount = spawnerData.getStorage();
        double sellPrice = spawnerData.getSellPrice() * amount;
        double xp = spawnerData.getStoredXp();

        if (!spawnerData.getPlugin().getPluginSettings().canPickupAtZero() && (spawnerData.getStoragePercent() <= 0.9)) {
            spawnerData.getPlugin().getSounds().playSound(player, "spawner-cannot-collect");
            return false;

        }

        spawnerData.setStorage(0);
        spawnerData.setStoredXp(0);

        spawnerData.getPlugin().getEconomy().depositPlayer(player, sellPrice);
        player.giveExp((int) xp);
        return true;
    }

    public boolean collectXp(Player player) {
        double xp = spawnerData.getStoredXp();

        if (xp == 0) {
            spawnerData.getPlugin().getSounds().playSound(player, "spawner-cannot-collect");
            return false;
        }

        spawnerData.setStoredXp(0);
        player.giveExp((int) xp);
        return true;
    }
}
