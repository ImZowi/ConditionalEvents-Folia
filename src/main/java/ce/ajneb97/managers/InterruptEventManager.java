package ce.ajneb97.managers;

import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.model.internal.WaitActionTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import java.util.*;

public class InterruptEventManager {
    private ConditionalEvents plugin;
    private ArrayList<WaitActionTask> tasks;

    public InterruptEventManager(ConditionalEvents plugin){
        this.plugin = plugin;
        this.tasks = new ArrayList<>();
    }

    public void addTask(String playerName, String eventName, ScheduledTask scheduledTask){
        tasks.add(new WaitActionTask(playerName,eventName,scheduledTask));
    }

    public void removeTask(ScheduledTask task) {
        tasks.removeIf(t -> t.getTask().equals(task));
    }

    // Interrupt actions for a specific event, globally or per player
    public void interruptEvent(String eventName, String playerName) {
        Iterator<WaitActionTask> it = tasks.iterator();
        while (it.hasNext()) {
            WaitActionTask task = it.next();
            boolean matches = playerName == null ? task.getEventName().equals(eventName) : playerName.equals(task.getPlayerName()) && task.getEventName().equals(eventName);
            if (matches) {
                task.getTask().cancel();
                it.remove();
            }
        }
    }
}
