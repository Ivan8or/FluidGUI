package ivan8or.fluidgui.parse;

import ivan8or.fluidgui.components.transition.Frame;
import ivan8or.fluidgui.components.transition.Transition;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransitionAliasParser {

    final private Map<String, List<Frame>> aliases;

    public TransitionAliasParser(Plugin p) {
        this.aliases = new HashMap<>();
    }
}
