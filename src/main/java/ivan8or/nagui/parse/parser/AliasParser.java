package ivan8or.nagui.parse.parser;

import ivan8or.nagui.parse.depend.LoadedDependency;
import ivan8or.nagui.parse.depend.QueuedDependency;
import ivan8or.nagui.parse.depend.DependencyID;
import ivan8or.nagui.parse.depend.DependencyType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

public class AliasParser extends Parser {

    final private Map<DependencyID, LoadedDependency> loadedDependencies;

    // key is some yet to be loaded depends, value is all queued depends that require it
    final private Map<DependencyID, Set<QueuedDependency>> queuedDependencies;

    final private Plugin plugin;
    final private NamespacedKey key;

    public AliasParser(Plugin plugin, NamespacedKey key) {
        this.loadedDependencies = new HashMap<>();
        this.queuedDependencies = new HashMap<>();
        this.plugin = plugin;
        this.key = key;
    }

    public LoadedDependency getDependency(DependencyType type, String name) {
        return getDependency(new DependencyID(type, name));
    }
    public LoadedDependency getDependency(DependencyID id) {
        return loadedDependencies.get(id);
    }

    public Set<DependencyID> allLoaded() {
        return loadedDependencies.keySet();
    }
    public NamespacedKey getKey() {
        return key;
    }

    public void parse(Map<String, Object> root) {

        if(root == null)
            return;

        if (!root.containsKey("aliases"))
            return;
        Map<String, Object> aliasList = (Map<String, Object>) root.get("aliases");

        if (aliasList.containsKey("items")) {
            Map<String, Object> transitionsList = (Map<String, Object>) aliasList.get("items");
            addAliasesOfType(transitionsList, DependencyType.ITEM);
        }

        if (aliasList.containsKey("transitions")) {
            Map<String, Object> transitionsList = (Map<String, Object>) aliasList.get("transitions");
            addAliasesOfType(transitionsList, DependencyType.TRANSITION);
        }
    }


    private void addAliasesOfType(Map<String, Object> itemsList, DependencyType type) {
        Set<String> aliasNames = itemsList.keySet();

        for (String name: aliasNames) {
            Object body = itemsList.get(name);
            QueuedDependency currentAlias = new QueuedDependency(type, name,  body, this);
            loadDependency(currentAlias);
        }
    }

    private void loadDependency(QueuedDependency toLoad) {

        DependencyID aliasID = toLoad.getID();
        if(loadedDependencies.containsKey(aliasID)) {
            plugin.getLogger().log(Level.SEVERE, "duplicate dependency found! "+aliasID);
            return;
        }
        plugin.getLogger().log(Level.INFO, "loading dependency "+aliasID);
        plugin.getLogger().log(Level.INFO, "still waiting on: ");
        for(DependencyID waitingOn : toLoad.getUnloadedDependencies()) {
            plugin.getLogger().log(Level.INFO, "\t"+waitingOn);
        }
        plugin.getLogger().log(Level.INFO, "(end)");
        // remove itself from all dependent items and attempt to load them
        if(toLoad.isLoadable()) {
            LoadedDependency loadedDependency = toLoad.load(this);
            loadedDependencies.put(aliasID, loadedDependency);

            Set<QueuedDependency> dependents = queuedDependencies.get(aliasID);
            if(dependents == null)
                dependents = new HashSet<>();

            for(QueuedDependency otherQueued: dependents) {
                otherQueued.checkOff(aliasID);
                if(otherQueued.isLoadable())
                    loadDependency(otherQueued);
            }
            queuedDependencies.remove(aliasID);
        }

        // add itself as a dependent to all items which are required to load this
        else {
            for(DependencyID desiredDependency: toLoad.getUnloadedDependencies()) {

                Set<QueuedDependency> queueForDesired = queuedDependencies.getOrDefault(
                        desiredDependency, new HashSet<>());

                queueForDesired.add(toLoad);
                queuedDependencies.put(desiredDependency, queueForDesired);
            }
        }
    }


}
