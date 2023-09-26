package dev.imlukas.ultraspawners.utils.schedulerutil;

import dev.imlukas.ultraspawners.utils.schedulerutil.data.ScheduleData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ScheduledTask {

    private final ScheduleData data;
    private final JavaPlugin plugin;
    private final List<Runnable> cancelTasks = new ArrayList<>();
    private boolean running = false;
    @Getter
    private int id = 0;

    public ScheduledTask(JavaPlugin plugin, ScheduleData data) {
        this.plugin = plugin;
        this.data = data;
    }

    public ScheduledTask start() {
        if (running) {
            return this;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();

        if (data.isSync()) {
            if (data.isRepeating()) {
                id = scheduler.scheduleSyncRepeatingTask(plugin, data.getRunnable(),
                        data.getTicks(),
                        data.getTicks());
            } else {
                id = scheduler.scheduleSyncDelayedTask(plugin, data.getRunnable(), data.getTicks());
            }
        } else {
            if (data.isRepeating()) {
                id = scheduler.runTaskTimerAsynchronously(plugin, data.getRunnable(),
                        data.getTicks(),
                        data.getTicks()).getTaskId();
            } else {
                id = scheduler.runTaskLaterAsynchronously(plugin, data.getRunnable(),
                                data.getTicks())
                        .getTaskId();
            }
        }

        if (data.getCancelIf() != null) {
            if (data.getCancelIf().getAsBoolean()) {
                scheduler.scheduleSyncDelayedTask(plugin, this::cancel, 1);
            }
        }

        if (data.getCancelIn() != -1) {
            scheduler.scheduleSyncDelayedTask(plugin, this::cancel, data.getCancelIn());
        }

        running = true;
        return this;
    }

    public ScheduledTask onCancel(Runnable runnable) {
        cancelTasks.add(runnable);
        return this;
    }

    public ScheduledTask cancelIf(BooleanSupplier predicate) {
        data.setCancelIf(predicate);
        return this;
    }

    public void cancel() {
        if (!running) {
            return;
        }

        running = false;

        Bukkit.getScheduler().cancelTask(id);

        cancelTasks.forEach(Runnable::run);
    }

}
