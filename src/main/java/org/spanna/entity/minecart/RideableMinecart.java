package org.spanna.entity.minecart;

import org.spanna.entity.Minecart;

/**
 * Represents a minecart that can have certain {@link
 * org.spanna.entity.Entity entities} as passengers. Normal passengers
 * include all {@link org.spanna.entity.LivingEntity living entities} with
 * the exception of {@link org.spanna.entity.IronGolem iron golems}.
 * Non-player entities that meet normal passenger criteria automatically
 * mount these minecarts when close enough.
 */
public interface RideableMinecart extends Minecart {
}
