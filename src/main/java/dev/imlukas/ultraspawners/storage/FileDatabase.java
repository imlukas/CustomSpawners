package dev.imlukas.ultraspawners.storage;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FileDatabase {

    private final FileManager fileManager;

    public FileDatabase(UltraSpawnersPlugin plugin) {
        this.fileManager = new FileManager(plugin);
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public String getIdentifier() {
        return "file";
    }

    public boolean connect() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            fileManager.add(onlinePlayer.getUniqueId());
        }

        return true;
    }

    public SpawnerFile getSpawnerFile() {
        return fileManager.getSpawnerFile();
    }

    public <T> CompletableFuture<T> fetch(UUID playerId, String key, Class<T> clazz) {
        return fileManager.getPlayerFile(playerId).fetch(key);
    }


    public <T> CompletableFuture<T> fetchOrDefault(UUID playerId, String key, Class<T> clazz, T defaultValue) {
        return fetch(playerId, key, clazz).thenApply(value -> value == null ? defaultValue : value);
    }

    public <T> CompletableFuture<Map<String, T>> fetchMultiple(UUID playerId, Class<T> clazz, T defaultValue, String... keys) {
        return fetchMultiple(playerId, clazz, keys).thenApply(values -> {
            Map<String, T> map = new HashMap<>();

            for (Map.Entry<String, T> valuesEntry : values.entrySet()) {
                if (valuesEntry.getValue() == null) {
                    map.put(valuesEntry.getKey(), defaultValue);
                }

                map.put(valuesEntry.getKey(), valuesEntry.getValue());
            }

            return map;
        });
    }

    public <T> CompletableFuture<Map<String, T>> fetchMultiple(UUID playerId, Class<T> clazz, String... keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, T> map = new HashMap<>();
            PlayerFile playerFile = fileManager.getPlayerFile(playerId);

            for (String key : keys) {
                playerFile.fetch(key, clazz).thenAccept(value -> map.put(key, value));
            }

            return map;
        });
    }

    public void store(UUID playerId, String key, Object value) {
        fileManager.getPlayerFile(playerId).store(key, value);
    }

    public void storeMultiple(UUID playerId, Map<String, Object> values) {
        fileManager.getPlayerFile(playerId).storeMultiple(values);
    }

    public void storeMultiple(UUID playerId, String sectionIdentifier, Map<String, Object> values) {
        fileManager.getPlayerFile(playerId).storeMultiple(sectionIdentifier, values);
    }
}
