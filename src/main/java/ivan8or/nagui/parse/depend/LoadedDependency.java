package ivan8or.nagui.parse.depend;

import ivan8or.nagui.components.transition.Frame;

import java.util.List;

public class LoadedDependency {

    final private DependencyID did;
    final private Object loadedBody;

    public LoadedDependency(DependencyType type, String name, Object loadedBody) {
        this.did = new DependencyID(type, name);
        this.loadedBody = loadedBody;
    }

    public LoadedDependency(DependencyID did, Object loadedBody) {
        this.did = did;
        this.loadedBody = loadedBody;
    }

    public List<Frame> asTransitionAlias() {
        return (List<Frame>) loadedBody;
    }
    public ItemAlias asItemAlias() {
        return (ItemAlias) loadedBody;
    }

    public DependencyID getID() {
        return did;
    }
}
