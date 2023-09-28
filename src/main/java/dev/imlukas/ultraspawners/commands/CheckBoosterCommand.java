package dev.imlukas.ultraspawners.commands;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.utils.command.SimpleCommand;
import dev.imlukas.ultraspawners.utils.storage.Messages;
import dev.imlukas.ultraspawners.utils.text.Placeholder;
import dev.imlukas.ultraspawners.utils.time.Time;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class CheckBoosterCommand implements SimpleCommand {

    private final Messages messages;
    private final PlayerDataRegistry playerDataRegistry;

    public CheckBoosterCommand(UltraSpawnersPlugin plugin) {
        this.messages = plugin.getMessages();
        this.playerDataRegistry = plugin.getPlayerDataRegistry();
    }

    @Override
    public String getPermission() {
        return "ultraspawners.booster";
    }

    @Override
    public String getIdentifier() {
        return "spawners.booster";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player player)) {
            return;
        }

        PlayerData playerData = playerDataRegistry.get(player.getUniqueId());

        Time time = new Time(playerData.getBoostDuration(), TimeUnit.SECONDS);

        long hours = time.as(TimeUnit.HOURS);
        time = new Time(hours, TimeUnit.HOURS);

        messages.sendMessage(player, "booster-check", new Placeholder<>("duration", time.toString()));
    }
}
