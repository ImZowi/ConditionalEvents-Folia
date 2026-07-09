package ce.ajneb97.managers.dependencies;

import ce.ajneb97.ConditionalEvents;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

public class PacketEventsManager {

    private ConditionalEvents plugin;
    public PacketEventsManager(ConditionalEvents plugin){
        this.plugin = plugin;
    }

    public void init(){
        EventManager events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new PacketEventsChatListener(plugin), PacketListenerPriority.NORMAL);
    }
}
