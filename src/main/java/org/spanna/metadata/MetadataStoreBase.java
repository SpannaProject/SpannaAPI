package org.spanna.metadata;

import org.apache.commons.lang.Validate;
import org.spanna.component.Component;

import java.util.*;

public abstract class MetadataStoreBase<T> {
    private Map<String, Map<Component, MetadataValue>> metadataMap = new HashMap<String, Map<Component, MetadataValue>>();

    /**
     * Adds a metadata value to an object. Each metadata value is owned by a
     * specific {@link Component}. If a component has already added a metadata value
     * to an object, that value will be replaced with the value of {@code
     * newMetadataValue}. Multiple components can set independent values for the
     * same {@code metadataKey} without conflict.
     * <p>
     * Implementation note: I considered using a {@link
     * java.util.concurrent.locks.ReadWriteLock} for controlling access to
     * {@code metadataMap}, but decided that the added overhead wasn't worth
     * the finer grained access control.
     * <p>
     * Spanna is almost entirely single threaded so locking overhead shouldn't
     * pose a problem.
     *
     * @param subject The object receiving the metadata.
     * @param metadataKey A unique key to identify this metadata.
     * @param newMetadataValue The metadata value to apply.
     * @see MetadataStore#setMetadata(Object, String, MetadataValue)
     * @throws IllegalArgumentException If value is null, or the owning component
     *     is null
     */
    public synchronized void setMetadata(T subject, String metadataKey, MetadataValue newMetadataValue) {
        Validate.notNull(newMetadataValue, "[ERROR] Value cannot be null");
        Component owningComponent = newMetadataValue.getOwningComponent();
        Validate.notNull(owningComponent, "[ERROR] Component cannot be null");
        String key = disambiguate(subject, metadataKey);
        Map<Component, MetadataValue> entry = metadataMap.get(key);
        if (entry == null) {
            entry = new WeakHashMap<Component, MetadataValue>(1);
            metadataMap.put(key, entry);
        }
        entry.put(owningComponent, newMetadataValue);
    }

    /**
     * Returns all metadata values attached to an object. If multiple
     * have attached metadata, each will value will be included.
     *
     * @param subject the object being interrogated.
     * @param metadataKey the unique metadata key being sought.
     * @return A list of values, one for each plugin that has set the
     *     requested value.
     * @see MetadataStore#getMetadata(Object, String)
     */
    public synchronized List<MetadataValue> getMetadata(T subject, String metadataKey) {
        String key = disambiguate(subject, metadataKey);
        if (metadataMap.containsKey(key)) {
            Collection<MetadataValue> values = metadataMap.get(key).values();
            return Collections.unmodifiableList(new ArrayList<MetadataValue>(values));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Tests to see if a metadata attribute has been set on an object.
     *
     * @param subject the object upon which the has-metadata test is
     *     performed.
     * @param metadataKey the unique metadata key being queried.
     * @return the existence of the metadataKey within subject.
     */
    public synchronized boolean hasMetadata(T subject, String metadataKey) {
        String key = disambiguate(subject, metadataKey);
        return metadataMap.containsKey(key);
    }

    /**
     * Removes a metadata item owned by a component from a subject.
     *
     * @param subject the object to remove the metadata from.
     * @param metadataKey the unique metadata key identifying the metadata to
     *     remove.
     * @param owningComponent the component attempting to remove a metadata item.
     * @see MetadataStore#removeMetadata(Object, String,
     *     org.spanna.component.Component)
     * @throws IllegalArgumentException If component is null
     */
    public synchronized void removeMetadata(T subject, String metadataKey, Component owningComponent) {
        Validate.notNull(owningComponent, "[ERROR] Component cannot be null");
        String key = disambiguate(subject, metadataKey);
        Map<Component, MetadataValue> entry = metadataMap.get(key);
        if (entry == null) {
            return;
        }

        entry.remove(owningComponent);
        if (entry.isEmpty()) {
            metadataMap.remove(key);
        }
    }

    /**
     * Invalidates all metadata in the metadata store that originates from the
     * given component. Doing this will force each invalidated metadata item to
     * be recalculated the next time it is accessed.
     *
     * @param owningComponent the component requesting the invalidation.
     * @see MetadataStore#invalidateAll(org.spanna.component.Component)
     * @throws IllegalArgumentException If component is null
     */
    public synchronized void invalidateAll(Component owningComponent) {
        Validate.notNull(owningComponent, "[ERROR] Component cannot be null");
        for (Map<Component, MetadataValue> values : metadataMap.values()) {
            if (values.containsKey(owningComponent)) {
                values.get(owningComponent).invalidate();
            }
        }
    }

    /**
     * Creates a unique name for the object receiving metadata by combining
     * unique data from the subject with a metadataKey.
     * <p>
     * The name created must be globally unique for the given object and any
     * two equivalent objects must generate the same unique name. For example,
     * two Player objects must generate the same string if they represent the
     * same player, even if the objects would fail a reference equality test.
     *
     * @param subject The object for which this key is being generated.
     * @param metadataKey The name identifying the metadata value.
     * @return a unique metadata key for the given subject.
     */
    protected abstract String disambiguate(T subject, String metadataKey);
}
