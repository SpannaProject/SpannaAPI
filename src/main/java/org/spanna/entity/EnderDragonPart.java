package org.spanna.entity;

/**
 * Represents an ender dragon part
 */
public interface EnderDragonPart extends ComplexEntityPart, Damageable {
    public EnderDragon getParent();
}
