package ce.ajneb97.tasks;

import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.api.FoliaAPI;

public class PlayerDataSaveTask {

	private ConditionalEvents plugin;
	private boolean end;
	public PlayerDataSaveTask(ConditionalEvents plugin) {
		this.plugin = plugin;
		this.end = false;
	}

	public void end() {
		end = true;
	}

	public void start(int minutes) {
		long ticks = minutes*60*20;

		FoliaAPI.runTaskTimerAsync(plugin, task -> {
			if(end) {
				FoliaAPI.cancelTask(task);
			}else {
				execute();
			}
		}, 0L, ticks);
	}

	public void execute() {
		plugin.getConfigsManager().getPlayerConfigsManager().saveConfigs();
	}
}