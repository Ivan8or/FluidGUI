package ivan8or.fluidgui.components.presentation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Presentation {

    public final static String HEAD_NAME = "__head__";
    public final static String IGNORE_NAME = "__ignore__";
    private final Plugin plugin;

    private final Map<String, Slide> slides;
    private Slide current;

    private final Inventory inv;
    private final Map<String, Object> context;

    private boolean transitioning = false;

    public Presentation(Plugin plugin, String title) {
        inv = Bukkit.createInventory(null, 54, title);

        slides = new HashMap<>();
        context = new HashMap<>();
        this.plugin = plugin;
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

    // transition to the goal slide
    public void transition(String goalSlide) {

        if (transitioning)
            return;

        if (current == null || goalSlide.equals(IGNORE_NAME) || !current.hasResponse(goalSlide))
            return;

        transitioning = true;
        int totalLength = current.start(goalSlide, inv, context, plugin);

        new BukkitRunnable() {
            public void run() {
                transitioning = false;
                setCurrentSlide(goalSlide);
            }
        }.runTaskLater(plugin, totalLength+1);
    }

    public void start(Player p) {
        current = slides.get(HEAD_NAME);
        p.openInventory(inv);
        transition(HEAD_NAME);
    }
}
