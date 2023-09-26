package dev.imlukas.ultraspawners.registry;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.storage.FileDatabase;
import dev.imlukas.ultraspawners.storage.PlayerFile;
import dev.imlukas.ultraspawners.utils.registry.DefaultRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerDataRegistry extends DefaultRegistry<UUID, PlayerData> {

    private final UltraSpawnersPlugin plugin;
    private final FileDatabase fileDatabase;

    public PlayerDataRegistry(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.fileDatabase = plugin.getFileDatabase();
    }

    public void register(Player player) {
        PlayerData playerData = new PlayerData(plugin, player.getUniqueId());

        PlayerFile playerFile = fileDatabase.getFileManager().getPlayerFile(player.getUniqueId());
        FileConfiguration config = playerFile.getConfiguration();

        boolean isBoosted = config.getBoolean("boosted", false);
        int boostDuration = config.getInt("boosted-time", 0);

        if (isBoosted) {
            playerData.setBoosted(true, boostDuration);
        }

        register(player.getUniqueId(), playerData);
    }

    @Override
    public void unregister(UUID uuid) {
        PlayerData playerData = get(uuid);

        if (playerData.isBoosted()) {
            fileDatabase.store(uuid, "boosted", true);
            fileDatabase.store(uuid, "boosted-time", playerData.getBoostDuration());
        }

        super.unregister(uuid);
    }
}
