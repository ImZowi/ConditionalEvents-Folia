package ce.ajneb97.tasks;

import ce.ajneb97.ConditionalEvents;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerDataSaveTask {

	private ConditionalEvents plugin;
	private boolean end;
	private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	
	public PlayerDataSaveTask(ConditionalEvents plugin) {
		this.plugin = plugin;
		this.end = false;
	}
	
	public void end() {
		end = true;
	}
	
	public void start(int minutes) {
		long ticks = minutes*60*20;
		
        	exec.scheduleAtFixedRate(() -> {
           		if (end) {
        			exec.shutdownNow();
            		} else {
                		execute();
            		}
        	}, 0, ticks, TimeUnit.SECONDS);
	}
	
	public void execute() {
		plugin.getConfigsManager().getPlayerConfigsManager().savePlayerData();
	}
}
