package org.mqnaas.core.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class Activator implements BundleActivator {

	BundleListener	bundleListener;

	public void start(BundleContext context) throws Exception {
		initCore(context);
	}

	public void stop(BundleContext context) throws Exception {
		stopCore(context);
	}

	private void initCore(BundleContext context) throws Exception {

		// The inner core services are instantiated directly...
		// TODO resolve the instances implementing these interfaces using a internal resolving mechanism. they should be resolved before other
		// dependencies resolution
		ExecutionService executionServiceInstance = new ExecutionService();

		BindingManagement bindingManagement = new BindingManagement();
		bindingManagement.resourceManagement = new RootResourceManagement();
		bindingManagement.bindingDecider = new BinderDecider();
		bindingManagement.executionService = executionServiceInstance;
		bindingManagement.observationService = executionServiceInstance;

		bindingManagement.init();

		new CapabilitiesAndApplicationsTracker(bindingManagement).init(context);

	}

	private void stopCore(BundleContext context) {
		context.removeBundleListener(bundleListener);
	}

	/**
	 * Tracks bundles in the system and reports given {@link BindingManagement} whenever new capabilities or applications are detected.
	 */
	private class CapabilitiesAndApplicationsTracker {

		BindingManagement				listener;

		// Manages the bundle dependency tree and the capabilities each bundle offers
		private CapabilityManagement	capabilityManagement;
		private ApplicationManagement	applicationManagement;

		CapabilitiesAndApplicationsTracker(BindingManagement bindingManagement) {
			this.listener = bindingManagement;

			// Initialize the capability management, which is used to track
			// capability implementations whenever bundles change...
			capabilityManagement = new CapabilityManagement();

			// ...and the application management, where deployed applications are
			// tracked
			applicationManagement = new ApplicationManagement();
		}

		void init(BundleContext context) {

			// There are two ways of adding bundles to the system, each of which will be handled

			// Way 1. If bundles are added or removed at runtime, the following
			// listener reacts (adding the hook before scanning the already loaded
			// ones to make sure we don't miss any).
			bundleListener = new BundleListener() {
				@Override
				public void bundleChanged(BundleEvent event) {
					if (event.getType() == BundleEvent.STARTED) {
						listener.capabilitiesAdded(capabilityManagement.addBundle(event.getBundle()));
						listener.applicationsAdded(applicationManagement.addBundle(event.getBundle()));
					}
				}
			};
			context.addBundleListener(bundleListener);

			// Way 2. If bundles are already active, add them now
			for (Bundle bundle : context.getBundles()) {
				if (bundle.getState() == Bundle.ACTIVE) {
					listener.capabilitiesAdded(capabilityManagement.addBundle(bundle));
					listener.applicationsAdded(applicationManagement.addBundle(bundle));
				}
			}
		}

	}

}
