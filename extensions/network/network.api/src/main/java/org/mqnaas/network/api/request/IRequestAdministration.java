package org.mqnaas.network.api.request;

import org.mqnaas.core.api.ICapability;

/**
 * Capability to edit attributes of network requests.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IRequestAdministration extends ICapability {

	// TODO Exchange this Period for java.time.Period once we use Java 8
	void setPeriod(Period period);

	Period getPeriod();

}
