package dev.imlukas.ultraspawners.storage;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.storage.YMLBase;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerFile extends YMLBase {

    public PlayerFile(UltraSpawnersPlugin plugin, String playerId) {
        super(plugin, "db/" + playerId + ".yml", false);
    }

    public <T> CompletableFuture<T> fetch(String key, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> (T) getConfiguration().get(key, clazz));
    }

    public <T> CompletableFuture<T> fetch(String key) {
        return CompletableFuture.supplyAsync(() -> (T) getConfiguration().get(key));
    }

    public void store(String key, Object value) {
        CompletableFuture.runAsync(() -> {
            getConfiguration().set(key, value);
            save();
        });
    }

    public void storeMultiple(Map<String, Object> values) {
        storeMultiple("", values);
    }

    public void storeMultiple(String sectionIdentifier, Map<String, Object> values) {
        CompletableFuture.runAsync(() -> {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                getConfiguration().set(sectionIdentifier + entry.getKey(), entry.getValue());
            }
            save();
        });
    }
}
