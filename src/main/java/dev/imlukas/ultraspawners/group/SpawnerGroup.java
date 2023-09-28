package dev.imlukas.ultraspawners.group;

import dev.imlukas.ultraspawners.UltraSpawnersPlugin;
import dev.imlukas.ultraspawners.data.PlayerData;
import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.impl.InstancedSpawner;
import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import dev.imlukas.ultraspawners.utils.schedulerutil.builders.ScheduleBuilder;

import java.util.ArrayList;
import java.util.List;

public class SpawnerGroup {

    private final UltraSpawnersPlugin plugin;

    private final List<InstancedSpawner> spawnerList = new ArrayList<>();

    private final String groupIdentifier;
    private final SpawnerData spawnerData;

    private ScheduledTask storageTask;

    public SpawnerGroup(UltraSpawnersPlugin plugin, SpawnerData data) {
        this.groupIdentifier = data.getIdentifier();
        this.spawnerData = data;
        this.plugin = plugin;

        storageTask = setupTask();
    }

    private ScheduledTask setupTask() {
        return new ScheduleBuilder(plugin).every(spawnerData.getTimePerCycle().asTicks()).ticks().run(() -> {
            for (InstancedSpawner spawner : spawnerList) {
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
            }
        }).sync().start();
    }

    public List<InstancedSpawner> getSpawnerList() {
        return spawnerList;
    }

    public String getGroupIdentifier()  {
        return groupIdentifier;
    }

    public void addSpawner(InstancedSpawner spawner) {
        spawnerList.add(spawner);
    }

    public void removeSpawner(InstancedSpawner spawner) {
        spawnerList.remove(spawner);
    }

    public void reload() {
        storageTask.cancel();
        storageTask = setupTask();
    }
}
