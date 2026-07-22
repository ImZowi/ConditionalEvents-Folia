package ce.ajneb97.model.verify;

import ce.ajneb97.api.ConditionalEventsAPI;
import ce.ajneb97.utils.JSONMessage;
import ce.ajneb97.utils.JSONMessageAdventure;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CEErrorCondition extends CEError{

    private int conditionLine;

    public CEErrorCondition(String event, String errorText,int conditionLine) {
        super(event, errorText);
        this.conditionLine = conditionLine;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<String>();
        boolean isPaper = ConditionalEventsAPI.getPlugin().getDependencyManager().isPaper();
        if(isPaper){
            String message = "<yellow>⚠ ";
            JSONMessageAdventure jsonMessage = new JSONMessageAdventure(player,message+"<gray>Condition <gold>"+conditionLine+" <gray>on Event <gold>"+event+" <gray>is not valid.");
            hover.add("<yellow>THIS IS A WARNING!");
            hover.add("<white>The condition defined for this event");
            hover.add("<white>is probably not formatted correctly:");
            for(String m : getFixedErrorText()) {
                hover.add("<red>"+m);
            }
            hover.add(" ");
            hover.add("<white>Remember to use a valid condition from this list:");
            hover.add("<green>https://ajneb97.gitbook.io/conditionalevents/conditions");
            jsonMessage.hover(hover).send();
        }else{
            String message = "&e⚠ ";
            JSONMessage jsonMessage = new JSONMessage(player,message+"&7Condition &6"+conditionLine+" &7on Event &6"+event+" &7is not valid.");
            hover.add("&eTHIS IS A WARNING!");
            hover.add("&fThe condition defined for this event");
            hover.add("&fis probably not formatted correctly:");
            for(String m : getFixedErrorText()) {
                hover.add("&c"+m);
            }
            hover.add(" ");
            hover.add("&fRemember to use a valid condition from this list:");
            hover.add("&ahttps://ajneb97.gitbook.io/conditionalevents/conditions");
            jsonMessage.hover(hover).send();
        }

    }

}
