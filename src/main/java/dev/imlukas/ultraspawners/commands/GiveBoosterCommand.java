package dev.imlukas.ultraspawners.commands;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveBoosterCommand implements SimpleCommand {

    private final UltraSpawnersPlugin plugin;

    public GiveBoosterCommand(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "spawners.give.booster";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player player)) {
            return;
        }

        ItemStack booster = plugin.getPluginSettings().getBoosterItem();

        if (booster == null) {
            return;
        }

        player.getInventory().addItem(booster);
        plugin.getMessages().sendMessage(player, "booster-received");
    }
}
