package org.mqnaas.core.impl.dependencies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.impl.ApplicationInstance;
import org.mqnaas.core.impl.dependencies.samples.IApp;
import org.mqnaas.core.impl.dummy.DummyExecutionService;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@RunWith(Parameterized.class)
public class DependencyManagementTest {

	DependencyManagement	depManager;

	Collection<IApp>		apps;

	public DependencyManagementTest(Collection<IApp> apps, String name) {
		this.apps = apps;
	}

	/**
	 * method which provides parameters to be injected into the test class constructor by Parameterized
	 * 
	 * This method loads some testing scenarios. Scenarios are described in a README file in the scenario package.
	 * 
	 * @return
	 */
	@Parameterized.Parameters(name = "{index}: depManagement({1})")
	public static Iterable<Object[]> data() {
		Collection<Object[]> data = new ArrayList<Object[]>();
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._1_4inline.ScenarioInitializer.getInstances(), "4inline").toArray());
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._2_tree.ScenarioInitializer.getInstances(), "tree").toArray());
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._3_cycle.ScenarioInitializer.getInstances(), "cycle").toArray());
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._4_multicycle.ScenarioInitializer.getInstances(), "multicycle").toArray());
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._5_minicycle.ScenarioInitializer.getInstances(), "minicycle").toArray());
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._6_4cycle.ScenarioInitializer.getInstances(), "4cycle").toArray());
		data.add(Arrays.asList(org.mqnaas.core.impl.dependencies.samples._7_multipleimpl.ScenarioInitializer.getInstances(), "multipleimpl")
				.toArray());
		return data;
	}

	@Before
	public void initDepManager() {
		depManager = new DependencyManagement();
		// add execution service that is required for any ApplicationInstance
		depManager.addApplicationInTheSystem(createApp(new DummyExecutionService()));
	}

	@Test
	public void addedApplicationsAreInSystem() {

		// add application instances to system
		List<ApplicationInstance> added = new ArrayList<ApplicationInstance>();
		for (IApp iapp : apps) {
			ApplicationInstance app = createApp(iapp);
			depManager.addApplicationInTheSystem(app);
			added.add(app);
			Assert.assertTrue(depManager.getApplicationInstancesInSystem().contains(app));
		}

		for (ApplicationInstance app : added) {
			Assert.assertTrue(depManager.getApplicationInstancesInSystem().contains(app));
		}

	}

	@Test
	public void removedApplicationsAreNotInSystem() {

		// add application instances to system
		List<ApplicationInstance> added = new ArrayList<ApplicationInstance>();
		for (IApp iapp : apps) {
			ApplicationInstance app = createApp(iapp);
			depManager.addApplicationInTheSystem(app);
			added.add(app);
			Assert.assertTrue(depManager.getApplicationInstancesInSystem().contains(app));
		}

		for (ApplicationInstance app : added) {
			depManager.removeApplicationInTheSystem(app);
			Assert.assertFalse(depManager.getApplicationInstancesInSystem().contains(app));
		}

	}

	@Test
	public void newcommingAppIsResolvedWithExistingApps() {

		for (IApp iapp : apps) {
			ApplicationInstance app = createApp(iapp);
			Collection<Class<? extends IApplication>> dependencies = app.getPendingClasses();

			depManager.addApplicationInTheSystem(app);

			List<ApplicationInstance> matching;
			for (Class<? extends IApplication> dependency : dependencies) {
				// get applications in system satisfying dependency
				matching = new ArrayList<ApplicationInstance>();
				for (ApplicationInstance existing : depManager.getApplicationInstancesInSystem()) {
					if (existing != app) {
						if (existing.getApplications().contains(dependency)) {
							matching.add(existing);
						}
					}
				}
				if (!matching.isEmpty()) {
					// check a matching one has been injected
					boolean found = false;
					for (ApplicationInstance candidate : matching) {
						if (app.getInjectedDependencies().contains(candidate)) {
							found = true;
							break;
						}
					}
					Assert.assertTrue(
							"An existing ApplicationInstance resolving dependency " + dependency + " has been injected to app " + app + ", if any",
							found);
				}
			}
		}
	}

	@Test
	public void newcommingAppIsUsedToResolveExistingApps() {

		for (IApp iapp : apps) {
			ApplicationInstance app = createApp(iapp);
			Collection<Class<? extends IApplication>> implemented = app.getApplications();
			// calculate existing application instances that depend on app
			List<ApplicationInstance> depending = new ArrayList<ApplicationInstance>();
			for (ApplicationInstance existing : depManager.getApplicationInstancesInSystem()) {
				for (Class<? extends IApplication> dependency : existing.getPendingClasses()) {
					if (implemented.contains(dependency)) {
						depending.add(existing);
						break;
					}
				}
			}

			// add app
			depManager.addApplicationInTheSystem(app);

			// check app has been injected
			for (ApplicationInstance dependent : depending) {
				Assert.assertTrue(
						"Existing ApplicationInstances having unsatisfied dependencies that are implemented by app get app injected when app is added to the system",
						dependent.getInjectedDependencies().contains(app));
			}

		}
	}

	@Test
	public void appWithAllDependenciesAssignedIsResolved() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);

			// check all apps without pending classes are resolved when added
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (app.getPendingClasses().isEmpty()) {
					Assert.assertTrue(app.isResolved());
					Assert.assertTrue("State of app " + app + " should be RESOLVED or more",
							app.getState().equals(ApplicationInstanceLifeCycleState.RESOLVED)
									|| app.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE));
				}
			}
		}

		// check all apps without pending classes are resolved
		for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
			if (app.getPendingClasses().isEmpty()) {
				Assert.assertTrue(app.isResolved());
				Assert.assertTrue("State of app " + app + " should be RESOLVED or more",
						app.getState().equals(ApplicationInstanceLifeCycleState.RESOLVED)
								|| app.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE));
			}
		}
	}

	@Test
	public void appWithAllDependencyChainResolvedIsActivated() {
		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);

			// check all apps with dependency chain resolved are active
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (DependencyChainUtils.isDependencyChainResolved(app, new LinkedList<ApplicationInstance>())) {
					Assert.assertEquals(ApplicationInstanceLifeCycleState.ACTIVE, app.getState());
				}
			}
		}
	}

	@Test
	public void removedAppsAreNotInjectedAsDependencies() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
			depManager.removeApplicationInTheSystem(app);
			Assert.assertFalse(depManager.getApplicationInstancesInSystem().contains(app));

			for (ApplicationInstance candidate : depManager.getApplicationInstancesInSystem()) {
				Assert.assertFalse(candidate.getInjectedDependencies().contains(app));
			}
		}
	}

	@Test
	public void appsUsingDependencyAreDeactivatedWhenDependencyIsRemoved() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		for (ApplicationInstance toRemove : depManager.getApplicationInstancesInSystem()) {
			// calculate ApplicationInstances depending on toRemove
			List<ApplicationInstance> depending = new ArrayList<ApplicationInstance>();
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (app != toRemove) {
					if (app.getInjectedDependencies().contains(toRemove)) {
						depending.add(app);
					}
				}
			}

			depManager.removeApplicationInTheSystem(toRemove);

			for (ApplicationInstance app : depending) {
				Assert.assertNotEquals(app.getState(), ApplicationInstanceLifeCycleState.ACTIVE);
			}
		}
	}

	@Test
	public void appsUsingDependencyAreNoLongerResolvedWhenDependencyIsRemoved() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		for (ApplicationInstance toRemove : depManager.getApplicationInstancesInSystem()) {
			// calculate ApplicationInstances depending on toRemove
			List<ApplicationInstance> depending = new ArrayList<ApplicationInstance>();
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (app != toRemove) {
					if (app.getInjectedDependencies().contains(toRemove)) {
						depending.add(app);
					}
				}
			}

			depManager.removeApplicationInTheSystem(toRemove);

			for (ApplicationInstance app : depending) {
				Assert.assertNotEquals(app.getState(), ApplicationInstanceLifeCycleState.RESOLVED);
				Assert.assertFalse(app.isResolved());
			}
		}
	}

	@Test
	public void appsWithRemovedAppInDependencyChainAreDeactivated() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		for (ApplicationInstance toRemove : depManager.getApplicationInstancesInSystem()) {

			List<ApplicationInstance> withRemovedAppInDependencyChain = new ArrayList<ApplicationInstance>();
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (DependencyChainUtils.getDependencyChain(app, new LinkedList<ApplicationInstance>()).contains(toRemove)) {
					withRemovedAppInDependencyChain.add(app);
				}
			}

			depManager.removeApplicationInTheSystem(toRemove);

			for (ApplicationInstance app : withRemovedAppInDependencyChain) {
				Assert.assertNotEquals(ApplicationInstanceLifeCycleState.ACTIVE, app.getState());
			}
		}
	}

	@Test
	public void iApplicationActivateCalledDuringAppActivation() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		// TODO listen to activation events

		for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
			if (app.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE)) {
				if (app instanceof IApp) {
					Assert.assertTrue(((IApp) app.getInstance()).activateCalled());
					Assert.assertTrue(((IApp) app.getInstance()).isActive());
				}
			}
		}
	}

	@Test
	public void iApplicationDectivateCalledDuringAppDeactivation() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		// TODO listen to deactivation events

		for (ApplicationInstance toRemove : depManager.getApplicationInstancesInSystem()) {
			List<ApplicationInstance> wasActive = new ArrayList<ApplicationInstance>();
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (app.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE)) {
					wasActive.add(app);
				}
			}

			depManager.removeApplicationInTheSystem(toRemove);

			for (ApplicationInstance app : wasActive) {
				if (!app.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE)) {
					if (app instanceof IApp) {
						Assert.assertTrue(((IApp) toRemove.getInstance()).deactivateCalled());
						Assert.assertFalse(((IApp) toRemove.getInstance()).isActive());
					}
				}
			}
		}
	}

	@Test
	public void errorInIApplicationActivatePreventsActivation() {

		ApplicationInstance app = createAppFailingOnActivate();
		depManager.addApplicationInTheSystem(app);

		if (app instanceof IApp) {
			Assert.assertTrue(((IApp) app.getInstance()).activateCalled());
			Assert.assertFalse(((IApp) app.getInstance()).isActive());
		}
		Assert.assertNotEquals(ApplicationInstanceLifeCycleState.ACTIVE, app.getState());
	}

	@Test
	public void scenarioIsAllActiveWhenCompletelyResolved() {

		Set<Class<? extends IApplication>> implemented = new HashSet<Class<? extends IApplication>>();
		Set<Class<? extends IApplication>> required = new HashSet<Class<? extends IApplication>>();
		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			implemented.addAll(toAdd.getApplications());
			required.addAll(toAdd.getPendingClasses());
			depManager.addApplicationInTheSystem(toAdd);
		}

		if (implemented.containsAll(required)) {
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				Assert.assertEquals(ApplicationInstanceLifeCycleState.ACTIVE, app.getState());
			}
		}
	}

	@Ignore
	@Test
	public void dependencyCycleIsAllDeactivatedWhenAppIsRemoved() {

		// TODO how to test this???

	}

	@Test
	public void resolveWithExistingImplementationsWhenDependencyIsRemoved() {

		for (IApp iapp : apps) {
			ApplicationInstance toAdd = createApp(iapp);
			depManager.addApplicationInTheSystem(toAdd);
		}

		// get interfaces with multiple implementations
		Set<Class<? extends IApplication>> withMultipleImpl = new HashSet<Class<? extends IApplication>>();
		Set<Class<? extends IApplication>> implemented = new HashSet<Class<? extends IApplication>>();
		for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
			for (Class<? extends IApplication> iapp : app.getApplications()) {
				if (implemented.contains(iapp)) {
					withMultipleImpl.add(iapp);
				} else {
					implemented.add(iapp);
				}
			}
		}

		// get used implementations of that applications
		// and get applications depending on them
		Set<ApplicationInstance> usingDependencyWithMultipleImpl = new HashSet<ApplicationInstance>();
		Map<Class<? extends IApplication>, List<ApplicationInstance>> usedImpl = new HashMap<Class<? extends IApplication>, List<ApplicationInstance>>();
		for (Class<? extends IApplication> clazz : withMultipleImpl) {
			for (ApplicationInstance app : depManager.getApplicationInstancesInSystem()) {
				if (app.getResolvedClasses().contains(clazz)) {
					usingDependencyWithMultipleImpl.add(app);
					for (ApplicationInstance inUse : app.getInjectedDependencies()) {
						if (inUse.getApplications().contains(clazz)) {
							if (!usedImpl.containsKey(clazz)) {
								usedImpl.put(clazz, Arrays.asList(inUse));
							} else {
								usedImpl.get(clazz).add(inUse);
							}
						}
					}
				}
			}
		}

		// remove an implementation being used
		for (Class<? extends IApplication> clazz : usedImpl.keySet()) {
			depManager.removeApplicationInTheSystem(usedImpl.get(clazz).get(0));
		}

		// check apps that were depending on removed are still resolved (with other implementations)
		for (ApplicationInstance app : usingDependencyWithMultipleImpl) {
			Assert.assertTrue(app.getPendingClasses().isEmpty());
				Assert.assertTrue(app.isResolved());
				Assert.assertTrue("State of app " + app + " should be RESOLVED or more",
						app.getState().equals(ApplicationInstanceLifeCycleState.RESOLVED)
								|| app.getState().equals(ApplicationInstanceLifeCycleState.ACTIVE));
		}

	}

	private ApplicationInstance createApp(IApplication app) {
		ApplicationInstance ai = new ApplicationInstance(app.getClass(), app);
		return ai;
	}

	private ApplicationInstance createAppFailingOnActivate() {
		return new ApplicationInstance(MyFailingApp.class, new MyFailingApp());
	}

	private class MyFailingApp implements IApp {

		private int		activateCount	= 0;
		private int		deactivateCount	= 0;
		private boolean	active			= false;

		@Override
		public void activate() {
			activateCount++;
			throw new UnsupportedOperationException();
		}

		@Override
		public void deactivate() {
			deactivateCount++;
			throw new UnsupportedOperationException();

		}

		public boolean activateCalled() {
			return activateCount > 0;
		}

		public boolean deactivateCalled() {
			return deactivateCount > 0;
		}

		@Override
		public boolean isActive() {
			return active;
		}
	}

}
