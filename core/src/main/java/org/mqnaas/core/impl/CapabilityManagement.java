package org.mqnaas.core.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;
import org.mqnaas.bundletree.utils.BundleClassPathUtils;
import org.mqnaas.core.api.ICapability;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class CapabilityManagement {

	private static final Logger	log	= LoggerFactory.getLogger(CapabilityManagement.class);

	// Stores a list of bundles that require the bundle used as a key
	private Dependencies		dependencies;

	// Stores the capabilities of each bundle
	private Capabilities		capabilities;

	public CapabilityManagement() {
		dependencies = new Dependencies();
		capabilities = new Capabilities();
	}

	private class Capabilities {
		private Multimap<Bundle, Class<? extends ICapability>>	capabilitiesPerBundle	= HashMultimap.create();

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Bundle b : capabilitiesPerBundle.keySet()) {
				sb.append(shortName(b)).append(" <-- ");
				for (Class<? extends ICapability> c : capabilitiesPerBundle.get(b)) {
					sb.append(c.getSimpleName()).append(" ");
				}
				sb.append("\n");
			}

			return sb.toString();
		}

		public boolean addCapability(Bundle bundle, Class<? extends ICapability> capability) {

			boolean added = false;

			if (capabilitiesPerBundle.containsValue(capability)) {
				// Find the bundle that hosts this capability at the moment
				Bundle containingBundle = null;
				for (Bundle b : capabilitiesPerBundle.keySet()) {
					if (capabilitiesPerBundle.get(b).contains(capability)) {
						containingBundle = b;
						break;
					}
				}

				// Determine the relation between the current bundle and the containing bundle
				if (dependencies.isParent(bundle, containingBundle)) {
					// Move the capability...
					capabilitiesPerBundle.remove(containingBundle, capability);
					capabilitiesPerBundle.put(bundle, capability);
				}
			} else {
				capabilitiesPerBundle.put(bundle, capability);
				added = true;
			}

			return added;
		}

		public Collection<Class<? extends ICapability>> getAllCapabilityClasses() {
			return capabilitiesPerBundle.values();
		}

	}

	private class Dependencies {

		private Multimap<Bundle, Bundle>	dependencies	= HashMultimap.create();

		public void addDependency(Bundle parent, Bundle child) {

			Collection<Bundle> family = Arrays.asList(parent, child);

			for (Bundle bundle : dependencies.keySet()) {
				if (dependencies.get(bundle).containsAll(family)) {
					dependencies.remove(bundle, child);
				}
			}

			// Remove all direct links between the parents of the child and the the parent
			for (Bundle bundle : dependencies.get(child)) {
				dependencies.remove(parent, bundle);
			}

			if (!isParent(parent, child)) {
				dependencies.put(parent, child);
			}

		}

		private boolean isParent(Bundle parent, Bundle child) {

			Set<Bundle> parents = new HashSet<Bundle>();
			parents.add(parent);

			Set<Bundle> childs = new HashSet<Bundle>();
			for (Bundle p : parents)
				childs.addAll(dependencies.get(p));

			while (!childs.contains(child) && !childs.isEmpty()) {
				parents = new HashSet<Bundle>(childs);
				childs.clear();
				for (Bundle p : parents)
					childs.addAll(dependencies.get(p));
			}

			return childs.contains(child);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Bundle b : dependencies.keySet()) {
				sb.append(shortName(b)).append(" <-- ");
				for (Bundle c : dependencies.get(b)) {
					sb.append(shortName(c)).append(" ");
				}
				sb.append("\n");
			}

			return sb.toString();
		}

	}

	private static Map<String, String>	shortBundleNames;

	static {
		shortBundleNames = new HashedMap<String, String>();

		shortBundleNames.put("org.opennaas.Router", "Router");
		shortBundleNames.put("org.opennaas.JunosRouter", "JunosRouter");
		shortBundleNames.put("org.opennaas.OpenerRouter", "OpenerRouter");
		shortBundleNames.put("org.eclipse.osgi", "org.eclipse.osgi");
		shortBundleNames.put("org.opennaas.api.Router", "api.Router");
		shortBundleNames.put("org.opennaas.Core", "Core");
		shortBundleNames.put("com.google.guava", "guava");
		shortBundleNames.put("org.eclipse.osgi.util", "org.elipse.osgi.util");
		shortBundleNames.put("org.apache.felix.gogo.runtime", "felix.gogo.runtime");

	}

	private String shortName(Bundle bundle) {

		for (Map.Entry<String, String> entry : shortBundleNames.entrySet()) {
			if (bundle.getSymbolicName().startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return bundle.getSymbolicName();
	}

	/**
	 * Adds the capabilities of the given bundle and returns the set of newly added capabilities
	 */
	public Set<Class<? extends ICapability>> addBundle(Bundle bundle) {
		log.trace(getClass().getSimpleName() + ": scanning bundle " + shortName(bundle));

		// Collect here all capabilities that are newly added
		Set<Class<? extends ICapability>> added = new HashSet<Class<? extends ICapability>>();

		// Filter bundles depending on core and scan all class resources of the given bundle and collects all capability classes, e.g. those which
		// implement ICapability
		Set<Class<? extends ICapability>> bundleCapabilities = BundleClassPathUtils.scanBundle(bundle, ICapability.class);

		if (!bundleCapabilities.isEmpty()) {

			// 1. Update the dependency tree
			for (Bundle requiredBundle : getRequiredBundles(bundle)) {
				dependencies.addDependency(requiredBundle, bundle);
			}

			// 2. Update the capabilities
			for (Class<? extends ICapability> capability : bundleCapabilities) {
				if (capabilities.addCapability(bundle, capability)) {
					added.add(capability);
				}
			}
		}

		return added;
	}

	private Set<Bundle> getRequiredBundles(Bundle bundle) {

		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		Set<Bundle> requiredBundles = new HashSet<Bundle>();

		for (BundleWire wire : wiring.getRequiredWires(null)) {
			requiredBundles.add(wire.getProviderWiring().getBundle());
		}

		return requiredBundles;
	}

	public Collection<Class<? extends ICapability>> getAllCapabilityClasses() {
		return this.capabilities.getAllCapabilityClasses();
	}

	@Override
	public String toString() {
		return dependencies.toString();
	}

}
