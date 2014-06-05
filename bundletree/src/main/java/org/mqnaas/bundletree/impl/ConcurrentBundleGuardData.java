package org.mqnaas.bundletree.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.bundletree.utils.BundleClassPathUtils;
import org.osgi.framework.Bundle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * {@link BundleGuard} data containing all the necessary structures to offer its services.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ConcurrentBundleGuardData {

	private Object						lock							= new Object();

	// map containing relation between classFiletrs and registered classListeners
	Map<IClassFilter, IClassListener>	classListenersMap				= new HashMap<IClassFilter, IClassListener>();

	// multimap containing classes notified by classAdded callback method for each IClassListener
	Multimap<IClassListener, Class<?>>	classListenerNotifiedClassesMap	= HashMultimap.<IClassListener, Class<?>> create();

	// multimap containing classes for each Bundle
	Multimap<Bundle, Class<?>>			bundleClassesMap				= HashMultimap.<Bundle, Class<?>> create();

	/**
	 * Performs a set of actions to be done when a {@link Bundle} enters the system: <br/>
	 * 
	 * <ol>
	 * <li>Extract the set of {@link Class}'es of the given Bundle and stores it in bundleClassesMap.</li>
	 * <li>Notify any previously added {@link IClassListener} with positive Class matches based on his {@link IClassFilter}.</li>
	 * <li>For each IClassListener, store notified Class'es in classListenerNotifiedClassesMap.</li>
	 * </ol>
	 * 
	 * @param bundle
	 *            added Bundle, to be analysed
	 */
	public void bundleIn(Bundle bundle) {
		System.out.println("Bundle in: " + bundle);
		synchronized (lock) {
			// extract classes from Bundle and store them in bundleClassesMap
			bundleClassesMap.putAll(bundle, BundleClassPathUtils.getBundleClasses(bundle));

			// notify previously added classListeners
			for (IClassFilter classFilter : classListenersMap.keySet()) {
				Collection<Class<?>> classesToNotify = getFilteredClasses(classFilter, bundleClassesMap.get(bundle));
				IClassListener classListener = classListenersMap.get(classFilter);
				classListenerNotifiedClassesMap.putAll(classListener, classesToNotify);
				notifyClassListener(classListener, classesToNotify, ClassListenerNotification.ENTERED);
			}
		}
	}

	/*
	 * Filters a Collection of Class'es fulfilling an IClassFilter
	 */
	private Collection<Class<?>> getFilteredClasses(IClassFilter classFilter, Collection<Class<?>> classes) {
		Collection<Class<?>> filteredClasses = new HashSet<Class<?>>();

		for (Class<?> clazz : classes) {
			if (classFilter.filter(clazz)) {
				filteredClasses.add(clazz);
			}
		}
		return filteredClasses;
	}

	private enum ClassListenerNotification {
		ENTERED, LEFT
	}

	/*
	 * Notifies (entered or left) an IClassListener with given Collection of Class'es.
	 */
	private void notifyClassListener(IClassListener classListener, Collection<Class<?>> classes, ClassListenerNotification notification) {
		for (Class<?> clazz : classes) {
			switch (notification) {
				case ENTERED:
					classListener.classEntered(clazz);
					break;
				case LEFT:
					classListener.classLeft(clazz);
					break;
			}
		}
	}

	/**
	 * Performs a set of actions to be done when a {@link Bundle} leaves the system: <br/>
	 * <ol>
	 * <li>Notify classes left to any previously notified {@link IClassListener} with classes in classListenerNotifiedClassesMap.</li>
	 * <li>Remove notified classes from classListenerNotifiedClassesMap for each IClassListener.</li>
	 * <li>Remove Bundle entry from bundleClassesMap.</li>
	 * </ol>
	 * 
	 * @param bundle
	 *            left Bundle
	 */
	public void bundleOut(Bundle bundle) {
		System.out.println("Bundle out: " + bundle);
		synchronized (lock) {
			// iterate over previously notified IClassListener present in classListenerNotifiedClassesMap
			for (IClassListener classListener : classListenerNotifiedClassesMap.keySet()) {
				// filter classes
				Collection<Class<?>> notifiedClasses = new HashSet<Class<?>>(classListenerNotifiedClassesMap.get(classListener));
				Collection<Class<?>> bundleClasses = bundleClassesMap.get(bundle);
				notifiedClasses.retainAll(bundleClasses);

				// notify left classes
				notifyClassListener(classListener, notifiedClasses, ClassListenerNotification.LEFT);

				// remove notified classes from classListenerNotifiedClassesMap for this classListener
				for (Class<?> clazz : notifiedClasses) {
					classListenerNotifiedClassesMap.remove(classListener, clazz);
				}
			}

			// remove bundle from bundleClassesMap
			bundleClassesMap.removeAll(bundle);
		}
	}

	/**
	 * Registers an {@link IClassListener} with an {@link IClassFilter}
	 * 
	 * @param classListener
	 *            IClassListener to be registered
	 * @param classFilter
	 *            IClassFilter to be associated with classListener
	 */
	public void registerClassListener(IClassListener classListener, IClassFilter classFilter) {
		synchronized (lock) {
			// add classListener with his classFilter to classListenersMap
			classListenersMap.put(classFilter, classListener);

			// notify any previous bundle with new classListener if necessary
			for (Bundle bundle : bundleClassesMap.keySet()) {
				Collection<Class<?>> classesToNotify = getFilteredClasses(classFilter, bundleClassesMap.get(bundle));
				classListenerNotifiedClassesMap.putAll(classListener, classesToNotify);
				notifyClassListener(classListener, classesToNotify, ClassListenerNotification.ENTERED);
			}
		}
	}

	/**
	 * Unregisters an {@link IClassListener} and removes all the notified classes associated with it from classListenerNotifiedClassesMap
	 * 
	 * @param classListener
	 */
	public void unregisterClassListener(IClassListener classListener) {
		synchronized (lock) {
			for (Iterator<Map.Entry<IClassFilter, IClassListener>> it = classListenersMap.entrySet().iterator(); it.hasNext();) {
				IClassListener currentClassListener = it.next().getValue();
				if (currentClassListener.equals(classListener)) {
					// remove entry in classListenersMap
					it.remove();
					// remove classListener from classListenerNotifiedClassesMap
					classListenerNotifiedClassesMap.removeAll(currentClassListener);
				}
			}
		}
	}

}
