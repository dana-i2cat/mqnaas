package org.mqnaas.examples.helloworld;

import org.mqnaas.core.api.ICapability;

/**
 * Hello World {@link ICapability}
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface IHelloWorldCapability extends ICapability {

	/**
	 * Service performing a personal greeting.
	 * 
	 * @param name
	 *            Name of the user to be greeted.
	 * @return Greeting message.
	 */
	public String hello(String name);

}
