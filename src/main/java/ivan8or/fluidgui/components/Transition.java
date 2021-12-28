package ivan8or.fluidgui.components;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Transition {

    protected static final ExecutorService executor = Executors.newCachedThreadPool();

    private final String targetSlideName;
    private final List<Frame> constantFrames;
    private final FrameAnimator compiler;
    protected GUICallable task;
    private final int constantDelay;

    // create a new transition for a slide, with no task to run and with no dynamic frames
    public Transition(String slide_id_into, List<Frame> frames) {
        this.targetSlideName = slide_id_into;
        this.constantFrames = frames;
        this.compiler = new FrameAnimator();
        constantDelay = getDelay(constantFrames);
    }

    // create a new transition for a slide, with a parameter for a task tor un and for dynamic frames
    public Transition(String slide_id_into, List<Frame> frames, GUICallable task, FrameAnimator compiler) {
        this.targetSlideName = slide_id_into;
        this.constantFrames = frames;
        this.task = task;
        constantDelay = getDelay(constantFrames);

        if (compiler != null)
            this.compiler = compiler;
        else
            this.compiler = new FrameAnimator();
    }

    // run the task associated with this transition (sync or async)
    private void callTask() {
        if (task == null)
            return;

        if (task.synchronous()) {
            try {
                task.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            executor.submit(task);
        }

    }

    // starts the transition process to the new slide
    // replaces the current item frames for new ones the new slide will contain
    // 'useDelay' parameter decides if the specified frame delays are to be floolwed or ignored
    public int start(Inventory inv, Map<String, Object> context, boolean useDelay, Plugin plugin)
            throws ExecutionException, InterruptedException {

        // run the GUICallable if one exists
        callTask();

        // start displaying the constant frames to the user
        new FrameLayerRunnable(constantFrames, inv, useDelay)
                .runTaskTimer(plugin, 0, 1);

        // start generating the dynamic frames to be shown after the constant frames run out
        Future<List<Frame>> dynamicFrames = executor.submit(() -> compiler.compile(context));

        // display the dynamic frames after the constant frames run out
        new FrameLayerRunnable(dynamicFrames.get(), inv, useDelay)
                .runTaskTimer(plugin, constantDelay, 1);

        // return the total delay of the transition
        return constantDelay + getDelay(dynamicFrames.get());
    }

    public int getConstantDelay() {
        return constantDelay;
    }
    private int getDelay(List<Frame> frames) {

        int result = 0;
        for (Frame f : frames)
            result += f.getDelay();

        return result;
    }

    public String getEndID() {
        return targetSlideName;
    }

}
