package ivan8or.fluidgui.components.transition;

import org.apache.commons.lang.ObjectUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Transition {

    // shared thread pool to run async actions
    protected static final ExecutorService executor = Executors.newCachedThreadPool();

    // the slide this transition points to
    private final String targetSlideName;

    // the unchanging, constant frames provided to this transition
    private final List<Frame> constantFrames;

    // an animator to produce on-the-spot dynamic frames for the transition
    private TransitionAnimator compiler;

    // a runnable action which this transition calls when it is invoked
    private TransitionAction task;

    // create a new transition for a slide, with no task to run and with no dynamic frames
    public Transition(String slideInto, List<Frame> frames) {
        this.targetSlideName = slideInto;
        this.constantFrames = frames;
        this.compiler = new TransitionAnimator();
    }

    // create a new transition for a slide, with a parameter for a task tor un and for dynamic frames
    public Transition(String slideInto, List<Frame> frames, TransitionAction task, TransitionAnimator compiler) {
        this.targetSlideName = slideInto;
        this.constantFrames = frames;
        this.task = task;
        this.compiler = Objects.requireNonNullElseGet(compiler, TransitionAnimator::new);
    }

    public void setAnimator(TransitionAnimator newAnimator) {
        compiler = newAnimator;
    }

    public void setAction(TransitionAction newAction) {
        task = newAction;
    }

    // run the task associated with this transition (sync or async)
    private void runAction() throws Exception {
        if (task == null)
            return;

        if (task.synchronous())
            task.call();
        else
            executor.submit(task);
    }


    // starts the transition process to the new slide
    // replaces the current item frames for new ones the new slide will contain
    // 'useDelay' parameter decides if the specified frame delays are to be floolwed or ignored
    public int start(Inventory inv, Map<String, Object> context, boolean useDelay, Plugin plugin)
            throws Exception {

        // run the GUICallable if one exists
        runAction();

        // start displaying the constant frames to the user
        new TransitionRunner(constantFrames, inv, useDelay)
                .runTaskTimer(plugin, 0, 1);

        // start generating the dynamic frames to be shown after the constant frames run out
        Future<List<Frame>> dynamicFrames = executor.submit(() -> compiler.compile(context));
        int constantsDelay = getDelay(constantFrames);

        // display the dynamic frames after the constant frames run out
        new TransitionRunner(dynamicFrames.get(), inv, useDelay)
                .runTaskTimer(plugin, constantsDelay, 1);

        int dynamicsDelay = getDelay(dynamicFrames.get());

        // return the total delay of the transition
        return constantsDelay + dynamicsDelay;
    }

    // gets the total delay for a sequence of frames
    private int getDelay(List<Frame> frames) {
        OptionalInt oint = frames.stream()
                .mapToInt(Frame::getDelay) // get delay from each frame
                .reduce(Integer::sum); // sum up all of the delays

        if (oint.isPresent())
            return oint.getAsInt();
        return 0;
    }

    public String getEndID() {
        return targetSlideName;
    }

}
