package org.mqnaas.bundletree.impl;

import java.util.HashSet;
import java.util.Set;

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.bundletree.exceptions.BundleNotFoundException;
import org.mqnaas.bundletree.utils.BundleUtils;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class BundleGuard implements IBundleGuard {

	private static final Logger	log	= LoggerFactory.getLogger(BundleGuard.class);

	// method declaring this instance as MQNaaS core ICapability
	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	ConcurrentBundleGuardData	data			= new ConcurrentBundleGuardData();
	InternalBundleListener		bundleListener	= new InternalBundleListener();

	@Override
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener) {
		log.info("Registering Class Listener: " + classListener);
		data.registerClassListener(classListener, classFilter);
	}

	@Override
	public void unregisterClassListener(IClassListener classListener) {
		log.info("Unregistering Class Listener: " + classListener);
		data.unregisterClassListener(classListener);
	}

	/*
	 * Internal BundleListener class
	 */
	private class InternalBundleListener implements BundleListener {

		private Set<Bundle>	coreAPIDependentBundles	= new HashSet<Bundle>();

		public InternalBundleListener() {
			// register itself as BundleListener
			log.info("Registering BundleGuard as BundleListener");
			FrameworkUtil.getBundle(BundleGuard.class).getBundleContext().addBundleListener(this);

			// check current platform active bundles
			log.info("Checking current platform bundles...");
			Bundle[] bundles = FrameworkUtil.getBundle(getClass()).getBundleContext().getBundles();
			for (Bundle bundle : bundles) {
				if (bundle.getState() == Bundle.ACTIVE) {
					// process each bundle as a recently started bundle
					log.trace("Checking current platform bundle: " + bundle);
					filterBundleStarted(bundle);
				}
			}
		}

		@Override
		public void bundleChanged(BundleEvent event) {
			log.debug("Received BundleEvent: " + event + ", " + bundleEventType2String(event.getType()));

			// a new bundle got started
			if (event.getType() == BundleEvent.STARTED) {
				Bundle bundle = event.getBundle();

				// process new bundle in platform
				filterBundleStarted(bundle);

				// refresh bundles to update wires
				BundleUtils.refreshBundles();
			}

			// a previous bundle got stopped
			else if (event.getType() == BundleEvent.STOPPED) {
				Bundle bundle = event.getBundle();

				// is this bundle dependent of core.api bundle
				if (coreAPIDependentBundles.contains(bundle)) {
					// apply bundle OUT logic
					data.bundleOut(bundle);
					// remove bundle from coreAPIDependentBundles
					coreAPIDependentBundles.remove(bundle);
				}

				// refresh bundles to update wires
				BundleUtils.refreshBundles();
			}
		}

		private void filterBundleStarted(Bundle bundle) {
			try {
				// is this bundle dependent of core.api bundle
				if (BundleUtils.bundleDependsOnBundle(bundle, BundleUtils.getBundleBySymbolicName("core.api"), BundleUtils.LOOK_UP_STRATEGY.UP)) {
					// add this bundle as dependent of core.api
					coreAPIDependentBundles.add(bundle);
					// apply bundle IN logic
					data.bundleIn(bundle);
				}
			} catch (BundleNotFoundException e) {
				// this should not happen, "core.api" bundle must exist
				log.error("core.api bundle not found.", e);
			}
		}
	}

	/*
	 * Converts integer representation of BundleEvent type attribute to String extracted from <a
	 * href="http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleEvent.html">reference</a>
	 */
	private static final String bundleEventType2String(int bundleEventType) {
		switch (bundleEventType) {
			case BundleEvent.INSTALLED:
				// The bundle has been installed.
				return "INSTALLED";
			case BundleEvent.LAZY_ACTIVATION:
				// The bundle will be lazily activated
				return "LAZY_ACTIVATION";
			case BundleEvent.RESOLVED:
				// The bundle has been resolved
				return "RESOLVED";
			case BundleEvent.STARTED:
				// The bundle has been started
				return "STARTED";
			case BundleEvent.STARTING:
				// The bundle is about to be activated
				return "STARTING";
			case BundleEvent.STOPPED:
				// The bundle has been stopped
				return "STOPPED";
			case BundleEvent.STOPPING:
				// The bundle is about to deactivated
				return "STOPPING";
			case BundleEvent.UNINSTALLED:
				// The bundle has been uninstalled
				return "UNINSTALLED";
			case BundleEvent.UNRESOLVED:
				// The bundle has been unresolved
				return "UNRESOLVED";
			case BundleEvent.UPDATED:
				// The bundle has been updated
				return "UPDATED";
			default:
				return "UNKNOWN_BUNDLE_EVENT_TYPE";
		}
	}
}
