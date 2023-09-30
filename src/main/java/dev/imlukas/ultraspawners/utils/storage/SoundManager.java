package dev.imlukas.ultraspawners.utils.storage;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SoundManager extends YMLBase {

    private final ConfigurationSection soundsSections;

    public SoundManager(JavaPlugin plugin) {
        super(plugin, "sounds.yml");
        this.soundsSections = getConfiguration().getConfigurationSection("sounds");
    }

    public void playSound(Player player, String key) {
        if (!soundsSections.getBoolean(key + ".enabled")) {
            return;
        }

        int volume = soundsSections.getInt(key + ".volume") == 0 ? 1 : soundsSections.getInt(key + ".volume");
        int pitch = soundsSections.getInt(key + ".pitch") == 0 ? 1 : soundsSections.getInt(key + ".pitch");

        playSound(player, key, volume, pitch);
    }

    public void playSound(Player player, String key, int volume, int pitch) {
        playSound(player, player.getLocation(), key, volume, pitch);
    }

    public void playSound(Player player, Location location, String key, int volume, int pitch) {
        Sound sound = Sound.valueOf(soundsSections.getString(key + ".sound"));
        player.playSound(location, sound, volume, pitch);
    }

    public void playSoundWorld(Player player, Location location, String key, int volume, int pitch) {
        World world = player.getWorld();
        Sound sound = Sound.valueOf(soundsSections.getString(key + ".sound"));
        world.playSound(location, sound, volume, pitch);
    }
}
