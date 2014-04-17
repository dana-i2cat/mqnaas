package org.opennaas.core.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

public abstract class BundleUtils {

	public interface ClassVisitor {
		void visit(Class<?> resourceClass);
	}

	public static <T> Set<Class<? extends T>> scanBundle(Bundle bundle, Class<T> interfaceScannedFor) {

		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		wiring.getRequiredWires(null);

		ImplementationDetectingClassVisitor<T> visitor = new ImplementationDetectingClassVisitor<T>(interfaceScannedFor);

		for (String resourceName : wiring.listResources("/", "*.class", BundleWiring.LISTRESOURCES_RECURSE)) {
			System.out.println("Resource to be scanned: " + resourceName);
			try {
				// Load the classes and pass them to the visitor
				resourceName = resourceName.replaceAll(".class", "").replaceAll("/", ".");

				// FIXME use low trace level log or remove these lines
				// printMemoryUsage();
				// System.out.println("Loading class " + resourceName);

				visitor.visit(bundle.loadClass(resourceName));
			} catch (IncompatibleClassChangeError e) {
				// FIXME Silently ignore this case for now...
				// thrown by Felix (analize...)
			} catch (VerifyError e) {
				// FIXME Silently ignore this case for now...
				// thrown by Felix (analize...)
			} catch (ClassNotFoundException e) {
				// FIXME Silently ignore this case for now...
			} catch (NoClassDefFoundError e) {
				// FIXME Silently ignore this case for now...
			}
		}

		return visitor.getImplementations();
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

	public static boolean isPackageDependant(Bundle bundle, String packageName) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		if (wiring != null && wiring.isInUse()) {
			for (BundleWire wire : wiring.getRequiredWires(null)) {
				if (wire.getCapability().getAttributes().containsKey(BundleRevision.PACKAGE_NAMESPACE) && wire.getCapability().getAttributes()
						.get(BundleRevision.PACKAGE_NAMESPACE).equals(packageName)) {
					// FIXME use low trace level log or remove this line
					// System.out.println("MATCH! " + bundle.getSymbolicName() + " === Bundle importing package: " + packageName);
					return true;
				}
			}
		}

		return false;
	}

	private static void printMemoryUsage() {
		Iterator<MemoryPoolMXBean> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
		while (iter.hasNext()) {
			MemoryPoolMXBean item = iter.next();
			String name = item.getName();

			if (name.equals("PS Perm Gen")) {
				MemoryType type = item.getType();
				MemoryUsage usage = item.getUsage();
				MemoryUsage peak = item.getPeakUsage();
				MemoryUsage collections = item.getCollectionUsage();
				System.out
						.println("Memory usage: Name: " + name + ", type: " + type + ", usage = " + longBytesToMegs(usage.getUsed()) + ", peak = " + longBytesToMegs(peak
								.getUsed()) + ", collections = " + longBytesToMegs(collections.getUsed()));
			}
		}
	}

	private static String longBytesToMegs(long bytes) {
		return bytes / (1000 * 1000) + "MB";
	}

}
