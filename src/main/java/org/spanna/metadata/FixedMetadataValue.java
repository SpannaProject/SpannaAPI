package org.spanna.metadata;

import org.spanna.component.Component;

import java.util.concurrent.Callable;

/**
 * A FixedMetadataValue is a special case metadata item that contains the same
 * value forever after initialization. Invalidating a FixedMetadataValue has
 * no effect.
 * <p>
 * This class extends LazyMetadataValue for historical reasons, even though it
 * overrides all the implementation methods. it is possible that in the future
 * that the inheritance hierarchy may change.
 */
public class FixedMetadataValue extends LazyMetadataValue {

    /**
     * Store the internal value that is represented by this fixed value.
     */
    private final Object internalValue;

    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningComponent the {@link Component} that created this metadata value
     * @param value the value assigned to this metadata value
     */
    public FixedMetadataValue(Component owningComponent, final Object value) {
        super(owningComponent);
        this.internalValue = value;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public Object value() {
        return internalValue;
    }
}
