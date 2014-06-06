package org.mqnaas.bundletree.utils;

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
