package dev.imlukas.ultraspawners;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.imlukas.ultraspawners.commands.CheckBoosterCommand;
import dev.imlukas.ultraspawners.commands.GiveBoosterCommand;
import dev.imlukas.ultraspawners.commands.GiveSpawnerCommand;
import dev.imlukas.ultraspawners.commands.ReloadCommand;
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
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.ultraspawners.utils.storage.Messages;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    private ScheduledTask rangeTask;


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

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            playerDataRegistry.register(onlinePlayer);
        }

        this.spawnerHandler = new SpawnerHandler(this);

        CustomBlockData.registerListener(this);

        parseOldSpawners().thenRun(() -> {
            commandManager.register(new GiveSpawnerCommand(this));
            commandManager.register(new GiveBoosterCommand(this));
            commandManager.register(new CheckBoosterCommand(this));
            commandManager.register(new ReloadCommand(this));
            registerListener(new BlockPlaceListener(this));
            registerListener(new ConnectionListener(this));
            registerListener(new InteractListener(this));
            registerListener(new BlockBreakListener(this));
            registerListener(new EntitySpawnListener(this));
            setupRangeTask();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<Void> parseOldSpawners() {
        return CompletableFuture.runAsync(() -> {
            SpawnerFile spawnerFile = fileDatabase.getFileManager().getSpawnerFile();
            FileConfiguration config = spawnerFile.getConfiguration();

            for (String key : config.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection(key);

                if (section == null) {
                    continue;
                }

                SpawnerData spawnerData = spawnerDataRegistry.supply(section.getString("spawner-id"));
                spawnerData.setStackSize(section.getInt("spawner-stack"));
                spawnerData.setStorage(section.getInt("spawner-storage"));
                spawnerData.setStoredXp(section.getInt("spawner-xp"));

                Location location = parseLocation(section.getConfigurationSection("location"));

                if (location == null) {
                    continue;
                }

                Block block = location.getBlock();
                Material material = block.getType();
                Material spawnerMaterial = spawnerData.getBlockItem().getType();
                Block relativeX = block.getRelative(1, 0, 0);
                Block relativeZ = block.getRelative(0, 0, 1);

                if (material != spawnerMaterial && relativeX.getType() != spawnerMaterial && relativeZ.getType() != spawnerMaterial) {
                    System.out.println("Spawner [" + key + "] at [" + location.getX() + " , " + location.getY() + " , " + location.getZ() +
                            "] is not a spawner, removing from file.");
                    spawnerFile.getConfiguration().set(key, null);
                    spawnerFile.save();
                    continue;
                }

                InstancedSpawner spawner = new InstancedSpawner(this, UUID.fromString(key), spawnerData, location);
                System.out.println("Loaded spawner " + spawner.getSpawnerId() + " at " + spawner.getBlockLocation());
                spawnerRegistry.addSpawner(spawner);
            }
        });
    }

    public void reload() {
        if (rangeTask != null) {
            rangeTask.cancel();
        }

        CompletableFuture.runAsync(() -> {
            for (InstancedSpawner spawner : spawnerRegistry.getSpawnerList()) {
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

            spawnerRegistry.clear();
        }).thenRun(() -> {
            reloadConfig();
            HandlerList.unregisterAll(this);
            onEnable();

        });
    }

    @Override
    public void onDisable() {
        CompletableFuture.runAsync(() -> {
            for (InstancedSpawner spawner : spawnerRegistry.getSpawnerList()) {
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
        });
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
        rangeTask = new ScheduleBuilder(this).every(1).seconds().run(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Location location = onlinePlayer.getLocation();
                PlayerData playerData = playerDataRegistry.get(onlinePlayer.getUniqueId());

                if (playerData == null) {
                    continue;
                }

                for (InstancedSpawner allSpawner : spawnerRegistry.getSpawnerList()) {

                    if (allSpawner.getBlockLocation().distance(location) < pluginSettings.getSpawnerRange()) {
                        allSpawner.addPlayerInRange(playerData);
                    } else {
                        allSpawner.removePlayerInRange(playerData);
                    }
                }
            }
        }).sync().start();
    }

    public Location parseLocation(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        return new Location(Bukkit.getWorld(section.getString("location-world")), section.getDouble("location-x"), section.getDouble("location-y"), section.getDouble("location-z"));
    }
}
