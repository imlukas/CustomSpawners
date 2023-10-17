package dev.imlukas.ultraspawners.listener;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.storage.FileDatabase;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.storage.Messages;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class BoosterInteractListener implements Listener {

    private final UltraSpawnersPlugin plugin;
    private final Messages messages;
    private final PlayerDataRegistry playerDataRegistry;

    public BoosterInteractListener(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.playerDataRegistry = plugin.getPlayerDataRegistry();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = playerDataRegistry.get(player.getUniqueId());
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }
        PDCWrapper wrapper = new PDCWrapper(plugin, item);

        if (!wrapper.contains("booster")) {
            return;
        }

        event.setCancelled(true);

        if (playerData.isBoosted()) {
            messages.sendActionbar(player, "booster-already-active");
            return;
        }

        playerData.setBoosted(true, plugin.getPluginSettings().getBoosterDuration().as(TimeUnit.SECONDS));

        if (item.getAmount() == 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        messages.sendActionbar(player, "booster-activated", new Placeholder<>("duration",
                plugin.getPluginSettings().getBoosterDuration().toString()));
    }

}
