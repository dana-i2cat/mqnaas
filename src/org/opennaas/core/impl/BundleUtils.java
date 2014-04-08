package org.opennaas.core.impl;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

public abstract class BundleUtils {

	public interface ClassVisitor {
		void visit(Class<?> resourceClass);
	}

	public static <T> Set<Class<? extends T>> scanBundle(Bundle bundle, Class<T> interfaceScannedFor) {

		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		wiring.getRequiredWires(null);
		
		ImplementationDetectingClassVisitor<T> visitor = new ImplementationDetectingClassVisitor<T>(interfaceScannedFor);

		for (String resourceName : wiring.listResources("/", "*.class",
				BundleWiring.LISTRESOURCES_RECURSE)) {
			try {
				// Load the classes and pass them to the visitor
				resourceName = resourceName.replaceAll(".class", "").replaceAll("/", ".");

				visitor.visit(bundle.loadClass(resourceName));
			} catch (ClassNotFoundException e) {
				// Silently ignore this case for now...
			} catch (NoClassDefFoundError e) {
				// Silently ignore this case for now...
			}
		}
		
		return visitor.getImplementations();
	}
	
	
	private static class ImplementationDetectingClassVisitor<T> implements ClassVisitor {
		
		private Class<T> implementedInterface;
		
		private Set<Class<? extends T>> implementations;

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
				// This cast is guaranteed to work since we assured that the
				// class implements the ICapability interface
				@SuppressWarnings("unchecked")
				Class<? extends T> capabilityClass = (Class<? extends T>) resourceClass;
				
				implementations.add(capabilityClass);
			}
		}
		
		public Set<Class<? extends T>> getImplementations() {
			return implementations;
		}
	}
	

}
