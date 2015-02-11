package org.mqnaas.core.api;

/**
 * A simple store for String values which can be bound to any {@link IResource} if necessary.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IAttributeStore extends ICapability {

	static final String	RESOURCE_EXTERNAL_ID	= "resource.external.id";

	String getAttribute(String name);

	void setAttribute(String name, String value);

}
