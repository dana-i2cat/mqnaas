package org.opennaas.core.impl.bundletree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

public class BundleTreeUtils {

	private Tree<Bundle>	tree;

	/*
	 * Creates the bundle tree
	 */
	protected void createTree(Bundle bundle) {
		if (bundle.getState() >= Bundle.RESOLVED) {
			createNode(tree);
		} else {
			System.out.println("Can not create Bundle tree for not resolved bundles");
		}
	}

	/*
	 * Creates a node in the bundle tree
	 */
	private void createNode(Node<Bundle> node) {
		Bundle bundle = node.getValue();
		Collection<Bundle> exporters = new HashSet<Bundle>();
		exporters.addAll(getWiredBundles(bundle).values());

		for (Bundle exporter : exporters) {
			if (node.hasAncestor(exporter)) {
				// System.out.println(String.format("Skipping %s (already exists in the current branch)", exporter));
			} else {
				boolean existing = tree.flatten().contains(exporter);
				// System.out.println(String.format("Adding %s as a dependency for %s", exporter, bundle));
				Node<Bundle> child = node.addChild(exporter);
				if (existing) {
					// System.out.println(String.format("Skipping children of %s (already exists in another branch)", exporter));
				} else {
					createNode(child);
				}
			}
		}
	}

	/*
	 * Get the list of bundles from which the given bundle imports packages
	 */
	protected Map<String, Bundle> getWiredBundles(Bundle bundle) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		// the set of bundles from which the bundle imports packages
		Map<String, Bundle> exporters = new HashMap<String, Bundle>();

		for (BundleWire pkg : wiring.getRequiredWires(null)) {
			Bundle providerBundle = pkg.getProviderWiring().getBundle();
			exporters.put(bundle.getSymbolicName(), providerBundle);
		}
		return exporters;
	}

	public static boolean isBundleDependant(Bundle bundle, String bundleSymbolicName) {
		// skip bundle itself
		if (bundle.getSymbolicName().equals(bundleSymbolicName)) {
			return false;
		}

		BundleTreeUtils bundleTreeUtils = new BundleTreeUtils();
		bundleTreeUtils.tree = new Tree<Bundle>(bundle);

		bundleTreeUtils.createTree(bundle);

		Set<Bundle> flattenBundles = bundleTreeUtils.tree.flatten();

		for (Bundle flattenBundle : flattenBundles) {
			// System.out.println("Bundle " + bundle.getSymbolicName() + " depends on " + flattenBundle.getSymbolicName());
			if (flattenBundle.getSymbolicName().equals(bundleSymbolicName)) {
				return true;
			}

		}

		return false;
	}
}
