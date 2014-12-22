package org.mqnaas.core.api;

/**
 * A simple store for String values which can be bound to any {@link IResource} if necessary.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IAttributeStore extends ICapability {

	String getAttribute(String name);

	void setAttribute(String name, String value);

}
