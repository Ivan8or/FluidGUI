package ivan8or.nagui.components.transition;

import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

public class TransitionRunner extends BukkitRunnable {

    private int runningFrameDelay = 0;

    final private List<Frame> clonedFrames = new LinkedList<>();
    private final boolean useDelay;
    private final Inventory drawTo;

    public TransitionRunner(List<Frame> originalFrames, Inventory drawTo, boolean useDelay) {
        clonedFrames.addAll(originalFrames);
        this.useDelay = useDelay;
        this.drawTo = drawTo;
    }

    @Override
    public void run() {
        while (!clonedFrames.isEmpty() && (useDelay || runningFrameDelay == 0)) {
            Frame to_draw = clonedFrames.remove(0);
            to_draw.draw(drawTo);
            if (!useDelay && !clonedFrames.isEmpty())
                runningFrameDelay = clonedFrames.get(0).getDelay();
        }
        if (clonedFrames.isEmpty())
            this.cancel();
        else
            runningFrameDelay--;
    }
}