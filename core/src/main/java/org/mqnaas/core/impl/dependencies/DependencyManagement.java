package org.mqnaas.core.impl.dependencies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.mqnaas.core.impl.ApplicationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class DependencyManagement {

	private static final Logger							log			= LoggerFactory.getLogger(DependencyManagement.class);

	private Collection<ApplicationInstance>				inSystem	= new ArrayList<ApplicationInstance>();

	private ApplicationInstanceLifeCycleStateListener	eventNotifier;

	public DependencyManagement() {
		eventNotifier = new ApplicationListener(this);
	}

	public void addApplicationInTheSystem(ApplicationInstance application) {
		doInstantiate(application);

		// add in system
		inSystem.add(application);
	}

	public void removeApplicationInTheSystem(ApplicationInstance application) {
		// remove from system
		inSystem.remove(application);

		doDeactivate(application);
		doUnresolve(application);
		doDestroy(application);

		// TODO clear application internal state (i.e. dependencies)
		application.unresolveAllDependencies();

	}

	// ////////////////////////////////////////////////////////////////
	// ApplicationInstanceLifeCycleStateListener private implementation
	// invoked by ApplicationListener
	// ////////////////////////////////////////////////////////////////

	private void instantiated(ApplicationInstance application) {
		resolve(Arrays.asList(application), getApplicationInstancesInSystem());
		resolve(getApplicationInstancesInSystem(), Arrays.asList(application));
	}

	private void resolved(ApplicationInstance application) {
		activate(Arrays.asList(application));
		activate(getApplicationInstancesResolvedWith(application));
		if (dependencyChainHasCycle(application))
			activateAtOnce(getDependencyChain(application));
	}

	private void activated(ApplicationInstance application) {
		// nothing to do by now

	}

	private void deactivated(ApplicationInstance application) {
		// nothing to do by now

	}

	private void unresolved(ApplicationInstance application) {
		deactivate(getApplicationInstancesResolvedWith(application));
		if (dependencyChainHasCycle(application))
			deactivate(getDependencyChain(application));

	}

	private void destroyed(ApplicationInstance application) {
		Collection<ApplicationInstance> affected = getApplicationInstancesResolvedWith(application);
		unresolve(affected, Arrays.asList(application));
		resolve(affected, getApplicationInstancesInSystem());
	}

	// ////////////////////////////////////////////////////////////////////////
	// Collectively management of ApplicationInstanceLifeCycleState transitions
	// ////////////////////////////////////////////////////////////////////////

	private void resolve(Collection<ApplicationInstance> toResolve, Collection<ApplicationInstance> toResolveWith) {
		List<ApplicationInstance> resolvedNow = new LinkedList<ApplicationInstance>();

		for (ApplicationInstance resolving : toResolve) {
			if (!resolving.isResolved()) {
				// resolve unresolved
				for (ApplicationInstance candidate : toResolveWith) {
					if (candidate != resolving) {
						resolving.resolve(candidate);
						if (resolving.isResolved()) {
							resolvedNow.add(resolving);
							break;
						}
					}
				}
			} else {
				// doResolve auto-resolved apps
				if (resolving.getState().equals(ApplicationInstanceLifeCycleState.INSTANTIATED)) {
					resolvedNow.add(resolving);
				}
			}
		}

		for (ApplicationInstance resolved : resolvedNow) {
			doResolve(resolved);
		}
	}

	private void unresolve(Collection<ApplicationInstance> toUnresolve, Collection<ApplicationInstance> toUnresolveWith) {
		List<ApplicationInstance> unresolvedNow = new LinkedList<ApplicationInstance>();

		for (ApplicationInstance unresolving : toUnresolve) {
			boolean wasResolved = unresolving.isResolved();
			for (ApplicationInstance candidate : toUnresolveWith) {
				unresolving.unresolve(candidate);
			}
			if (wasResolved && !unresolving.isResolved())
				unresolvedNow.add(unresolving);
		}

		for (ApplicationInstance unresolved : unresolvedNow) {
			doUnresolve(unresolved);
		}

	}

	private void activate(Collection<ApplicationInstance> toActivate) {
		for (ApplicationInstance candidate : toActivate) {
			if (isDependencyChainResolved(candidate)) {
				// activate candidate
				doActivate(candidate);
			}
		}
	}

	private void deactivate(Collection<ApplicationInstance> toDeactivate) {
		for (ApplicationInstance candidate : toDeactivate) {
			doDeactivate(candidate);
		}
	}

	// /////////////////////////////////////////////
	// ApplicationInstanceLifeCycleState transitions
	// /////////////////////////////////////////////

	private void doResolve(ApplicationInstance resolved) {
		resolved.setState(ApplicationInstanceLifeCycleState.RESOLVED);
		// launch resolved event
		eventNotifier.resolved(resolved);
	}

	private void doUnresolve(ApplicationInstance unresolved) {
		unresolved.setState(ApplicationInstanceLifeCycleState.INSTANTIATED);
		// launch unresolved event
		eventNotifier.unresolved(unresolved);
	}

	private void doActivate(ApplicationInstance toActivate) {
		// activate toActivate
		ApplicationInstanceLifeCycleState previous = toActivate.getState();
		try {
			toActivate.setState(ApplicationInstanceLifeCycleState.ACTIVATING);
			toActivate.getInstance().activate();
			toActivate.setState(ApplicationInstanceLifeCycleState.ACTIVE);
			// launch activated event
			eventNotifier.activated(toActivate);
		} catch (Exception e) {
			log.error("Error activating application " + toActivate, e);
			toActivate.setState(previous);
		}
	}

	private void doDeactivate(ApplicationInstance toDeactivate) {
		// deactivate toDeactivate
		if (toDeactivate.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE)) {
			try {
				toDeactivate.setState(ApplicationInstanceLifeCycleState.DEACTIVATING);
				toDeactivate.getInstance().deactivate();
			} catch (Exception e) {
				log.error("Error deactivating application " + toDeactivate, e);
				// ignored, application is considered deactivated even if its deactivate method failed
			}
			toDeactivate.setState(ApplicationInstanceLifeCycleState.RESOLVED);
			// launch deactivated event
			eventNotifier.deactivated(toDeactivate);
		}
	}

	private void doInstantiate(ApplicationInstance toInstantiate) {
		toInstantiate.setState(ApplicationInstanceLifeCycleState.INSTANTIATED);
		// launch instantiated event
		eventNotifier.instantiated(toInstantiate);
	}

	private void doDestroy(ApplicationInstance toDestroy) {
		toDestroy.setState(ApplicationInstanceLifeCycleState.DESTROYING);
		// launch destroyed event
		eventNotifier.destroyed(toDestroy);
	}

	// ///////////////
	// private methods
	// ///////////////

	/**
	 * Activates given applications.
	 * 
	 * This methods activates given applications at once:
	 * 
	 * If a single application fails at activating, applications are left in the state they were before calling this method.
	 * 
	 * @param applications
	 */
	private void activateAtOnce(Collection<ApplicationInstance> applications) {
		Collection<ApplicationInstance> activatedNow = new ArrayList<ApplicationInstance>();
		boolean rollbackChainActivation = false;
		for (ApplicationInstance toActivate : applications) {
			if (toActivate.getState().equals(ApplicationInstanceLifeCycleState.RESOLVED)) {

				doActivate(toActivate);

				if (toActivate.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE)) {
					activatedNow.add(toActivate);
				} else {
					log.error("Error activating application " + toActivate);
					rollbackChainActivation = true;
					break;
				}
			}
		}
		// rollback if a single activation in the chain fails
		// chain activation is an atomic operation
		if (rollbackChainActivation) {
			for (ApplicationInstance activated : activatedNow) {
				doDeactivate(activated);
			}
		}
	}

	/**
	 * 
	 * @param application
	 * @return whether application dependency chain is completely resolved (any element in it is resolved) or not
	 */
	private boolean isDependencyChainResolved(ApplicationInstance application) {
		return DependencyChainUtils.isDependencyChainResolved(application, new LinkedList<ApplicationInstance>());
	}

	/**
	 * 
	 * @param application
	 * @return whether application dependency chain contains cycles or not
	 */
	private boolean dependencyChainHasCycle(ApplicationInstance application) {
		return DependencyChainUtils.dependencyChainHasCycle(application, new ArrayList<ApplicationInstance>());
	}

	/**
	 * Computes dependency chain for a given application
	 * 
	 * @param application
	 * @return
	 */
	private Collection<ApplicationInstance> getDependencyChain(ApplicationInstance application) {
		return DependencyChainUtils.getDependencyChain(application, new LinkedList<ApplicationInstance>());
	}

	/**
	 * 
	 * @return application in the system. Returned Collection is not the live one but a defensive copy.
	 */
	Collection<ApplicationInstance> getApplicationInstancesInSystem() {
		return new ArrayList<ApplicationInstance>(inSystem);
	}

	private Collection<ApplicationInstance> getApplicationInstancesResolvedWith(ApplicationInstance application) {
		List<ApplicationInstance> dependent = new LinkedList<ApplicationInstance>();
		for (ApplicationInstance candidate : getApplicationInstancesInSystem()) {
			if (candidate.getInjectedDependencies().contains(application)) {
				dependent.add(candidate);
			}
		}
		return dependent;
	}

	/**
	 * ApplicationInstanceLifeCycleStateListener delegating received calls to DependencyManagement given in the constructor.
	 * 
	 * This class is used in DependencyManagement to notify itself of ApplicationInstanceLifeCycleState changes.
	 * 
	 * In the future, it may be extended to launch broader impact events (i.e. notifying to other components in the system)
	 */
	class ApplicationListener implements ApplicationInstanceLifeCycleStateListener {

		DependencyManagement	relay;

		public ApplicationListener(DependencyManagement dependencyManagement) {
			relay = dependencyManagement;
		}

		@Override
		public void instantiated(ApplicationInstance application) {
			relay.instantiated(application);
		}

		@Override
		public void resolved(ApplicationInstance application) {
			relay.resolved(application);
		}

		@Override
		public void activated(ApplicationInstance application) {
			relay.activated(application);
		}

		@Override
		public void deactivated(ApplicationInstance application) {
			relay.deactivated(application);
		}

		@Override
		public void unresolved(ApplicationInstance application) {
			relay.unresolved(application);
		}

		@Override
		public void destroyed(ApplicationInstance application) {
			relay.destroyed(application);
		}

	}

}
