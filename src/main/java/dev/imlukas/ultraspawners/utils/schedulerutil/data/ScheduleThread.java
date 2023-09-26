package dev.imlukas.ultraspawners.utils.schedulerutil.data;

import dev.imlukas.ultraspawners.utils.schedulerutil.ScheduledTask;
import lombok.Getter;

@Getter
public class ScheduleThread implements ScheduleBuilderBase {

    private final ScheduleData data;

    public ScheduleThread(ScheduleData data) {
        this.data = data;
    }

    public ScheduledTask sync() {
        data.setSync(true);
        return new ScheduledTask(data.getPlugin(), data);
    }

    public ScheduledTask async() {
        data.setSync(false);
        return new ScheduledTask(data.getPlugin(), data);
    }


}
