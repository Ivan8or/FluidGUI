package ivan8or.fluidgui.parse.depend;

import ivan8or.fluidgui.parse.parser.AliasParser;
import ivan8or.fluidgui.parse.parser.ItemParser;
import ivan8or.fluidgui.parse.parser.TransitionParser;

import java.util.*;

public class QueuedDependency {

    final private DependencyID did;
    final private Object source;

    final private Set<DependencyID> unloadedDependencies;

    public QueuedDependency(DependencyType type, String name, Object source, AliasParser aliases) {
        this.unloadedDependencies = calculateNeededDependencies(aliases);
        this.did = new DependencyID(type, name);
        this.source = source;
    }

    public QueuedDependency(DependencyID did, Object source, AliasParser aliases) {
        this.unloadedDependencies = calculateNeededDependencies(aliases);
        this.did = did;
        this.source = source;
    }


    public LoadedDependency load(AliasParser aliases) {
        return switch (did.getType()) {
            case TRANSITION -> loadAsTransitionAlias(aliases);
            case ITEM -> loadAsItemAlias(aliases);
        };
    }

    private LoadedDependency loadAsItemAlias(AliasParser aliases) {
        Object resultBody = ItemParser.parseItem((Map<String, Object>) source, aliases);
        LoadedDependency loadedDepend = new LoadedDependency(did, resultBody);
        return loadedDepend;
    }

    private LoadedDependency loadAsTransitionAlias(AliasParser aliases) {
        Object resultBody = TransitionParser.accumulateTransition(
                (List<Map<String, Object>>) source,
                aliases,
                aliases.getKey());

        LoadedDependency loadedDepend = new LoadedDependency(did, resultBody);
        return loadedDepend;
    }

    public boolean isLoadable() {
        return unloadedDependencies.isEmpty();
    }

    public Set<DependencyID> getUnloadedDependencies() {
        return unloadedDependencies;
    }

    private Set<DependencyID> calculateNeededDependencies(AliasParser aliases) {
        Set<DependencyID> dependencies = new HashSet<>();
        switch (did.getType()) {
            case TRANSITION:
                dependencies.addAll(TransitionParser.getTransitionDependencies((List<Map<String, Object>>) source));
            case ITEM:
                ItemParser.getItemDependency((Map<String, Object>) source).ifPresent(dependencies::add);
        }
        dependencies.removeAll(aliases.allLoaded());
        return dependencies;
    }

    public void checkOff(DependencyID hasLoaded) {
        unloadedDependencies.remove(hasLoaded);
    }

    public DependencyID getID() {
        return did;
    }

    public Object getSource() {
        return source;
    }



}
