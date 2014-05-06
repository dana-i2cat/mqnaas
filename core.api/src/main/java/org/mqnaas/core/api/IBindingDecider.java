package org.mqnaas.core.api;

/**
 * <p>
 * <code>IBindingDecider</code> provides the logic to decide when {@link ICapability}'s should be bound to {@link IResource}'s.
 * </p>
 */
public interface IBindingDecider extends ICapability {

	/**
	 * <p>
	 * Defines whether the services present in the given {@link ICapability} class should be bound to the given {@link IResource}. This is the service
	 * defining the automatic binding process of the platform.
	 * </p>
	 * 
	 * @param resource
	 *            The resource for which the binding is checked
	 * @param capabilityClass
	 *            The class containing an implementation of one or more {@link ICapability}s.
	 * @return whether the {@link IResource} and the {@link Class} should be bound
	 */
	boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass);

}
