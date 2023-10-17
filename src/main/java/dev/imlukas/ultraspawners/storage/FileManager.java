package dev.imlukas.ultraspawners.storage;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileManager {

    private final UltraSpawnersPlugin plugin;

    private final Map<UUID, PlayerFile> playerFiles = new HashMap<>();
    private final SpawnerFile spawnerFile;

    public FileManager(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.spawnerFile = new SpawnerFile(plugin);
    }

    public PlayerFile getPlayerFile(UUID playerId) {
        return playerFiles.computeIfAbsent(playerId, uuid -> new PlayerFile(plugin, uuid.toString()));
    }

    public SpawnerFile getSpawnerFile() {
        return spawnerFile;
    }

    public void remove(UUID playerId) {
        playerFiles.remove(playerId);
    }

    public void add(UUID playerId) {
        playerFiles.put(playerId, new PlayerFile(plugin, playerId.toString()));
    }
}
