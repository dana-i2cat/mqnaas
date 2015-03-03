package org.mqnaas.core.impl.dependencies;

/*
 * #%L
 * MQNaaS :: Core
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

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.ApplicationInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class DependencyChainUtils {

	/**
	 * Recursive method that checks whether the dependency chain of given application is resolved (any element in it is resolved) or not (at least one
	 * element in it is not resolved)
	 * 
	 * @param application
	 * @param alreadyVisited
	 *            collection containing visited nodes in the dependency graph (used to break cycles and performance improvement)
	 * @return
	 */
	public static boolean isDependencyChainResolved(ApplicationInstance application, Collection<ApplicationInstance> alreadyVisited) {

		// avoid treating elements more than once (notice this avoids getting into cycles too)
		if (alreadyVisited.contains(application))
			// application has been visited and the search has continued,
			// it means application is resolved but there is more than one path to application in computed dependency chain (there may be a cycle)
			// application's dependency chain is still being computed
			// thus, return true so previously started computation is not interrupted,
			// but do not keep computing over an already started path
			return true;

		if (!application.isResolved()) {
			return false;
		}

		alreadyVisited.add(application);

		for (ApplicationInstance dependency : application.getInjectedDependencies()) {
			if (!isDependencyChainResolved(dependency, alreadyVisited))
				return false;
		}

		// application is resolved, its dependencies are resolved
		// and their dependencies too and the dependencies of those ones, and so on
		return true;
	}

	/**
	 * Recursive method that computes dependency chain for a given application
	 * 
	 * @param application
	 * @param alreadyVisited
	 *            collection containing visited nodes in the dependency graph (used to break cycles and performance improvement)
	 * @return collection containing nodes in the dependency chain
	 */
	public static Collection<ApplicationInstance> getDependencyChain(ApplicationInstance application, Collection<ApplicationInstance> alreadyVisited) {

		// avoid treating elements more than once (notice this avoids getting into cycles too)
		if (alreadyVisited.contains(application))
			return alreadyVisited;

		alreadyVisited.add(application);

		for (ApplicationInstance dependency : application.getInjectedDependencies()) {
			getDependencyChain(dependency, alreadyVisited);
		}

		return alreadyVisited;
	}

	/**
	 * Recursive method that looks for cycles in dependency chain of given application.
	 * 
	 * @param application
	 * @param path
	 *            collection containing the path followed to reach application (a cycle is detected when application is already in the path)
	 * @return
	 */
	public static boolean dependencyChainHasCycle(ApplicationInstance application, Collection<ApplicationInstance> path) {

		// detect cycles
		if (path.contains(application))
			return true;

		Collection<ApplicationInstance> updatedPath = new ArrayList<ApplicationInstance>(path);
		updatedPath.add(application);

		for (ApplicationInstance dependency : application.getInjectedDependencies()) {
			if (dependencyChainHasCycle(dependency, updatedPath))
				return true;
		}

		return false;
	}

}
