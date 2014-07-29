package org.mqnaas.clientprovider.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.bundletree.utils.ClassFilterFactory;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.ICoreModelCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProviderFactory<CP> implements ICapability {

	private final Logger			log	= LoggerFactory.getLogger(getClass());

	@DependingOn
	protected ICoreModelCapability	coreModelCapability;

	@DependingOn
	protected IBundleGuard			bundleGuard;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	protected Map<Class<CP>, CP>	internalClientProviders;

	// internal {@link IClassListener} instance
	protected InternalClassListener	internalClassListener;

	protected abstract Class<?> getInternalProviderClass();

	@Override
	public void activate() {
		// register class listener
		log.info("Registering as ClassListener.");
		internalClassListener = new InternalClassListener(getInternalProviderClass());
		bundleGuard.registerClassListener(ClassFilterFactory.getBasicClassFilter(getInternalProviderClass()), internalClassListener);
	}

	@Override
	public void deactivate() {
		// unregister class listeners
		log.info("Unregistering as ClassListener.");
		bundleGuard.unregisterClassListener(internalClassListener);
	}

	private void internalClientProviderAdded(Class<CP> clazz) {
		try {
			internalClientProviders.put(clazz, clazz.newInstance());
		} catch (InstantiationException e) {
			// this are guaranteed to be an instantiable class
			log.error("Error instantiating IClientProvider of class: " + clazz, e);
		} catch (IllegalAccessException e) {
			// at this moment, access to class should be granted
			log.error("Error instantiating IClientProvider of class: " + clazz, e);
		}
	}

	private void internalClientProviderRemoved(Class<? extends CP> clazz) {
		// remove them
		internalClientProviders.remove(clazz);
	}

	protected class InternalClassListener implements IClassListener {

		private Class<?>	internalProviderClass;

		public InternalClassListener(Class<?> internalProviderClass) {
			this.internalProviderClass = internalProviderClass;
		}

		@Override
		// safe checking castings
		@SuppressWarnings("unchecked")
		public void classEntered(Class<?> clazz) {
			log.debug("Received classEntered event for class: " + clazz.getCanonicalName());
			if (internalProviderClass.isAssignableFrom(clazz)) {
				internalClientProviderAdded((Class<CP>) clazz);
			} else {
				log.error("Unknown ClassListener classEntered event received from class " + clazz.getCanonicalName());
			}
		}

		@Override
		// safe checking castings
		@SuppressWarnings("unchecked")
		public void classLeft(Class<?> clazz) {
			log.debug("Received classLeft event for class: " + clazz.getCanonicalName());
			if (internalProviderClass.isAssignableFrom(clazz)) {
				internalClientProviderRemoved((Class<? extends CP>) clazz);
			} else {
				log.error("Unknown ClassListener classLeft event received from class " + clazz.getCanonicalName());
			}

		}

	}

	/**
	 * Returns true if given two classes have common types on first generic interface present in given valid types.
	 * 
	 * @param validTypes
	 *            valid types to be checked
	 * @param class1
	 *            first target {@link Class}
	 * @param class2
	 *            second target {@link Class}
	 * @return true if given two classes have common types on first generic interface present in given valid types; false otherwise
	 */
	protected static boolean doTypeArgumentsMatch(Set<Type> validTypes, Class<?> class1, Class<?> class2) {
		int numTypes;
		if ((numTypes = getTypeNumber(class1)) != getTypeNumber(class2)) {
			return false;
		}

		for (int i = 0; i < numTypes; i++) {
			if (!getTypeArgument(validTypes, i, class1).equals(getTypeArgument(validTypes, i, class2))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retrieves index {@link Type} of the first generic interface implemented in given {@link Class} if it is cpresent in given valid types.
	 * 
	 * @param validTypes
	 *            valid Types to be checked
	 * @param index
	 *            index of Types to be checked
	 * @param clazz
	 *            target Class
	 * @return Type found or null if not found
	 */
	private static Type getTypeArgument(Set<Type> validTypes, int index, Class<?> clazz) {

		// Look for the specific generic interfaces...
		for (Type type : clazz.getGenericInterfaces()) {

			ParameterizedType parameterizedType = (ParameterizedType) type;

			if (validTypes.contains(parameterizedType.getRawType())) {
				return parameterizedType.getActualTypeArguments()[index];
			}
		}

		return null;
	}

	/**
	 * Retrieves the number of generic types of first interface of given {@link Class}.
	 * 
	 * @param clazz
	 *            target Class
	 * @return number of types
	 */
	private static int getTypeNumber(Class<?> clazz) {
		return ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments().length;
	}

}
