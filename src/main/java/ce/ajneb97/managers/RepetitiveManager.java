package ce.ajneb97.managers;

import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.model.CEEvent;
import ce.ajneb97.model.EventType;
import ce.ajneb97.model.internal.ConditionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RepetitiveManager {
    private ConditionalEvents plugin;
    private CEEvent ceEvent;
    private long ticks;
    private boolean mustEnd;
    private boolean started;
    private ScheduledExecutorService scheduler;

    public RepetitiveManager(ConditionalEvents plugin, CEEvent ceEvent, long ticks){
        this.plugin = plugin;
        this.ceEvent = ceEvent;
        this.ticks = ticks;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public boolean isStarted() {
        return started;
    }

    public void end() {
        this.mustEnd = true;
        this.started = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void start(){
        this.mustEnd = false;
        this.started = true;
        long ms = (ticks * 1000) / 20;
        scheduler.scheduleAtFixedRate(() -> {
            if (mustEnd || !execute()) {
                end();
            }
        }, 0, ms, TimeUnit.MILLISECONDS);
    }

    public boolean execute(){
        if (ceEvent == null) {
            return false;
        }
        EventsManager eventsManager = plugin.getEventsManager();
        CompletableFuture.runAsync(() -> {
            if (ceEvent.getEventType().equals(EventType.REPETITIVE)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ConditionEvent conditionEvent = new ConditionEvent(plugin, player, null, EventType.REPETITIVE, null);
                    conditionEvent.setAsync(true);
                    eventsManager.checkSingularEvent(conditionEvent,ceEvent);
                }
            } else {
				// Repetitive server
                ConditionEvent conditionEvent = new ConditionEvent(plugin, null, null, EventType.REPETITIVE_SERVER, null);
                conditionEvent.setAsync(true);
                eventsManager.checkSingularEvent(conditionEvent,ceEvent);
            }
        });
        
        return true;
    }
}