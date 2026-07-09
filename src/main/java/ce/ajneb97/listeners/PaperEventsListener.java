package ce.ajneb97.listeners;

import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.api.ConditionalEventsAPI;
import ce.ajneb97.managers.chatevents.ReceiveChatEvent;
import ce.ajneb97.managers.chatevents.ReceiveChatMessageType;
import ce.ajneb97.model.CEEvent;
import ce.ajneb97.model.EventType;
import ce.ajneb97.model.StoredVariable;
import ce.ajneb97.model.internal.ConditionEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Iterator;

public class PaperEventsListener implements Listener {

    public ConditionalEvents plugin;
    public PaperEventsListener(ConditionalEvents plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJump(PlayerJumpEvent event){
        Player player = event.getPlayer();
        Location locationFrom = event.getFrom();
        Location locationTo = event.getTo();

        ConditionEvent conditionEvent = new ConditionEvent(plugin, player, event, EventType.PLAYER_JUMP, null);
        if(!conditionEvent.containsValidEvents()) return;
        conditionEvent.addVariables(
               new StoredVariable("%from_x%",locationFrom.getX()+""),
               new StoredVariable("%from_y%",locationFrom.getY()+""),
               new StoredVariable("%from_z%",locationFrom.getZ()+""),
               new StoredVariable("%from_world%",locationFrom.getWorld().getName()),
               new StoredVariable("%from_yaw%",locationFrom.getYaw()+""),
               new StoredVariable("%from_pitch%",locationFrom.getPitch()+""),
               new StoredVariable("%to_x%",locationTo.getX()+""),
               new StoredVariable("%to_y%",locationTo.getY()+""),
               new StoredVariable("%to_z%",locationTo.getZ()+""),
               new StoredVariable("%to_world%",locationTo.getWorld().getName()),
               new StoredVariable("%to_yaw%",locationTo.getYaw()+""),
               new StoredVariable("%to_pitch%",locationTo.getPitch()+"")
        ).checkEvent();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatReceive(AsyncChatEvent event){
        // Someone sends a message
        if(event.isCancelled()){
            return;
        }

        ArrayList<CEEvent> validEvents = plugin.getEventsManager().getValidEvents(EventType.PLAYER_RECEIVE_CHAT);
        if(validEvents.isEmpty()){
            return;
        }

        Component message = event.message();
        String jsonMessage = GsonComponentSerializer.gson().serialize(message);
        String normalMessage = MiniMessage.miniMessage().serialize(message);
        String normalMessageWithoutColorCodes = PlainTextComponentSerializer.plainText().serialize(message);

        Iterator<Audience> iterator = event.viewers().iterator();
        while (iterator.hasNext()) {
            // Player receives the message
            Audience audience = iterator.next();
            if (!(audience instanceof Player)) {
                continue;
            }

            Player player = (Player) audience;
            ReceiveChatEvent messageEvent = initReceiveChatEvent(player,jsonMessage,normalMessage,normalMessageWithoutColorCodes,ReceiveChatMessageType.PLAYER_MESSAGE);
            if(messageEvent.isCancelled()) {
                iterator.remove();
            }
        }
    }

    public static ReceiveChatEvent initReceiveChatEvent(Player player, String jsonMessage, String normalMessage, String normalMessageWithoutColorCodes,
                                                        ReceiveChatMessageType messageType){
        ReceiveChatEvent messageEvent = new ReceiveChatEvent(player,jsonMessage,normalMessage);
        ConditionEvent conditionEvent = new ConditionEvent(ConditionalEventsAPI.getPlugin(), player, messageEvent, EventType.PLAYER_RECEIVE_CHAT, null);
        conditionEvent.addVariables(
                new StoredVariable("%json_message%",jsonMessage),
                new StoredVariable("%normal_message%",normalMessage),
                new StoredVariable("%normal_message_without_color_codes%", normalMessageWithoutColorCodes),
                new StoredVariable("%message_type%",messageType.name())
        ).checkEvent();
        return messageEvent;
    }
}
