package dev.imlukas.ultraspawners.commands;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.utils.PDCUtils.PDCWrapper;
import dev.imlukas.ultraspawners.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class GiveSpawnerCommand implements SimpleCommand {

    private final UltraSpawnersPlugin plugin;
    private final SpawnerDataFactory spawnerDataFactory;

    public GiveSpawnerCommand(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
        this.spawnerDataFactory = plugin.getSpawnerDataRegistry();
    }

    @Override
    public String getIdentifier() {
        return "spawners.give.*.*";
    }

    @Override
    public String getPermission() {
        return "ultraspawners.give";
    }

    @Override
    public Map<Integer, List<String>> tabCompleteWildcards() {
        return Map.of(1, spawnerDataFactory.getIdentifiers());
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player player)) {
            return;
        }

        if (args.length == 0) {
            return;
        }

        String identifier = args[0];

        if (identifier.isEmpty()) {
            return;
        }

        int amount = 1;

        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        ItemStack blockItem = spawnerDataFactory.supply(identifier).getBlockItem();

        PDCWrapper.modifyItem(plugin, blockItem, wrapper -> {
            wrapper.setInteger("stack-amount", 1);
        });


        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(blockItem);
        }

        player.sendMessage("You have been given the spawner " + identifier);
    }
}
