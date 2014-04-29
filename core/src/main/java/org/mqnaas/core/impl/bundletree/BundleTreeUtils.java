package org.mqnaas.core.impl.bundletree;

import java.util.Collection;
import java.util.HashSet;
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
		exporters.addAll(getWiredBundles(bundle));

		for (Bundle exporter : exporters) {
			if (node.hasAncestor(exporter)) {
				// FIXME use logger
				// System.out.println(String.format("Skipping %s (already exists in the current branch)", exporter));
			} else {
				boolean existing = tree.flatten().contains(exporter);
				// FIXME use logger
				// System.out.println(String.format("Adding %s as a dependency for %s", exporter, bundle));
				Node<Bundle> child = node.addChild(exporter);
				if (existing) {
					// FIXME use logger
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
	protected Set<Bundle> getWiredBundles(Bundle bundle) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		// the set of bundles from which the bundle imports packages
		Set<Bundle> exporters = new HashSet<Bundle>();

		for (BundleWire pkg : wiring.getRequiredWires(null)) {
			Bundle providerBundle = pkg.getProviderWiring().getBundle();
			exporters.add(providerBundle);
		}

		return exporters;
	}

	/**
	 * Check if {@link Bundle} <code>bundle</code> depends on another bundle named <code>bundleSymbolicName</code>
	 * 
	 * @param bundle
	 *            Bundle to be checked
	 * @param bundleSymbolicName
	 *            Bundle Symbolic Name of the dependency to be checked
	 * @return true if <code>bundleSymbolicName</code> is a dependency of <code>bundle</code>, false otherwise
	 */
	public static boolean isBundleDependant(Bundle bundle, String bundleSymbolicName) {
		// skip bundle itself
		if (bundle.getSymbolicName().equals(bundleSymbolicName)) {
			return false;
		}

		BundleTreeUtils bundleTreeUtils = new BundleTreeUtils();
		bundleTreeUtils.tree = new Tree<Bundle>(bundle);

		bundleTreeUtils.createTree(bundle);

		// FIXME use logger
		// System.out.println("Bundle " + bundle.getSymbolicName() + " dependency tree:\n" + bundleTreeUtils.getStringTree());

		Set<Bundle> flattenBundles = bundleTreeUtils.tree.flatten();

		for (Bundle flattenBundle : flattenBundles) {
			// FIXME use logger
			// System.out.println("Bundle " + bundle.getSymbolicName() + " depends on " + flattenBundle.getSymbolicName());
			if (flattenBundle.getSymbolicName().equals(bundleSymbolicName)) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Generates human readable String of the dependency tree
	 */
	public String getStringTree() {
		return stringNode("", tree);
	}

	public String stringNode(String prefix, Node<Bundle> node) {
		StringBuilder sb = new StringBuilder();
		for (Node<Bundle> childNode : node.getChildren()) {
			sb.append(String.format("%s \u2517 %s [%s]\n", prefix, childNode.getValue().getSymbolicName(), childNode.getValue().getBundleId()));
			sb.append(stringNode("\t" + prefix, childNode));
		}

		return sb.toString();
	}
}
