package ivan8or.fluidgui.components;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class Slide {

    private final String slideName;
    private final Map<String, Transition> transitions;

    public Slide(String slideName) {
        this.slideName = slideName;
        transitions = new HashMap<>();
    }

    public String getName() {
        return slideName;
    }

    public boolean hasResponse(String itemID) {

        return transitions.containsKey(itemID);
    }

    public void addTransition(String itemID, Transition response) {
        transitions.put(itemID, response);
    }

    public int getStaticTime(String itemID) {
        return transitions.get(itemID).getConstantDelay();
    }

    public String resultSlide(String itemID) {
        return transitions.get(itemID).getEndID();
    }

    // starts the transition to another slide
    // returns the total time it will take for the transition
    public int start(String itemID, Inventory inv, Map<String, Object> context, Plugin plugin) {
        try {
            return transitions.get(itemID).start(inv, context, false, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
