package org.mqnaas.api.writers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IIdentifiable;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;

/**
 * Holds meta information about a {@link ICapability} used during the REST API mapping process
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
class CapabilityMetaDataContainer {

	// If the capability, for which this container hold meta information is a management capability:
	// (1) entityClass represents the Class of the managed objects
	private Class<?>		entityClass;

	// (2) listService is the method listing all managed resources without parameters.
	private Method			listService;

	private List<Method>	services;

	/**
	 * Checks the usability of the given {@link ICapability} class
	 * 
	 * @param capabilityClass
	 * @throws InvalidCapabilityDefinionException
	 *             An invalid capability exception is thrown when the capability defines a service which does not comply with the basic capability
	 *             contract
	 */
	public CapabilityMetaDataContainer(Class<? extends ICapability> capabilityClass) throws InvalidCapabilityDefinionException {

		// All methods defined in the capability are services
		services = new ArrayList<Method>(Arrays.asList(capabilityClass.getDeclaredMethods()));

		// All resource entities used in the services are collected to be able to check them
		Set<Class<?>> entityClasses = new HashSet<Class<?>>();

		// CHECK THE BASIC CONTRACT
		for (Method m : getServiceMethods(AddsResource.class)) {
			// (1) Check validity
			// Either has to returns IIdentifiable or to define a single IIdentifiable parameters
			boolean returnsIdentifiable = IIdentifiable.class.isAssignableFrom(m.getReturnType());
			boolean hasIdentifiableParameter = m.getParameterTypes().length == 1 && IIdentifiable.class.isAssignableFrom(m.getParameterTypes()[0]);

			if (!returnsIdentifiable && !hasIdentifiableParameter) {
				throw new InvalidCapabilityDefinionException(
						"Creation service " + m.getName() + " has to either: (a) return a subclass of " + IIdentifiable.class + " or (b) has a single parameter subclassing " + IIdentifiable.class);
			}

			// (2) Collect used entities
			if (IIdentifiable.class.isAssignableFrom(m.getReturnType())) {
				entityClasses.add(m.getReturnType());
			} else {
				entityClasses.add(m.getParameterTypes()[0]);
			}
		}

		for (Method m : getServiceMethods(RemovesResource.class)) {
			// (1) Check validity
			// Have to have one parameter subclassing IIdentifiable
			if (m.getParameterTypes().length != 1) {
				throw new InvalidCapabilityDefinionException(
						"Deletion service " + m.getName() + " has to have exactly one parameter extending " + IIdentifiable.class);
			} else if (!IIdentifiable.class.isAssignableFrom(m.getParameterTypes()[0])) {
				throw new InvalidCapabilityDefinionException(
						"Deletion service " + m.getName() + ": Parameter must be a subclass of " + IIdentifiable.class);
			}

			// (2) Collect used entities
			entityClasses.add(m.getParameterTypes()[0]);
		}

		for (Method m : getServiceMethods(ListsResources.class)) {
			// (1) Check validity
			// Has to return a collection of IIdentifiables
			if (!Collection.class.isAssignableFrom(m.getReturnType())) {
				throw new InvalidCapabilityDefinionException("List service " + m.getName() + " has to return a collection of " + IIdentifiable.class);
			}

			Class<?> returnParameterType = getReturnTypeParameter(m);

			if (!IIdentifiable.class.isAssignableFrom(returnParameterType)) {
				throw new InvalidCapabilityDefinionException(
						"List service " + m.getName() + " has to return collections of " + IIdentifiable.class);
			}

			// (2) Collect used entities
			entityClasses.add(getReturnTypeParameter(m));

			// (3) Search for a list service without parameters
			if (m.getParameterTypes().length == 0 && listService == null) {
				listService = m;
			}

		}

		// Check the collected entity classed
		switch (entityClasses.size()) {
			case 0: // There are no entity classes, e.g. there are no services returning or receiving entities. Nothing to be done.
				break;
			case 1: // There is just one entity class. This is the correct case. All services in the capability are working on the same entity bean.
					// Initialize the bean for later use.
				entityClass = entityClasses.iterator().next();
				break;
			default: // There is more than one entity class. The user has to separate the management capabilities.
				throw new InvalidCapabilityDefinionException("More than one entity class found: " + entityClasses);
		}

		if (listService == null && !getServiceMethods(ListsResources.class).isEmpty()) {
			throw new InvalidCapabilityDefinionException("No list service without parameters found.");
		}

	}

	private Class<?> getReturnTypeParameter(Method m) {

		Class<?> clazz = null;

		Type returnType = m.getGenericReturnType();
		if (returnType instanceof ParameterizedType) {
			ParameterizedType collectionType = (ParameterizedType) returnType;

			Type type = collectionType.getActualTypeArguments()[0];
			if (type instanceof Class<?>) {
				clazz = (Class<?>) type;
			}
		}

		return clazz;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public List<Method> getServiceMethods(Class<? extends Annotation> annotationClass) {
		List<Method> filtered = new ArrayList<Method>();

		for (Method service : services) {
			if (service.getAnnotation(annotationClass) != null) {
				filtered.add(service);
			}
		}

		return filtered;
	}

	public List<Method> getServiceMethods() {
		return services;
	}

	public Method getListService() {
		return listService;
	}

}
