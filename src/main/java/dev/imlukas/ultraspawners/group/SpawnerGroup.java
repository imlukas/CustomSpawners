package dev.imlukas.ultraspawners.group;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;

import java.util.List;

public class SpawnerGroup {

    private final ScheduledTask storageTask;

    public SpawnerGroup(UltraSpawnersPlugin plugin, SpawnerData data) {
        storageTask = new ScheduleBuilder(plugin).every(data.getTimePerCycle().asTicks()).ticks().run(() -> {
            plugin.getSpawnerRegistry().getSpawnersByIdentifier(data.getIdentifier()).forEach(spawner -> {
                SpawnerData spawnerData = spawner.getSpawnerData();
                List<PlayerData> players = spawner.getPlayersInRange();

                if (players.isEmpty() || !spawnerData.isActive()) {
                    return;
                }

                spawnerData.setActive(true);
                spawnerData.setBoosted(false);

                for (PlayerData playerData : players) {
                    if (playerData.isBoosted()) {
                        spawnerData.setBoosted(true);
                    }
                }

                spawnerData.addStorage();
                spawnerData.addXp(1);
            });
        }).sync().start();
    }

    public void dispose() {
        storageTask.cancel();
    }
}
