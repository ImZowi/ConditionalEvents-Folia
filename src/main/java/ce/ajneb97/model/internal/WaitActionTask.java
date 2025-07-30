package ce.ajneb97.model.internal;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class WaitActionTask {
    private String playerName;
    private String eventName;
    private ScheduledTask task;

    public WaitActionTask(String playerName, String eventName, ScheduledTask task) {
        this.playerName = playerName;
        this.eventName = eventName;
        this.task = task;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ScheduledTask getTask() {
        return task;
    }

    public void setTask(ScheduledTask task) {
        this.task = task;
    }
}
