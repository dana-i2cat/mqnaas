package org.mqnaas.core.api.slicing;

import org.mqnaas.core.api.ICapability;

/**
 * This capability allows to manage the additional attributes of the slice unit.
 * 
 * It is thought to be bound to units.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IUnitAdministration extends ICapability {

	/**
	 * Initializes the {@link Range} for the given {@link Unit}, e.g. the width of the available slicing elements along that axis.
	 */
	void setRange(Range range);

	/**
	 * Returns the {@link Range} defined for the given slice {@link Unit}. If the give <code>unit</code> is not defined in this slice,
	 * <code>null</code> is returned.
	 */
	Range getRange();

}
