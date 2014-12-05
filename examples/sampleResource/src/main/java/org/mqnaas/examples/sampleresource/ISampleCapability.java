package org.mqnaas.examples.sampleresource;

import org.mqnaas.core.api.ICapability;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface ISampleCapability extends ICapability {

	public void increment();

	public void setCounter(int counterValue);

	public int getCounter();

}
