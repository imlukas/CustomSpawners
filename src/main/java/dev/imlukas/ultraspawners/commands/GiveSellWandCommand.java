package dev.imlukas.ultraspawners.commands;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveSellWandCommand implements SimpleCommand {

    private final UltraSpawnersPlugin plugin;

    public GiveSellWandCommand(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "ultraspawners.give.sellwand";
    }

    @Override
    public String getIdentifier() {
        return "ultraspawners.give.sellwand";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player player)) {
            return;
        }

        ItemStack sellWandItem = plugin.getPluginSettings().getSellWandItem();

        if (sellWandItem == null) {
            return;
        }

        player.getInventory().addItem(sellWandItem);
        plugin.getMessages().sendMessage(player, "sell-wand-received");
    }
}
