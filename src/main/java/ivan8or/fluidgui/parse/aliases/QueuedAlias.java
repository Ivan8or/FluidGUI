package ivan8or.fluidgui.parse.aliases;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueuedAlias {

    final private String aliasName;
    final private List<Map<String, Object>> components;

    final private Set<String> neededDependencies;

    public QueuedAlias(String name, List<Map<String, Object>> components, Set<String> neededAliases) {
        this.aliasName = name;
        this.components = components;
        neededDependencies = neededAliases;
    }

    public boolean canBeLoaded() {
        return neededDependencies.isEmpty();
    }

    public List<Map<String, Object>> getComponents() {
        return components;
    }

    public String getAliasName() {
        return aliasName;
    }

    public Set<String> neededDependencies() {
        return neededDependencies;
    }

    public void removeNeeded(String loadedAlias) {
        neededDependencies.remove(loadedAlias);
    }
}
