package org.mqnaas.bundletree.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.bundletree.Activator;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.bundletree.exceptions.BundleNotFoundException;
import org.mqnaas.bundletree.utils.BundleClassPathUtils;
import org.mqnaas.bundletree.utils.BundleUtils;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * Bundle Guard maintains a set of registered {@link IClassListener}, each of them using a {@link IClassFilter}.
 * 
 * Any started {@link Bundle} that enters in the system would be analysed looking for {@link Class}es. IClassListener's would be notified for each
 * positive match with its IClassFilter.
 * 
 * Any stopped Bundle that leaves the system would be analysed looking for Classes previously notified to any IClassListener. For each of these cases,
 * its IClassListener would be notified, warning the left classes.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class BundleGuard implements IBundleGuard, BundleListener, ICapability {

	// method declaring this instance as MQNaaS core ICapability
	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	// map containing relation between classFiletrs and classListeners
	Map<IClassFilter, IClassListener>	classListenersMap	= new ConcurrentHashMap<IClassFilter, IClassListener>();

	public BundleGuard() {
		// register itself as BundleListener
		Activator.getContext().addBundleListener(this);
	}

	@Override
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener) {
		// add classFileter-classListener to the map
		classListenersMap.put(classFilter, classListener);
	}

	@Override
	public void unregisterClassListener(IClassListener classListener) {
		// remove classListener from the map
		for (Iterator<Map.Entry<IClassFilter, IClassListener>> it = classListenersMap.entrySet().iterator(); it.hasNext();) {
			if (it.next().getValue().equals(classListener)) {
				it.remove();
			}
		}
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		System.out.println("Received BundleEvent: " + event);
		// a new bundle got started
		if (event.getType() == BundleEvent.STARTED) {
			Bundle bundle = event.getBundle();

			try {
				// is this bundle dependent of core.api bundle
				if (BundleUtils.bundleDependsOnBundle(Activator.getContext(), bundle,
						BundleUtils.getBundleBySymbolicName(Activator.getContext(), "core.api"))) {
					// scan bundle and notify in case of classFilters matches
					scanBundleAndNotifyClassFound(bundle);
				}
			} catch (BundleNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// a previous bundle got stopped
		else if (event.getType() == BundleEvent.STOPPED) {
			// TODO would be classes available? would we store each notification? :$
			Bundle bundle = event.getBundle();

			try {
				// is this bundle dependent of core.api bundle
				if (BundleUtils.bundleDependsOnBundle(Activator.getContext(), bundle,
						BundleUtils.getBundleBySymbolicName(Activator.getContext(), "core.api"))) {
					// scan bundle and notify in case of classFilters matches
					scanBundleAndNotifyClassLeft(bundle);
				}
			} catch (BundleNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void scanBundleAndNotifyClassFound(Bundle bundle) {
		// iterate over bundle classes
		for (Class<?> bundleClass : BundleClassPathUtils.getBundleClasses(bundle)) {
			// iterate over map asking each classFilter
			for (IClassFilter classFilter : classListenersMap.keySet()) {
				if (classFilter.filter(bundleClass)) {
					// class found, notify classListener
					classListenersMap.get(classFilter).classEntered(bundleClass);
				}
			}
		}
	}

	private void scanBundleAndNotifyClassLeft(Bundle bundle) {
		// iterate over bundle classes
		for (Class<?> bundleClass : BundleClassPathUtils.getBundleClasses(bundle)) {
			// iterate over map asking each classFilter
			for (IClassFilter classFilter : classListenersMap.keySet()) {
				if (classFilter.filter(bundleClass)) {
					// class found, notify classListener
					classListenersMap.get(classFilter).classLeft(bundleClass);
				}
			}
		}
	}
}
