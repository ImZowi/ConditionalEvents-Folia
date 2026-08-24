package ce.ajneb97.model.verify;

import ce.ajneb97.api.ConditionalEventsAPI;
import ce.ajneb97.utils.JSONMessage;
import ce.ajneb97.utils.JSONMessageAdventure;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CEErrorAction extends CEError{

    private int actionLine;
    private String actionGroup;

    public CEErrorAction(String event, String errorText, int actionLine, String actionGroup) {
        super(event, errorText);
        this.actionLine = actionLine;
        this.actionGroup = actionGroup;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<String>();
        boolean isPaper = ConditionalEventsAPI.getPlugin().getDependencyManager().isPaper();
        if(isPaper){
            String message = "<red>⚠ ";
            JSONMessageAdventure jsonMessage = new JSONMessageAdventure(player,message+"<gray>Action <gold>"+actionLine+" <gray>on Action group <gold>"
                    +actionGroup+" <gray>on Event <gold>"+event+" <gray>is not valid.");
            hover.add("<yellow>THIS IS AN ERROR!");
            hover.add("<white>The action defined for this event");
            hover.add("<white>is probably not formatted correctly:");
            for(String m : getFixedErrorText()) {
                hover.add("<red>"+m);
            }
            hover.add(" ");
            hover.add("<white>Remember to use a valid actions from this list:");
            hover.add("<green>https://ajneb97.gitbook.io/conditionalevents/actions");
            jsonMessage.hover(hover).send();
        }else{
            String message = "&c⚠ ";
            JSONMessage jsonMessage = new JSONMessage(player,message+"&7Action &6"+actionLine+" &7on Action group &6"
                    +actionGroup+" &7on Event &6"+event+" &7is not valid.");
            hover.add("&eTHIS IS AN ERROR!");
            hover.add("&fThe action defined for this event");
            hover.add("&fis probably not formatted correctly:");
            for(String m : getFixedErrorText()) {
                hover.add("&c"+m);
            }
            hover.add(" ");
            hover.add("&fRemember to use a valid actions from this list:");
            hover.add("&ahttps://ajneb97.gitbook.io/conditionalevents/actions");
            jsonMessage.hover(hover).send();
        }

    }

}
