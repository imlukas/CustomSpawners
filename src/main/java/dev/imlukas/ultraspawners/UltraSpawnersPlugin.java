package dev.imlukas.ultraspawners;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.commands.CheckBoosterCommand;
import dev.imlukas.ultraspawners.commands.GiveBoosterCommand;
import dev.imlukas.ultraspawners.commands.GiveSpawnerCommand;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.handler.SpawnerHandler;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.listener.*;
import dev.imlukas.ultraspawners.registry.GeneralSpawnerRegistry;
import dev.imlukas.ultraspawners.registry.PlayerDataRegistry;
import dev.imlukas.ultraspawners.registry.SpawnerDataFactory;
import dev.imlukas.ultraspawners.storage.FileDatabase;
import dev.imlukas.ultraspawners.storage.SpawnerFile;
import dev.imlukas.ultraspawners.utils.BetterJavaPlugin;
import dev.imlukas.ultraspawners.utils.command.impl.CommandManager;
import dev.imlukas.ultraspawners.utils.menu.registry.MenuRegistry;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.ultraspawners.utils.storage.Messages;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;
import java.util.UUID;

@Getter
public final class UltraSpawnersPlugin extends BetterJavaPlugin {

    private PluginSettings pluginSettings;
    private CommandManager commandManager;
    private MenuRegistry menuRegistry;
    private Messages messages;
    private Economy economy;
    private FileDatabase fileDatabase;

    private GeneralSpawnerRegistry spawnerRegistry;
    private PlayerDataRegistry playerDataRegistry;
    private SpawnerDataFactory spawnerDataRegistry;

    private SpawnerHandler spawnerHandler;


    @Override
    public void onEnable() {
        super.onEnable();
        pluginSettings = new PluginSettings(this);
        setupEconomy();
        this.commandManager = new CommandManager(this);
        this.menuRegistry = new MenuRegistry(this);
        this.messages = new Messages(this);

        this.fileDatabase = new FileDatabase(this);

        this.spawnerRegistry = new GeneralSpawnerRegistry();
        this.spawnerDataRegistry = new SpawnerDataFactory();
        this.playerDataRegistry = new PlayerDataRegistry(this);

        this.spawnerHandler = new SpawnerHandler(this);

        CustomBlockData.registerListener(this);

        commandManager.register(new GiveSpawnerCommand(this));
        commandManager.register(new GiveBoosterCommand(this));
        commandManager.register(new CheckBoosterCommand(this));
        registerListener(new BlockPlaceListener(this));
        registerListener(new ConnectionListener(this));
        registerListener(new InteractListener(this));
        // registerListener(new PlayerMoveListener(this));
        registerListener(new BlockBreakListener(this));
        registerListener(new EntitySpawnListener(this));
        setupRangeTask();
    }

    @Override
    public void onDisable() {
        for (InstancedSpawner spawner : spawnerRegistry.getValues()) {
            SpawnerFile spawnerFile = fileDatabase.getFileManager().getSpawnerFile();
            SpawnerData spawnerData = spawner.getSpawnerData();
            Location location = spawner.getBlockLocation();

            spawnerFile.storeMultiple(spawner.getSpawnerId().toString() + ".", Map.of("spawner-id", spawnerData.getIdentifier(),
                    "spawner-stack", spawnerData.getStackSize(),
                    "spawner-storage", spawnerData.getStorage(),
                    "spawner-xp", spawnerData.getStoredXp(),
                    "location.location-x", location.getX(),
                    "location.location-y", location.getY(),
                    "location.location-z", location.getZ(),
                    "location.location-world", location.getWorld().getName()));
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public void setupRangeTask() {
        new ScheduleBuilder(this).every(1).seconds().run(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Location location = onlinePlayer.getLocation();
                PlayerData playerData = playerDataRegistry.get(onlinePlayer.getUniqueId());

                if (playerData == null) {
                    continue;
                }

                for (InstancedSpawner allSpawner : spawnerRegistry.getValues()) {

                    if (allSpawner.getBlockLocation().distance(location) < pluginSettings.getSpawnerRange()) {
                        allSpawner.addPlayerInRange(playerData);
                    } else {
                        allSpawner.removePlayerInRange(playerData);
                    }
                }
            }
        }).sync().start();
    }

    public InstancedSpawner fetchSpawner(String spawnerId, SpawnerFile file) {
        FileConfiguration config = file.getConfiguration();
        UUID spawnerUUID = UUID.fromString(spawnerId);
        ConfigurationSection spawnerSection = config.getConfigurationSection(spawnerId);

        if (spawnerSection == null) {
            return null;
        }

        SpawnerData spawnerData = spawnerDataRegistry.supply(spawnerSection.getString("spawner-id"));
        spawnerData.setStackSize(spawnerSection.getInt("spawner-stack"));
        spawnerData.setStorage(spawnerSection.getInt("spawner-storage"));
        spawnerData.setStoredXp(spawnerSection.getInt("spawner-xp"));

        Location location = parseLocation(spawnerSection.getConfigurationSection("location"));
        return new InstancedSpawner(this, spawnerUUID, spawnerData, location);
    }

    public Location parseLocation(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        return new Location(Bukkit.getWorld(section.getString("location-world")), section.getDouble("location-x"), section.getDouble("location-y"), section.getDouble("location-z"));
    }
}
