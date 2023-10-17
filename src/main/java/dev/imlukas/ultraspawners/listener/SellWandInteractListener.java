package dev.imlukas.ultraspawners.listener;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.storage.Messages;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SellWandInteractListener implements Listener {

    private final UltraSpawnersPlugin plugin;
    private final Messages messages;
    private final GeneralSpawnerRegistry spawnerRegistry;

    public SellWandInteractListener(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.spawnerRegistry = plugin.getSpawnerRegistry();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }
        PDCWrapper wrapper = new PDCWrapper(plugin, item);


        if (!wrapper.contains("sell-wand")) {
            return;
        }

        Location location = player.getLocation();
        double collectedMoney = 0;
        double collectedXp = 0;

        for (InstancedSpawner allSpawner : spawnerRegistry.getAllSpawners()) {

            if (!(allSpawner.getBlockLocation().distance(location) < plugin.getPluginSettings().getSpawnerRange())) {
                continue;
            }

            double amountMoney = allSpawner.getSpawnerData().getSellPrice() * allSpawner.getSpawnerData().getStorage();
            double amountXp = allSpawner.getSpawnerData().getStoredXp();

            if (allSpawner.collect(player)) {
                collectedXp += amountXp;
                collectedMoney += amountMoney;
            }
        }

        if (collectedMoney == 0) {
            messages.sendMessage(player, "sell-wand-cant-collect");
            return;
        }

        Placeholder<Player> moneyPlaceholder = new Placeholder<>("sold-amount", String.valueOf(collectedMoney));
        Placeholder<Player> xpPlaceholder = new Placeholder<>("xp-amount", String.valueOf(collectedXp));

        messages.sendMessage(player, "sell-wand-used");
        messages.sendMessage(player, "spawner.collected", xpPlaceholder, moneyPlaceholder);
        messages.sendActionbar(player, "spawner.collected-actionbar", xpPlaceholder, moneyPlaceholder);
        plugin.getSounds().playSound(player, "spawner-collected");
    }
}
