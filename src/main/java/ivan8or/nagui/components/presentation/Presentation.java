package ivan8or.nagui.components.presentation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Presentation {

    public final static String HEAD_NAME = "__head__";
    public final static String INIT_ITEM_ID = "init";
    public final static String EMPTY_ID = "void";
    private final Plugin plugin;

    private final Map<String, Slide> slides;
    private Slide current;

    private final Inventory inv;
    private final String title;
    private final Map<String, Object> context;

    private boolean transitioning = false;

    public Presentation(Plugin plugin, String title) {
        inv = Bukkit.createInventory(null, 54, title);
        this.title = title;

        slides = new HashMap<>();
        context = new HashMap<>();
        this.plugin = plugin;
    }

    public Presentation clone() {
        Presentation cloned = new Presentation(plugin, title);
        cloned.slides.putAll(slides);
        return cloned;
    }

    public void addSlide(Slide slide) {
        slides.put(slide.getName(), slide);
    }

    public void setCurrentSlide(String slideName) {
        current = slides.get(slideName);
    }

    public Map<String, Object> getContext() {
        return context;
    }

    // respond to the provided item ID
    public void transition(String itemID) {

        if (transitioning)
            return;

        if (current == null || !current.hasResponse(itemID))
        if (current == null || !current.hasResponse(itemID))
            return;

        transitioning = true;
        int totalLength = current.start(itemID, inv, context, plugin);

        new BukkitRunnable() {
            public void run() {
                transitioning = false;
                String newSlide = current.resultSlide(itemID);
                setCurrentSlide(newSlide);
            }
        }.runTaskLater(plugin, totalLength+1);
    }

    public void start(Player p) {
        current = slides.get(HEAD_NAME);
        p.openInventory(inv);
        transition(INIT_ITEM_ID);
    }
}
