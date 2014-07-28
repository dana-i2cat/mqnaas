package org.mqnaas.clientprovider.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.ICoreModelCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProviderFactory<CP> {

	private static final Logger		log	= LoggerFactory.getLogger(AbstractProviderFactory.class);

	@DependingOn
	protected ICoreModelCapability	coreModelCapability;

	@DependingOn
	protected IBundleGuard			bundleGuard;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	protected Map<Class<CP>, CP>	internalClientProviders;

	protected static Set<Type>		VALID_CLIENT_PROVIDERS;

	// internal {@link IClassListener} instance
	protected InternalClassListener	internalClassListener;

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

	protected static boolean doTypeArgumentsMatch(Set<Type> validTypes, Class<?> clazz1, Class<?> class2, int numArgs) {

		for (int i = 0; i < numArgs; i++) {
			if (!getTypeArgument(validTypes, i, clazz1).equals(getTypeArgument(validTypes, i, class2))) {
				return false;
			}
		}

		return true;
	}

	private static Type getTypeArgument(Set<Type> validTypes, int index, Class<?> clientProviderClass) {

		// Look for the specific generic interfaces...
		for (Type type : clientProviderClass.getGenericInterfaces()) {

			ParameterizedType parameterizedType = (ParameterizedType) type;

			if (validTypes.contains(parameterizedType.getRawType())) {
				return parameterizedType.getActualTypeArguments()[index];
			}
		}

		return null;
	}

}
