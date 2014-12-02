package org.spanna.event.inventory;

import org.spanna.event.inventory.InventoryType.SlotType;
import org.spanna.inventory.InventoryView;
import org.spanna.inventory.ItemStack;

/**
 * This event is called when a player in creative mode puts down or picks up
 * an item in their inventory / hotbar and when they drop items from their
 * Inventory while in creative mode.
 */
public class InventoryCreativeEvent extends InventoryClickEvent {
    private ItemStack item;

    public InventoryCreativeEvent(InventoryView what, SlotType type, int slot, ItemStack newItem) {
        super(what, type, slot, ClickType.CREATIVE, InventoryAction.PLACE_ALL);
        this.item = newItem;
    }

    public ItemStack getCursor() {
        return item;
    }

    public void setCursor(ItemStack item) {
        this.item = item;
    }
}
