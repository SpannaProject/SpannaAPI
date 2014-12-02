package org.spanna.block;

import org.spanna.inventory.InventoryHolder;

/**
 * Indicates a block type that has inventory.
 *
 * @deprecated in favour of {@link InventoryHolder}
 */
@Deprecated
public interface ContainerBlock extends InventoryHolder {}
