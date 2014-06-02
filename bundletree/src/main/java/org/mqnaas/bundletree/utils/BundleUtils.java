package org.mqnaas.bundletree.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mqnaas.bundletree.exceptions.BundleNotFoundException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
	 * Checks if given {@link Bundle} depends on rootBundle based on {@link BundleContext}
	 * 
	 * @param context
	 *            BundleContext to be used to check dependencies
	 * @param candidateBundle
	 *            target bundle to be checked
	 * @param rootBundle
	 *            Bundle to be checked as dependency of candidateBundle
	 * @return true if candidateBundle depends directly or indirectly on rootBundle, false otherwise
	 */
	public static boolean bundleDependsOnBundle(BundleContext context, Bundle candidateBundle, Bundle rootBundle) {
		// check if candidateBundle is rootBundle itself
		if (candidateBundle.equals(rootBundle)) {
			return true;
		}
		return bundleDependsOnBundle(context, candidateBundle, rootBundle, new HashSet<Bundle>());
	}

	// FIXME improve performance looking for positive candidates per layer.
	private static boolean bundleDependsOnBundle(BundleContext context, Bundle candidateBundle, Bundle rootBundle, Set<Bundle> visitedBundles) {
		// get required bundles by candidateBundle (direct dependency ancestors)
		Set<Bundle> candidateAncestorBundles = getAncestorBundles(candidateBundle);

		// check if any direct ancestor is rootBundle
		if (candidateAncestorBundles.contains(rootBundle)) {
			return true;
		}

		// iterate over ancestors of each direct ancestor looking for rootBundle
		for (Bundle ancestorBundle : candidateAncestorBundles) {
			// don't analyse previously visited bundles
			if (!visitedBundles.contains(ancestorBundle)) {
				if (bundleDependsOnBundle(context, ancestorBundle, rootBundle, visitedBundles)) {
					return true;
				}
				// mark candidate as visited bundle
				visitedBundles.add(ancestorBundle);
			}
		}

		// no ancestor is rootBundle
		return false;
	}

	/**
	 * Retrieves a {@link Bundle} with given <a href="http://wiki.osgi.org/wiki/Bundle-SymbolicName">Bundle-SymbolicName</a> using given
	 * {@link BundleContext}.
	 * 
	 * @param context
	 *            BundleContext to be used to look for Bundle's
	 * @param bundleSymbolicName
	 *            target Bundle-SymbolicName to be searched
	 * @return Bundle with target bundleSymbolicName
	 * @throws BundleNotFoundException
	 *             if no Bundle with given bundleSymbolicName is found
	 */
	public static Bundle getBundleBySymbolicName(BundleContext context, String bundleSymbolicName) throws BundleNotFoundException {
		Bundle[] bundles = context.getBundles();
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
