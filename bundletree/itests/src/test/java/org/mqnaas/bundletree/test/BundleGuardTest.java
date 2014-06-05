package org.mqnaas.bundletree.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.bundletree.impl.BundleGuard;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BundleGuardTest {

	@Inject
	BundleContext	bundleContext;

	@Configuration
	public Option[] config() {
		return new Option[] {
				// distribution to test: Karaf 3.0.1
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz"))
						.karafVersion("3.0.1").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/exam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				KarafDistributionOption.keepRuntimeFolder(),
				// add bundletree feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas").artifactId("bundletree").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "bundletree"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	// classes names
	static final String	ROOT_CLASS_NAME				= "org.mqnaas.bundletree.itests.testbundleA.RootClass";
	static final String	TEST_CLASS_A_NAME			= "org.mqnaas.bundletree.itests.testbundleB.TestClassA";

	// lock objects to synchronise threads
	static final long	TEST_TIMEOUT				= 30000;
	static final long	NOTIFICATION_TIMEOUT		= 10000;

	boolean				rootClassAvailable			= false;
	Object				rootClassNotificationLock	= new Object();
	boolean				testClassAvailable			= false;
	Object				testClassANotificationLock	= new Object();

	Object				bothClassesNotificationLock	= new Object();

	@Test(timeout = TEST_TIMEOUT)
	public void bundleGuardtest() throws Exception {

		// create bundle guard
		IBundleGuard bundleGuard = new BundleGuard();

		// register to listen for RootClass
		bundleGuard.registerClassListener(new TestClassFilter(ROOT_CLASS_NAME), new TestClassListener());

		Bundle testBundleA;
		synchronized (rootClassNotificationLock) {
			// install and start testBundleA
			System.out.println("Installing and starting testbundleA...");
			testBundleA = bundleContext.installBundle("mvn:org.mqnaas/bundletree-itests-testbundleA/0.0.1-SNAPSHOT");
			testBundleA.start();

			// wait for callback of class RootClass
			do {
				System.out.println("Waiting for notification of RootClass...");
				rootClassNotificationLock.wait(NOTIFICATION_TIMEOUT);
			} while (!rootClassAvailable);
			System.out.println("Notification of RootClass received.");
		}

		Bundle testBundleB;
		synchronized (testClassANotificationLock) {
			// install testBundleB
			System.out.println("Installing and starting testbundleB...");
			testBundleB = bundleContext.installBundle("mvn:org.mqnaas/bundletree-itests-testbundleB/0.0.1-SNAPSHOT");
			testBundleB.start();

			// wait for callback of class TestClassA
			do {
				System.out.println("Waiting for notification of TestClassA...");
				testClassANotificationLock.wait(NOTIFICATION_TIMEOUT);
			} while (!testClassAvailable);
			System.out.println("Notification of TestClassA received.");
		}

		synchronized (bothClassesNotificationLock) {
			// FIXME remove testBundleA (it does not change state on testbundleB and its a dependency?!?!? on Karaf, WTF?!?!?)
			System.out.println("Uninstalling testBundleA and testbundleB...");
			testBundleA.uninstall();
			testBundleB.uninstall();

			// wait for callback of both classes
			do {
				System.out.println("Waiting for notification of both classes... [" + rootClassAvailable + ", " + testClassAvailable + "]");
				bothClassesNotificationLock.wait(NOTIFICATION_TIMEOUT);
			} while (rootClassAvailable || testClassAvailable);
			System.out.println("Notification of both classes received.");
		}
	}

	// test class filter returning true when Class clazz is classname or a superclass of it
	class TestClassFilter implements IClassFilter {

		String	className;

		public TestClassFilter(String className) {
			this.className = className;
		}

		@Override
		public boolean filter(Class<?> clazz) {
			return getAllSuperClassesNames(clazz).contains(className);
		}

		Set<String> getAllSuperClassesNames(Class<?> clazz) {
			Set<String> superClassesNames = new HashSet<String>();
			while (clazz != null) {
				superClassesNames.add(clazz.getCanonicalName());
				clazz = clazz.getSuperclass();
			}
			return superClassesNames;
		}

	}

	// test ClassListener receiving callbacks
	class TestClassListener implements IClassListener {

		@Override
		public void classEntered(Class<?> clazz) {
			System.out.println("Received classEntered notification for class: " + clazz.getCanonicalName());

			// if event refers to RootClass, notify his lock
			if (clazz.getCanonicalName().equals(ROOT_CLASS_NAME)) {
				synchronized (rootClassNotificationLock) {
					System.out.println("Notifying RootClass locked thread...");
					rootClassAvailable = true;
					rootClassNotificationLock.notify();
				}
			}
			// if event refers to TestClassA, notify his lock
			else if (clazz.getCanonicalName().equals(TEST_CLASS_A_NAME)) {
				synchronized (testClassANotificationLock) {
					System.out.println("Notifying TestClassA locked thread...");
					testClassAvailable = true;
					testClassANotificationLock.notify();
				}
			}
		}

		@Override
		public void classLeft(Class<?> clazz) {
			System.out.println("Received classLeft notification for class: " + clazz.getCanonicalName());
			// if event refers to RootClass, notify it
			if (clazz.getCanonicalName().equals(ROOT_CLASS_NAME)) {
				synchronized (bothClassesNotificationLock) {
					System.out.println("Notifying both classes locked thread...");
					rootClassAvailable = false;
					bothClassesNotificationLock.notify();
				}
			}
			// if event refers to TestClassA, notify it
			else if (clazz.getCanonicalName().equals(TEST_CLASS_A_NAME)) {
				synchronized (bothClassesNotificationLock) {
					System.out.println("Notifying both classes locked threads...");
					testClassAvailable = false;
					bothClassesNotificationLock.notify();
				}
			}
		}

	}

}
