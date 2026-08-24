package ce.ajneb97.listeners.dependencies;

import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.model.EventType;
import ce.ajneb97.model.StoredVariable;
import ce.ajneb97.model.internal.ConditionEvent;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FanzyNpcsListener implements Listener {

    public ConditionalEvents plugin;
    public FanzyNpcsListener(ConditionalEvents plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(NpcInteractEvent event){
        Player player = event.getPlayer();
        Npc npc = event.getNpc();
        
        EventType type;
        switch (event.getInteractionType()) {
        	case RIGHT_CLICK:
        		type = EventType.FANZYNPCS_RIGHT_CLICK_NPC;
        	case LEFT_CLICK:
        		type = EventType.FANZYNPCS_LEFT_CLICK_NPC;
        	default:
        		type = EventType.FANZYNPCS_ANY_CLICK_NPC;
        }
        
        new ConditionEvent(plugin, player, event, type, null).addVariables(new StoredVariable("%npc_id%", npc.getData().getId()+""),new StoredVariable("%npc_name%", npc.getData().getName()+"")).checkEvent();
    }
}
