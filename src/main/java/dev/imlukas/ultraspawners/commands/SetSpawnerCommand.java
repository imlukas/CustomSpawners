package dev.imlukas.ultraspawners.commands;

import dev.imlukas.ultraspawners.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;

public class SetSpawnerCommand implements SimpleCommand {
    @Override
    public String getIdentifier() {
        return "spawners.set.*.*.*.*.*";
    }

    @Override
    public void execute(CommandSender sender, String... args) {

    }
}
