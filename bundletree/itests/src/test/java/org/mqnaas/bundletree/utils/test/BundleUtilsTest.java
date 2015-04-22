package org.mqnaas.bundletree.utils.test;

/*
 * #%L
 * MQNaaS :: BundleTree :: iTests
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

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.bundletree.exceptions.BundleNotFoundException;
import org.mqnaas.bundletree.utils.BundleUtils;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * {@link BundleUtils} tests
 * 
 * @author Julio Carlos Barrera
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BundleUtilsTest {

	@Inject
	BundleContext	bundleContext;

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven. Same applies for bundletree-itests-testbundleX version
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.3
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.3"))
						.karafVersion("3.0.3").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/exam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				// keep runtime folder allowing analysing results
				KarafDistributionOption.keepRuntimeFolder(),
				// use custom logging configuration file with a custom appender
				KarafDistributionOption.replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", new File(
						"src/test/resources/org.ops4j.pax.logging.cfg")),
				// add core feature (including bundletree)
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas").artifactId("mqnaas").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "mqnaas"),
				// install both test bundles
				CoreOptions.mavenBundle().groupId("org.mqnaas").artifactId("bundletree-itests-testbundleA").version("0.0.1-SNAPSHOT"),
				CoreOptions.mavenBundle().groupId("org.mqnaas").artifactId("bundletree-itests-testbundleB").version("0.0.1-SNAPSHOT"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	// bundle names
	static final String	BUNDLETREE_BUNDLE_NAME		= "bundletree";
	static final String	FELIX_CORE_BUNDLE_NAME		= "org.apache.felix.framework";
	static final String	TEST_BUNDLE_A_BUNDLE_NAME	= "bundletree-itests-testbundleA";
	static final String	TEST_BUNDLE_B_BUNDLE_NAME	= "bundletree-itests-testbundleB";
	static final String	MQNAAS_CORE_BUNDLE_NAME		= "core";

	@Test
	public void getBundleBySymbolicNameTest() throws Exception {
		// valid bundle
		try {
			Bundle bundle = BundleUtils.getBundleBySymbolicName(BUNDLETREE_BUNDLE_NAME);
			Assert.assertEquals("Obtained bundle must be same as requested", BUNDLETREE_BUNDLE_NAME, bundle.getSymbolicName());
		} catch (BundleNotFoundException e) {
			Assert.fail("BundleNotFoundException must not be thrown: " + e);
		}
	}

	@Test(expected = BundleNotFoundException.class)
	public void getBadBundleBySymbolicNameTest() throws Exception {
		// invalid bundle
		BundleUtils.getBundleBySymbolicName("not valid bundle name");
	}

	static final int			TEST_ITERATIONS	= 1000;

	static final NumberFormat	FORMATTER		= new DecimalFormat("#,###,###.000");

	@Test
	public void bundleDependecyAnalysisTest() throws Exception {
		// used bundles
		final Bundle rootBundle = BundleUtils.getBundleBySymbolicName(FELIX_CORE_BUNDLE_NAME);
		final Bundle testBundleA = BundleUtils.getBundleBySymbolicName(TEST_BUNDLE_A_BUNDLE_NAME);
		final Bundle testBundleB = BundleUtils.getBundleBySymbolicName(TEST_BUNDLE_B_BUNDLE_NAME);
		final Bundle coreBundle = BundleUtils.getBundleBySymbolicName(MQNAAS_CORE_BUNDLE_NAME);

		// mean vars
		long[] times = new long[2];

		double upTotalAverage = 0d;
		int upTotalCount = 0;

		double upPosAverage = 0d;
		int upPosCount = 0;

		double upNegAverage = 0d;
		int upNegCount = 0;

		double downTotalAverage = 0d;
		int downTotalCount = 0;

		double downPosAverage = 0d;
		int downPosCount = 0;

		double downNegAverage = 0d;
		int downNegCount = 0;

		// do multiple calls to method to extract means
		for (int i = 0; i < TEST_ITERATIONS; i++) {
			times = upAndDownDependency(testBundleA, rootBundle, true);
			upTotalAverage += (times[0] - upTotalAverage) / ++upTotalCount;
			downTotalAverage += (times[1] - downTotalAverage) / ++downTotalCount;

			upPosAverage += (times[0] - upPosAverage) / ++upPosCount;
			downPosAverage += (times[1] - downPosAverage) / ++downPosCount;

			times = upAndDownDependency(testBundleB, rootBundle, true);
			upTotalAverage += (times[0] - upTotalAverage) / ++upTotalCount;
			downTotalAverage += (times[1] - downTotalAverage) / ++downTotalCount;

			upPosAverage += (times[0] - upPosAverage) / ++upPosCount;
			downPosAverage += (times[1] - downPosAverage) / ++downPosCount;

			times = upAndDownDependency(testBundleB, coreBundle, false);
			upTotalAverage += (times[0] - upTotalAverage) / ++upTotalCount;
			downTotalAverage += (times[1] - downTotalAverage) / ++downTotalCount;

			upNegAverage += (times[0] - upNegAverage) / ++upNegCount;
			downNegAverage += (times[1] - downNegAverage) / ++downNegCount;

			times = upAndDownDependency(testBundleA, coreBundle, false);
			upTotalAverage += (times[0] - upTotalAverage) / ++upTotalCount;
			downTotalAverage += (times[1] - downTotalAverage) / ++downTotalCount;

			upNegAverage += (times[0] - upNegAverage) / ++upNegCount;
			downNegAverage += (times[1] - downNegAverage) / ++downNegCount;
		}

		// expose mean results
		System.out.println("[ UP ]    Total average = " + FORMATTER.format(upTotalAverage) + " ns (" + upTotalCount + ")");
		System.out.println("[DOWN]    Total average = " + FORMATTER.format(downTotalAverage) + " ns (" + downTotalCount + ")");

		System.out.println("[ UP ] Positive average = " + FORMATTER.format(upPosAverage) + " ns (" + upPosCount + ")");
		System.out.println("[DOWN] Positive average = " + FORMATTER.format(downPosAverage) + " ns (" + downPosCount + ")");

		System.out.println("[ UP ] Negative average = " + FORMATTER.format(upNegAverage) + " ns (" + upNegCount + ")");
		System.out.println("[DOWN] Negative average = " + FORMATTER.format(downNegAverage) + " ns (" + downNegCount + ")");
	}

	static long		time;
	static boolean	depends;

	private static long[] upAndDownDependency(Bundle targetBundle, Bundle rootBundle, boolean expectedResult) {
		long[] times = new long[2];
		time = System.nanoTime();
		depends = BundleUtils.bundleDependsOnBundle(targetBundle, rootBundle, BundleUtils.LOOK_UP_STRATEGY.UP);
		time = System.nanoTime() - time;
		Assert.assertTrue("Bundle " + targetBundle + (expectedResult ? " must" : " must not") + " depend on " + rootBundle, expectedResult == depends);
		// System.out.println("[ UP ] " + targetBundle + (res ? " does" : " doesn't") + " depend on " + rootBundle + ". Time = " + time + " ns.");
		times[0] = time;

		time = System.nanoTime();
		depends = BundleUtils.bundleDependsOnBundle(targetBundle, rootBundle, BundleUtils.LOOK_UP_STRATEGY.DOWN);
		time = System.nanoTime() - time;
		Assert.assertTrue("Bundle " + targetBundle + (expectedResult ? " must" : " must not") + " depend on " + rootBundle, expectedResult == depends);
		// System.out.println("[DOWN] " + targetBundle + (res ? " does" : " doesn't") + " depend on " + rootBundle + ". Time = " + time + " ns.");
		times[1] = time;

		return times;
	}

}
