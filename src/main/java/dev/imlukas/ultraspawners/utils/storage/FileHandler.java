package dev.imlukas.ultraspawners.utils.storage;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class FileHandler extends YMLBase{

    private final JavaPlugin plugin;

    public FileHandler(JavaPlugin plugin, String name) {
        super(plugin, name);
        this.plugin = plugin;
    }

    public abstract void load();

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
