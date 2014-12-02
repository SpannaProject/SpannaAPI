package org.spanna.component;

/**
 * Represents a base {@link Component}
 * <p>
 * Extend this class if your component is not a {@link
 * org.spanna.component.java.JavaComponent}
 */
public abstract class ComponentBase implements Component {
    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Component)) {
            return false;
        }
        return getName().equals(((Component) obj).getName());
    }

    public final String getName() {
        return getDescription().getName();
    }
}
