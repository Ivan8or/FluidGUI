package ivan8or.fluidgui.parse.aliases;

import ivan8or.fluidgui.components.transition.Frame;
import ivan8or.fluidgui.parse.Parser;
import ivan8or.fluidgui.parse.transitions.TransitionParser;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class AliasParser extends Parser {
    final private Map<String, List<Frame>> transitionAliases;

    // key is the required alias, value is all queued aliases that depend on it
    final private Map<String, Set<QueuedTransitionAlias>> queuedTransitionAliases;

    final private Plugin plugin;
    final private NamespacedKey key;

    public AliasParser(Plugin plugin, NamespacedKey key) {
        this.transitionAliases = new HashMap<>();
        this.queuedTransitionAliases = new HashMap<>();
        this.plugin = plugin;
        this.key = key;
    }

    public List<Frame> getAlias(String name) {
        return transitionAliases.get(name);
    }

    public Set<String> getAliasNames() {
        return transitionAliases.keySet();
    }

    public void parse(Map<String, Object> root) {
        if (!root.containsKey("aliases"))
            return;
        Map<String, Object> aliasList = (Map<String, Object>) root.get("aliases");

        if (aliasList.containsKey("transitions")) {
            Map<String, Object> transitionsList = (Map<String, Object>) aliasList.get("transitions");
            addAllTransitionAliases(transitionsList);
        }
    }

    private void addAllTransitionAliases(Map<String, Object> transitionsList) {
        Set<String> aliasesToAdd = transitionsList.keySet();

        for (String aliasName : aliasesToAdd) {
            List<Map<String, Object>> components = (List<Map<String, Object>>) transitionsList.get(aliasName);
            loadTransitionAlias(aliasName, components);
        }
    }

    private void loadTransitionAlias(String aliasName, List<Map<String, Object>> components) {
        Set<String> neededDependencies = TransitionParser.getTransitionDependencies(components);
        neededDependencies.removeAll(transitionAliases.keySet());
        QueuedTransitionAlias queuedAlias = new QueuedTransitionAlias(aliasName, components, neededDependencies);
        loadTransitionAlias(queuedAlias);
    }


    private void loadTransitionAlias(QueuedTransitionAlias queuedAlias) {

        String aliasName = queuedAlias.getAliasName();
        if(transitionAliases.containsKey(aliasName))
            return;

        if(queuedAlias.canBeLoaded()) {
            List<Frame> cumulativeFrames = TransitionParser.accumulateTransition(
                    queuedAlias.getComponents(),
                    this,
                    key);
            transitionAliases.put(aliasName, cumulativeFrames);

            for(QueuedTransitionAlias otherQueued: queuedTransitionAliases.getOrDefault(
                    aliasName,
                    new HashSet<>())) {
                otherQueued.removeNeeded(aliasName);
                loadTransitionAlias(otherQueued);
            }
        }
        else {
            for(String dependency: queuedAlias.neededDependencies()) {
                Set<QueuedTransitionAlias> queuedAliases = queuedTransitionAliases.getOrDefault(
                        dependency,
                        new HashSet<>());
                queuedAliases.add(queuedAlias);
                queuedTransitionAliases.put(dependency, queuedAliases);
            }
        }
    }


}
