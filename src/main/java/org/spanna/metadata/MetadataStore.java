package org.spanna.metadata;

import org.spanna.component.Component;

import java.util.List;

public interface MetadataStore<T> {
    /**
     * Adds a metadata value to an object.
     *
     * @param subject The object receiving the metadata.
     * @param metadataKey A unique key to identify this metadata.
     * @param newMetadataValue The metadata value to apply.
     * @throws IllegalArgumentException If value is null, or the owning component
     *     is null
     */
    public void setMetadata(T subject, String metadataKey, MetadataValue newMetadataValue);

    /**
     * Returns all metadata values attached to an object. If multiple component
     * have attached metadata, each will value will be included.
     *
     * @param subject the object being interrogated.
     * @param metadataKey the unique metadata key being sought.
     * @return A list of values, one for each component that has set the
     *     requested value.
     */
    public List<MetadataValue> getMetadata(T subject, String metadataKey);

    /**
     * Tests to see if a metadata attribute has been set on an object.
     *
     * @param subject the object upon which the has-metadata test is
     *     performed.
     * @param metadataKey the unique metadata key being queried.
     * @return the existence of the metadataKey within subject.
     */
    public boolean hasMetadata(T subject, String metadataKey);

    /**
     * Removes a metadata item owned by a component from a subject.
     *
     * @param subject the object to remove the metadata from.
     * @param metadataKey the unique metadata key identifying the metadata to
     *     remove.
     * @param owningComponent the component attempting to remove a metadata item.
     * @throws IllegalArgumentException If component is null
     */
    public void removeMetadata(T subject, String metadataKey, Component owningComponent);

    /**
     * Invalidates all metadata in the metadata store that originates from the
     * given component. Doing this will force each invalidated metadata item to
     * be recalculated the next time it is accessed.
     *
     * @param owningComponent the component requesting the invalidation.
     * @throws IllegalArgumentException If component is null
     */
    public void invalidateAll(Component owningComponent);
}
