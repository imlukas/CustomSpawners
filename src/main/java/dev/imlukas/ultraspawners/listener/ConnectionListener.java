package dev.imlukas.ultraspawners.listener;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.storage.FileDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final FileDatabase fileDatabase;
    private final PlayerDataRegistry playerDataRegistry;

    public ConnectionListener(UltraSpawnersPlugin plugin) {
        this.fileDatabase = plugin.getFileDatabase();
        this.playerDataRegistry = plugin.getPlayerDataRegistry();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        fileDatabase.getFileManager().add(event.getPlayer().getUniqueId());
        playerDataRegistry.register(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playerDataRegistry.unregister(event.getPlayer().getUniqueId());
        fileDatabase.getFileManager().remove(event.getPlayer().getUniqueId());
    }
}
