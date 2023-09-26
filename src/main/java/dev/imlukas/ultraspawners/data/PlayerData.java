package dev.imlukas.ultraspawners.data;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.registry.DefaultRegistry;
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UltraSpawnersPlugin plugin;
    private final UUID playerId;
    private boolean isBoosted;
    private long boostDuration;
    private ScheduledTask boosterTask;

    public PlayerData(UltraSpawnersPlugin plugin, UUID playerId) {
        this.plugin = plugin;
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public void setBoosted(boolean isBoosted, long duration) {
        this.isBoosted = isBoosted;
        boostDuration = duration;
        setupTask();
    }

    public void setupTask() {
        boosterTask = new ScheduleBuilder(plugin).every(1).seconds().run(() -> {
            boostDuration--;

            if (boostDuration <= 0) {
                isBoosted = false;
                boosterTask.cancel();
            }
        }).sync().start();
    }

    public long getBoostDuration() {
        return boostDuration;
    }
}
