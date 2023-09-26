package dev.imlukas.ultraspawners.utils.schedulerutil.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BooleanSupplier;

@Getter
@Setter
public class ScheduleData {

    private boolean sync;
    private long ticks;
    private Runnable runnable;
    private boolean repeating;
    private long cancelIn = -1;
    private BooleanSupplier cancelIf;

    private JavaPlugin plugin;

}
