package dev.imlukas.ultraspawners.commands;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SimpleCommand {

    private final UltraSpawnersPlugin plugin;

    public ReloadCommand(UltraSpawnersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "ultraspawners.reload";
    }

    @Override
    public String getPermission() {
        return "ultraspawners.reload";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        plugin.reload();
        sender.sendMessage("[UltraSpawners] Reloaded Plugin!");
    }
}
