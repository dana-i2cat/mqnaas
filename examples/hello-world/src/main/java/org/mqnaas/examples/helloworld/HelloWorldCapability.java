package org.mqnaas.examples.helloworld;

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;

/**
 * Implementation of the {@link IHelloWorldCapability}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class HelloWorldCapability implements IHelloWorldCapability {

	private String			greetingMessageStart;
	private String			greetingMessageEnd;

	@Resource
	private IRootResource	resource;

	public static boolean isSupporting(IRootResource resource) {
		// This capability implementation will bind with a resource of Type OTHER and model "hello-world".
		// This is the way to associate Capability implementation and resources. Any logic may be used.
		return resource.getDescriptor().getSpecification().getType().equals(Type.OTHER)
				&& resource.getDescriptor().getSpecification().getModel().equals("hello-world");
	}

	// Mandatory initialisation method of Capability implementation
	@Override
	public void activate() throws ApplicationActivationException {
		// Initialise greeting message
		this.greetingMessageStart = "Hello, ";
		this.greetingMessageEnd = " from " + this.resource.getId() + ".";
	}

	// Mandatory destroy method of Capability implementation
	@Override
	public void deactivate() {
		this.greetingMessageStart = null;
		this.greetingMessageEnd = null;
	}

	// Implementation of the hello service
	@Override
	public String hello(String name) {
		return greetingMessageStart + name + greetingMessageEnd;
	}

}
