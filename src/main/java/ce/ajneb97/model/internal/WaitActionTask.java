package ce.ajneb97.model.internal;

public class WaitActionTask {
    private String playerName;
    private String eventName;
    private Object task;

    public WaitActionTask(String playerName, String eventName, Object task) {
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

    public Object getTask() {
        return task;
    }

    public void setTask(Object task) {
        this.task = task;
    }
}