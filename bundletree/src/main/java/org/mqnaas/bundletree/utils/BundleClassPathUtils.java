package org.mqnaas.bundletree.utils;

/*
 * #%L
 * MQNaaS :: BundleTree
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.mqnaas.bundletree.tree.BundleTreeUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundle Classpath Utilities.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class BundleClassPathUtils {

	private static final Logger	log	= LoggerFactory.getLogger(BundleClassPathUtils.class);

	interface ClassVisitor {
		void visit(Class<?> resourceClass);
	}

	/**
	 * Calls {@link #scanBundle(Bundle, Class, String)} using "core.api" bundle as ancestorBundle.
	 */
	public static <T> Set<Class<? extends T>> scanBundle(Bundle bundle, Class<T> interfaceScannedFor) {
		return scanBundle(bundle, interfaceScannedFor, "core.api");
	}

	/**
	 * Scans given {@link Bundle} looking for interfaceScannedFor interface {@link Class} and retrieves each subclass or subinterface of it. Search is
	 * filtered scanning only Bundles dependent on ancestorBundle Bundle-SymbolicName.
	 * 
	 * @param bundle
	 *            Bundle to be used in the search
	 * @param interfaceScannedFor
	 *            Class to b used to look for subclasses and subinterfaces
	 * @param ancestorBundle
	 *            Bundle-SymbolicName of ancestor bundle
	 * @return {@link Set} of subclasses and subinterfaces of given Class found in given Bundle
	 */
	public static <T> Set<Class<? extends T>> scanBundle(Bundle bundle, Class<T> interfaceScannedFor, String ancestorBundle) {
		if (BundleTreeUtils.isBundleDependant(bundle, "core.api")) {
			log.debug("Scanning bundle " + bundle.getSymbolicName());

			ImplementationDetectingClassVisitor<T> visitor = new ImplementationDetectingClassVisitor<T>(interfaceScannedFor);

			for (Class<?> clazz : getBundleClasses(bundle)) {
				visitor.visit(clazz);
			}

			return visitor.getImplementations();
		}
		return Collections.emptySet();
	}

	private static class ImplementationDetectingClassVisitor<T> implements ClassVisitor {

		private Class<T>				implementedInterface;
		private Set<Class<? extends T>>	implementations;

		private ImplementationDetectingClassVisitor(Class<T> implementedInterface) {
			this.implementedInterface = implementedInterface;
			implementations = new HashSet<Class<? extends T>>();
		}

		@Override
		public void visit(Class<?> resourceClass) {

			// 1. Ignore interfaces and abstract classes
			if (resourceClass.isInterface() || Modifier.isAbstract(resourceClass.getModifiers()))
				return;

			// 2. Check if the ICapability interface is implemented
			boolean implementsCapability = ClassUtils.getAllInterfaces(resourceClass).contains(implementedInterface);

			if (implementsCapability) {
				// This cast is guaranteed to work since we assured that the class implements the ICapability interface
				@SuppressWarnings("unchecked")
				Class<? extends T> capabilityClass = (Class<? extends T>) resourceClass;

				implementations.add(capabilityClass);
			}
		}

		public Set<Class<? extends T>> getImplementations() {
			return implementations;
		}
	}

	/**
	 * Retrieves all the {@link Class}es found in given {@link Bundle}.
	 * 
	 * @param bundle
	 *            Bundle to be used in the search
	 * @return {@link Set} of classes present in given Bundle
	 */
	public static Set<Class<?>> getBundleClasses(Bundle bundle) {
		if (bundle.getState() != Bundle.ACTIVE) {
			log.warn("Can not get classes from bundle " + bundle.getSymbolicName() + ", it is not ACTIVE.");
			return Collections.emptySet();
		}

		log.debug("Getting classes for bundle " + bundle.getSymbolicName());

		Set<Class<?>> classes = new HashSet<Class<?>>();

		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		for (String resourceName : wiring.listResources("/", "*.class", BundleWiring.LISTRESOURCES_LOCAL | BundleWiring.LISTRESOURCES_RECURSE)) {
			try {
				// Load the classes and pass them to the visitor
				resourceName = resourceName.replaceAll(".class", "").replaceAll("/", ".");

				log.trace("\tLoading class " + resourceName);

				classes.add(bundle.loadClass(resourceName));
			} catch (IncompatibleClassChangeError e) {
				// FIXME Silently ignore this case for now...
				// thrown by Felix (analyse...)
			} catch (VerifyError e) {
				// FIXME Silently ignore this case for now...
				// thrown by Felix (analyse...)
			} catch (ClassNotFoundException e) {
				// FIXME Silently ignore this case for now...
			} catch (NoClassDefFoundError e) {
				// FIXME Silently ignore this case for now...
			}
		}

		return classes;
	}
}
