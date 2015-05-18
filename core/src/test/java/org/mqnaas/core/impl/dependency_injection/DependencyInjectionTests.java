package org.mqnaas.core.impl.dependency_injection;

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

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreProvider;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.test.helpers.capability.sample.ISampleCapability;
import org.mqnaas.test.helpers.capability.sample.ResourceA;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * 
 * @author Julio Carlos Barrera (i2CAT Foundation)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class DependencyInjectionTests {

	// private static final Logger log = LoggerFactory.getLogger(DependencyInjectionTests.class);

	@Inject
	IRootResourceAdministration	rootResourceMgmt;

	@Inject
	IServiceProvider			serviceProvider;

	@Inject
	IRootResourceProvider		rootResourceProv;

	@Inject
	ICoreProvider				coreProvider;

	IRootResource				resourceAInstance1;
	IRootResource				resourceAInstance2;
	IRootResource				resourceAInstance3;

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven.
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.3
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.3"))
						.karafVersion("3.0.3").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/paxexam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				// keep runtime folder allowing analysing results
				KarafDistributionOption.keepRuntimeFolder(),
				// use custom logging configuration file with a custom appender
				KarafDistributionOption.replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", new File(
						"src/test/resources/org.ops4j.pax.logging.cfg")),
				// maintain our log configuration
				KarafDistributionOption.doNotModifyLogConfiguration(),
				// add network feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas").artifactId("mqnaas").classifier("features").type("xml")
						.version("0.0.1-SNAPSHOT"), "mqnaas"),
				// install mqnaas-test-helpers bundle
				CoreOptions.mavenBundle().groupId("org.mqnaas").artifactId("mqnaas-test-helpers").version("0.0.1-SNAPSHOT"),
				// debug option
				KarafDistributionOption.debugConfiguration()
		};
	}

	@Before
	public void createResources() throws InstantiationException, IllegalAccessException, InterruptedException {
		resourceAInstance1 = createRootResource(ResourceA.RESOURCE_A_TYPE, ResourceA.RESOURCE_A_MODEL);
		resourceAInstance2 = createRootResource(ResourceA.RESOURCE_A_TYPE, ResourceA.RESOURCE_A_MODEL);
		resourceAInstance3 = createRootResource(ResourceA.RESOURCE_A_TYPE, ResourceA.RESOURCE_A_MODEL);
	}

	@After
	public void removeResources() throws ResourceNotFoundException {
		rootResourceMgmt.removeRootResource(resourceAInstance1);
		rootResourceMgmt.removeRootResource(resourceAInstance2);
		rootResourceMgmt.removeRootResource(resourceAInstance3);
	}

	@Ignore
	@Test
	public void rootResourceProiderInjectionTest() throws IllegalArgumentException, IllegalAccessException, CapabilityNotFoundException {
		// get ISampleCapability instances of each resourceA instance
		ISampleCapability resourceA1SampleCapabilityInstance = serviceProvider.getCapabilityInstance(resourceAInstance1, ISampleCapability.class);
		ISampleCapability resourceA2SampleCapabilityInstance = serviceProvider.getCapabilityInstance(resourceAInstance2, ISampleCapability.class);
		ISampleCapability resourceA3SampleCapabilityInstance = serviceProvider.getCapabilityInstance(resourceAInstance3, ISampleCapability.class);

		// get IRootResourceProvider instances of each resourceA instance
		IRootResourceProvider resourceA1RootResourceProviderInstance = serviceProvider.getCapabilityInstance(resourceAInstance1,
				IRootResourceProvider.class);
		IRootResourceProvider resourceA2RootResourceProviderInstance = serviceProvider.getCapabilityInstance(resourceAInstance2,
				IRootResourceProvider.class);
		IRootResourceProvider resourceA3RootResourceProviderInstance = serviceProvider.getCapabilityInstance(resourceAInstance3,
				IRootResourceProvider.class);

		// get injected IRootResourceProvider instances of each IRootResourceProvider instance
		IRootResourceProvider resourceA1RootResourceProviderInjectedInstance = getPrivateField(resourceA1SampleCapabilityInstance,
				IRootResourceProvider.class);
		IRootResourceProvider resourceA2RootResourceProviderInjectedInstance = getPrivateField(resourceA2SampleCapabilityInstance,
				IRootResourceProvider.class);
		IRootResourceProvider resourceA3RootResourceProviderInjectedInstance = getPrivateField(resourceA3SampleCapabilityInstance,
				IRootResourceProvider.class);

		// assert instance equality
		Assert.assertEquals("Instances should be equals", resourceA1RootResourceProviderInstance, resourceA1RootResourceProviderInjectedInstance);
		Assert.assertEquals("Instances should be equals", resourceA2RootResourceProviderInstance, resourceA2RootResourceProviderInjectedInstance);
		Assert.assertEquals("Instances should be equals", resourceA3RootResourceProviderInstance, resourceA3RootResourceProviderInjectedInstance);
	}

	private IRootResource createRootResource(Specification.Type resourceType, String resourceModel) throws InstantiationException,
			IllegalAccessException {
		Specification spec = new Specification(resourceType, resourceModel);
		Endpoint endpoint = null;

		return rootResourceMgmt.createRootResource(RootResourceDescriptor.create(spec, Arrays.asList(endpoint)));
	}

	private static <C> C getPrivateField(Object target, Class<C> fieldClass) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields;
		Class<?> clazz = target.getClass();
		fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (!field.getType().isAssignableFrom(fieldClass)) {
				continue;
			}

			field.setAccessible(true);

			// safe cast, it was checked previously
			@SuppressWarnings("unchecked")
			C fieldValue = (C) field.get(target);

			field.setAccessible(false);
			return fieldValue;
		}

		return null;
	}

}
