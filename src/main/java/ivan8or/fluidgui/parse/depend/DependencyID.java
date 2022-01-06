package ivan8or.fluidgui.parse.depend;

import java.util.Objects;

public class DependencyID {

    final private DependencyType dependencyType;
    final private String dependencyName;

    public DependencyID(DependencyType type, String name) {
        this.dependencyName = name;
        this.dependencyType = type;
    }

    public DependencyType getType() {
        return dependencyType;
    }

    public String getName() {
        return dependencyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyID that = (DependencyID) o;
        return dependencyType == that.dependencyType && Objects.equals(dependencyName, that.dependencyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencyType, dependencyName);
    }

    @Override
    public String toString() {
        return dependencyType.name() + "." + dependencyName;
    }

}
