package ivan8or.fluidgui.components.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransitionAnimator {

    TransitionAnimator next;

    public final List<Frame> compile(Map<String,Object> context) {

        List<Frame> toReturn = determineFrames(context);

        if(toReturn == null)
            toReturn = new ArrayList<>();

        if (next != null)
            toReturn.addAll(next.compile(context));

        return toReturn;
    }

    public final void chainAnimator(TransitionAnimator animator) {
        next = animator;
    }

    // to be extended via a custom FrameAnimator class
    public List<Frame> determineFrames(Map<String, Object> context) {
        List<Frame> toReturn = new ArrayList<>();

        return toReturn;
    }

}
