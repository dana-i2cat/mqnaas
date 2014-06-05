package org.mqnaas.bundletree.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mqnaas.bundletree.exceptions.BundleNotFoundException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Bundle Utilities.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class BundleUtils {

	/**
	 * 
	 *
	 */
	public enum LOOK_UP_STRATEGY {
		UP, DOWN
	}

	/**
	 * Checks if given {@link Bundle} depends on rootBundle based on {@link BundleContext}
	 * 
	 * @param candidateBundle
	 *            target bundle to be checked
	 * @param rootBundle
	 *            Bundle to be checked as dependency of candidateBundle
	 * @return true if candidateBundle depends directly or indirectly on rootBundle, false otherwise
	 */
	public static boolean bundleDependsOnBundle(Bundle candidateBundle, Bundle rootBundle, LOOK_UP_STRATEGY strategy) {
		switch (strategy) {
			case UP:
				return bundleDependsOnBundleUp(candidateBundle, rootBundle);
			case DOWN:
				return bundleDependsOnBundleDown(candidateBundle, rootBundle);
		}
		// this code should never be reached, but compiler does not detect switch cases
		throw new IllegalArgumentException("Invalid strategy!");
	}

	/**
	 * Checks if given {@link Bundle} depends on rootBundle based on {@link BundleContext}. It looks for rootBundle as direct or indirect dependency
	 * ancestor of candidateBundle.
	 * 
	 * @param candidateBundle
	 *            target bundle to be checked
	 * @param rootBundle
	 *            Bundle to be checked as dependency of candidateBundle
	 * @return true if candidateBundle depends directly or indirectly on rootBundle, false otherwise
	 */
	private static boolean bundleDependsOnBundleUp(Bundle candidateBundle, Bundle rootBundle) {
		// System.out.println("Checking if " + candidateBundle + " depends on " + rootBundle);
		// check if candidateBundle is rootBundle itself
		if (candidateBundle.equals(rootBundle)) {
			return true;
		}
		return bundleDependsOnBundleUp(new HashSet<Bundle>(Arrays.asList(candidateBundle)), rootBundle, new HashSet<Bundle>());
	}

	private static boolean bundleDependsOnBundleUp(Set<Bundle> candidateAncestorBundles, Bundle rootBundle,
			Set<Bundle> visitedBundles) {
		// check if any direct ancestor is rootBundle
		// System.out.println("\tChecking if " + rootBundle + " is an ancestor of " + candidateAncestorBundles);
		if (candidateAncestorBundles.contains(rootBundle)) {
			return true;
		}

		Set<Bundle> nextAncestordBundles = new HashSet<Bundle>();
		// obtain next layer ancestor bundles
		for (Bundle candidateAncestorBundle : candidateAncestorBundles) {
			// add next ancestor bundles to set
			nextAncestordBundles.addAll(getAncestorBundles(candidateAncestorBundle));
		}

		// remove previously visited bundles
		nextAncestordBundles.removeAll(visitedBundles);

		// if all the nextAncestordBundles have been visited previously, it is not necessary to continue looking for rootBundle
		if (nextAncestordBundles.isEmpty()) {
			// System.out.println("No new ancestors, returning false.");
			return false;
		}

		// check ancestor bundles recursively
		// System.out.println("\tNext Layer: " + nextAncestordBundles);
		if (bundleDependsOnBundleUp(nextAncestordBundles, rootBundle, visitedBundles)) {
			return true;
		}

		// no ancestor bundle is rootBundle
		// System.out.println("No " + rootBundle + " in ancestors, returning false.");
		return false;
	}

	/**
	 * Checks if given {@link Bundle} depends on rootBundle based on {@link BundleContext}. It looks for candidateBundle as direct or indirect
	 * dependency child of rootBundle.
	 * 
	 * @param candidateBundle
	 *            target bundle to be checked
	 * @param rootBundle
	 *            Bundle to be checked as dependency of targetBundle
	 * @return true if candidateBundle depends directly or indirectly on rootBundle, false otherwise
	 */
	private static boolean bundleDependsOnBundleDown(Bundle candidateBundle, Bundle rootBundle) {
		// System.out.println("Checking if " + candidateBundle + " depends on " + rootBundle);
		// check if candidateBundle is rootBundle itself
		if (candidateBundle.equals(rootBundle)) {
			return true;
		}
		return bundleDependsOnBundleDown(candidateBundle, new HashSet<Bundle>(Arrays.asList(rootBundle)), new HashSet<Bundle>());
	}

	private static boolean bundleDependsOnBundleDown(Bundle candidateBundle, Set<Bundle> candidateChildBundles,
			Set<Bundle> visitedBundles) {
		// check if any direct child is candidateBundle
		// System.out.println("\tChecking if " + candidateBundle + " depends on " + candidateChildBundles);
		if (candidateChildBundles.contains(candidateBundle)) {
			return true;
		}

		Set<Bundle> nextChildBundles = new HashSet<Bundle>();
		// obtain next layer child bundles
		for (Bundle candidateChildBundle : candidateChildBundles) {
			// add next child bundles to set
			nextChildBundles.addAll(getChildBundles(candidateChildBundle));
		}

		// remove previously visited bundles
		nextChildBundles.removeAll(visitedBundles);

		// if all the nextChildbundles have been visited previously, it is not necessary to continue looking for candidateBundle
		if (nextChildBundles.isEmpty()) {
			// System.out.println("No new childs, returning false.");
			return false;
		}

		// check child bundles recursively
		// System.out.println("\tNext Layer: " + nextChildBundles);
		if (bundleDependsOnBundleDown(candidateBundle, nextChildBundles, visitedBundles)) {
			return true;
		}

		// no child bundle is candidateBundle
		// System.out.println("No " + candidateBundle + " in childs, returning false.");
		return false;
	}

	/**
	 * Retrieves a {@link Bundle} with given <a href="http://wiki.osgi.org/wiki/Bundle-SymbolicName">Bundle-SymbolicName</a> using given
	 * {@link BundleContext}.
	 * 
	 * @param bundleSymbolicName
	 *            target Bundle-SymbolicName to be searched
	 * @return Bundle with target bundleSymbolicName
	 * @throws BundleNotFoundException
	 *             if no Bundle with given bundleSymbolicName is found
	 */
	public static Bundle getBundleBySymbolicName(String bundleSymbolicName) throws BundleNotFoundException {
		Bundle[] bundles = FrameworkUtil.getBundle(BundleUtils.class).getBundleContext().getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals(bundleSymbolicName)) {
				return bundle;
			}
		}
		throw new BundleNotFoundException("Bundle-SymbolicName: " + bundleSymbolicName + " not present in the system.");
	}

	/**
	 * Retrieves the set of bundles from which given bundle depends.
	 * 
	 * @param bundle
	 *            target bundle
	 * @return set of ancestor bundles
	 */
	public static Set<Bundle> getAncestorBundles(Bundle bundle) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		// the set of bundles from which the bundle imports packages
		Set<Bundle> exporters = new HashSet<Bundle>();

		if (wiring != null) {
			List<BundleWire> bundleWires = wiring.getRequiredWires(null);

			if (bundleWires != null) {
				for (BundleWire pkg : bundleWires) {
					Bundle providerBundle = pkg.getProviderWiring().getBundle();
					exporters.add(providerBundle);
				}
			}
		}

		return exporters;
	}

	/**
	 * Retrieves the set of bundles dependent of given bundle.
	 * 
	 * @param bundle
	 *            target bundle
	 * @return set of child bundles
	 */
	public static Set<Bundle> getChildBundles(Bundle bundle) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);

		Set<Bundle> dependencies = new HashSet<Bundle>();

		if (wiring != null) {
			for (BundleWire requiredWire : wiring.getProvidedWires(null)) {
				dependencies.add(requiredWire.getRequirerWiring().getBundle());
			}
		}

		return dependencies;
	}

}