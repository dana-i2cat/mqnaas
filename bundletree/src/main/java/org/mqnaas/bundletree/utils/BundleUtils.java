package org.mqnaas.bundletree.utils;

/*
 * #%L
 * MQNaaS :: BundleTree
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundle Utilities.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class BundleUtils {

	private static final Logger	log	= LoggerFactory.getLogger(BundleUtils.class);

	/**
	 * Strategy chooser enum for {@link #bundleDependsOnBundle(Bundle, Bundle, LOOK_UP_STRATEGY)} method
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
		log.debug("Checking if " + candidateBundle + " depends on " + rootBundle);
		// check if candidateBundle is rootBundle itself
		if (candidateBundle.equals(rootBundle)) {
			return true;
		}
		return bundleDependsOnBundleUp(new HashSet<Bundle>(Arrays.asList(candidateBundle)), rootBundle, new HashSet<Bundle>());
	}

	private static boolean bundleDependsOnBundleUp(Set<Bundle> candidateAncestorBundles, Bundle rootBundle,
			Set<Bundle> visitedBundles) {
		// check if any direct ancestor is rootBundle
		log.trace("\tChecking if " + rootBundle + " is an ancestor of " + candidateAncestorBundles);
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
			log.trace("No new ancestors, returning false.");
			return false;
		}

		// add bundle to visitiedBundles
		visitedBundles.addAll(nextAncestordBundles);

		// check ancestor bundles recursively
		log.trace("\tNext Layer: " + nextAncestordBundles);
		if (bundleDependsOnBundleUp(nextAncestordBundles, rootBundle, visitedBundles)) {
			return true;
		}

		// no ancestor bundle is rootBundle
		log.trace("No " + rootBundle + " in ancestors, returning false.");
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
		log.debug("Checking if " + candidateBundle + " depends on " + rootBundle);
		// check if candidateBundle is rootBundle itself
		if (candidateBundle.equals(rootBundle)) {
			return true;
		}
		return bundleDependsOnBundleDown(candidateBundle, new HashSet<Bundle>(Arrays.asList(rootBundle)), new HashSet<Bundle>());
	}

	private static boolean bundleDependsOnBundleDown(Bundle candidateBundle, Set<Bundle> candidateChildBundles,
			Set<Bundle> visitedBundles) {
		// check if any direct child is candidateBundle
		log.trace("\tChecking if " + candidateBundle + " depends on " + candidateChildBundles);
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
			log.trace("No new childs, returning false.");
			return false;
		}

		// add bundle to visitiedBundles
		visitedBundles.addAll(nextChildBundles);

		// check child bundles recursively
		log.trace("\tNext Layer: " + nextChildBundles);
		if (bundleDependsOnBundleDown(candidateBundle, nextChildBundles, visitedBundles)) {
			return true;
		}

		// no child bundle is candidateBundle
		log.trace("No " + candidateBundle + " in childs, returning false.");
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

	/**
	 * Refreshes bundles using FrameworkWiring and current Bundle (<a
	 * href="https://mail.osgi.org/pipermail/osgi-dev/2014-June/004459.html">reference</a>).
	 */
	public static void refreshBundles() {
		log.trace("Refreshing bundles...");

		Bundle currentBundle = FrameworkUtil.getBundle(BundleUtils.class);
		if (currentBundle == null) {
			log.error("Could not obtain current bundle! Not refreshing Bundles.");
			return;
		}

		BundleContext bundleContext = currentBundle.getBundleContext();
		if (bundleContext == null) {
			log.error("Could not obtain bundle context! Not refreshing Bundles.");
			return;
		}

		Bundle systemBundle = bundleContext.getBundle(0);
		if (systemBundle == null) {
			log.error("Could not obtain system bundle! Not refreshing Bundles.");
			return;
		}

		FrameworkWiring frameworkWiring = systemBundle.adapt(FrameworkWiring.class);
		if (frameworkWiring == null) {
			log.error("Could not obtain FrameworkWiring from system bundle! Not refreshing Bundles.");
			return;
		}

		frameworkWiring.refreshBundles(null);
	}

}
