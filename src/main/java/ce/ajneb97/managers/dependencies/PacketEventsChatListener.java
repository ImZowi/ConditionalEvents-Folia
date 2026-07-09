package ce.ajneb97.managers.dependencies;

import ce.ajneb97.ConditionalEvents;
import ce.ajneb97.managers.chatevents.ReceiveChatEvent;
import ce.ajneb97.listeners.PaperEventsListener;
import ce.ajneb97.managers.chatevents.ReceiveChatMessageType;
import ce.ajneb97.model.CEEvent;
import ce.ajneb97.model.EventType;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisguisedChat;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PacketEventsChatListener implements PacketListener {

    private ConditionalEvents plugin;
    public PacketEventsChatListener(ConditionalEvents plugin){
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Component component = getComponent(event);

        if(component != null){
            ArrayList<CEEvent> validEvents = plugin.getEventsManager().getValidEvents(EventType.PLAYER_RECEIVE_CHAT);
            if(validEvents.isEmpty()){
                return;
            }

            String jsonMessage = GsonComponentSerializer.gson().serialize(component);
            String normalMessage = MiniMessage.miniMessage().serialize(component);
            String normalMessageWithoutColorCodes = PlainTextComponentSerializer.plainText().serialize(component);
            executeEvent(event.getPlayer(),jsonMessage,normalMessage,normalMessageWithoutColorCodes,event);
        }
    }

    @Nullable
    private static Component getComponent(PacketSendEvent event) {
        Component component = null;
        if(event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE){
            WrapperPlayServerSystemChatMessage packet = new WrapperPlayServerSystemChatMessage(event);
            component = packet.getMessage();
        }else if(event.getPacketType() == PacketType.Play.Server.DISGUISED_CHAT) {
            WrapperPlayServerDisguisedChat packet = new WrapperPlayServerDisguisedChat(event);
            component = packet.getMessage();
        }
        return component;
    }

    private void executeEvent(Player player, String jsonMessage, String normalMessage, String normalMessageWithoutColorCodes, PacketSendEvent event){
        ReceiveChatEvent messageEvent = PaperEventsListener.initReceiveChatEvent(player,jsonMessage,normalMessage,normalMessageWithoutColorCodes,
                ReceiveChatMessageType.SERVER_MESSAGE);
        if(messageEvent.isCancelled()){
            event.setCancelled(true);
        }
    }
}
