package dev.imlukas.ultraspawners.utils.PDCUtils;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.function.Consumer;

public class BlockPDCWrapper {

    private final JavaPlugin plugin;
    private final TileState state;

    public BlockPDCWrapper(JavaPlugin plugin, Block block) {
        this.plugin = plugin;
        this.state = (TileState) block.getState();
    }

    public static void modifyItem(JavaPlugin plugin, Block block, Consumer<BlockPDCWrapper> modifier) {
        BlockPDCWrapper wrapper = new BlockPDCWrapper(plugin, block);
        modifier.accept(wrapper);
    }

    public void setString(String key, String value) {
        set(key, PersistentDataType.STRING, value);
    }

    public void setInteger(String key, int value) {
        set(key, PersistentDataType.INTEGER, value);
    }

    public void setBoolean(String key, boolean value) {
        set(key, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public void setUUID(String key, UUID value) {
        set(key, PersistentDataType.STRING, value.toString());
    }

    public String getString(String key) {
        return get(key, PersistentDataType.STRING);
    }

    public int getInteger(String key) {
        return get(key, PersistentDataType.INTEGER);
    }

    public boolean getBoolean(String key) {
        return get(key, PersistentDataType.BYTE) == 1;
    }

    public UUID getUUID(String key) {
        return UUID.fromString(get(key, PersistentDataType.STRING));
    }

    private NamespacedKey createKey(String name) {
        return new NamespacedKey(plugin, name);
    }


    private <T> void set(String key, PersistentDataType<T, T> type, T value) {
        state.getPersistentDataContainer().set(createKey(key), type, value);
        state.update();
    }

    private <T> T get(String key, PersistentDataType<T, T> type) {
        return state.getPersistentDataContainer().get(createKey(key), type);
    }
}
