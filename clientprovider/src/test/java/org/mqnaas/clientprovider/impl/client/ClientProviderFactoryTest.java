package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.impl.ICoreModelCapability;

/**
 * Unit tests for {@link ClientProviderFactory}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ClientProviderFactoryTest {

	@Test
	public void testClientProviderFactory() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		// generate base objects
		IRootResource resource = generateIRootResource();
		TestBundleGuard bg = new TestBundleGuard();
		ClientProviderFactory cpf = new ClientProviderFactory();
		injectBundleGuard(cpf, bg);
		injectCoreModelCapability(cpf, new TestCoreModelCapability(resource));
		cpf.activate();

		// add two InternalClientProvider's
		bg.throwClassEntered(getInternalClassListener(cpf), TestInternalClientProvider.class);

		// obtain a client from client provider
		TestClient client = cpf.getClientProvider(TestClientProvider.class).getClient(resource);
		Assert.assertTrue("Client must be an instance of TestClient.", client instanceof TestClient);
	}

	/*
	 * Test IBundleGuard able to be called in order to generate fake events
	 */
	private class TestBundleGuard implements IBundleGuard {

		@Override
		public void activate() {
			// nothing to do
		}

		@Override
		public void deactivate() {
			// nothing to do
		}

		@Override
		public void registerClassListener(IClassFilter classFilter, IClassListener classListener) {
			// nothing to do
		}

		@Override
		public void unregisterClassListener(IClassListener classListener) {
			// nothing to do
		}

		public void throwClassEntered(IClassListener classListener, Class<?> clazz) {
			classListener.classEntered(clazz);
		}

		public void throwClassLeft(IClassListener classListener, Class<?> clazz) {
			classListener.classLeft(clazz);
		}

	}

	/*
	 * Inject IBundleGuard using reflection
	 */
	private static void injectBundleGuard(ClientProviderFactory cpf, IBundleGuard bundleGuard) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Class<ClientProviderFactory> cpfClass = ClientProviderFactory.class;
		Field bundleGuardField = cpfClass.getDeclaredField("bundleGuard");
		bundleGuardField.setAccessible(true);
		bundleGuardField.set(cpf, bundleGuard);
	}

	/*
	 * Inject IBundleGuard using reflection
	 */
	private static void injectCoreModelCapability(ClientProviderFactory cpf, ICoreModelCapability coreModelCapability) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<ClientProviderFactory> cpfClass = ClientProviderFactory.class;
		Field bundleGuardField = cpfClass.getDeclaredField("coreModelCapability");
		bundleGuardField.setAccessible(true);
		bundleGuardField.set(cpf, coreModelCapability);
	}

	/*
	 * Obtain internalClassListener field using reflection
	 */
	private static IClassListener getInternalClassListener(ClientProviderFactory cpf) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Class<ClientProviderFactory> cpfClass = ClientProviderFactory.class;
		Field internalClassListenerField = cpfClass.getDeclaredField("internalClassListener");
		internalClassListenerField.setAccessible(true);
		return (IClassListener) internalClassListenerField.get(cpf);
	}

	/*
	 * Test IInternalClientProvider
	 */
	public static class TestInternalClientProvider implements IInternalClientProvider<TestClient, TesClientConfiguration> {

		@Override
		public TestClient getClient(Endpoint ep, Credentials c) {
			return new TestClient();
		}

		@Override
		public TestClient getClient(Endpoint ep, Credentials c, TesClientConfiguration configuration) {
			return new TestClient();
		}
	}

	private interface TestClientProvider extends IClientProvider<TestClient, TesClientConfiguration> {

		@Override
		public TestClient getClient(IResource resource);

		@Override
		public TestClient getClient(IResource resource, TesClientConfiguration clientConfiguration);

	}

	private static class TestClient {
	}

	private class TesClientConfiguration {
	}

	private static class TestCoreModelCapability implements ICoreModelCapability {

		private IRootResource	resourceToBeReturned;

		public TestCoreModelCapability(IRootResource resourceToBeReturned) {
			this.resourceToBeReturned = resourceToBeReturned;
		}

		@Override
		public void activate() {
		}

		@Override
		public void deactivate() {
		}

		@Override
		public IRootResource getRootResource(IResource resource) throws IllegalArgumentException {
			return resourceToBeReturned;
		}

	}

	private static IRootResource generateIRootResource() {
		return new IRootResource() {

			@Override
			public ITransactionBehavior getTransactionBehaviour() {
				return null;
			}

			@Override
			public Specification getSpecification() {
				return null;
			}

			@Override
			public ILockingBehaviour getLockingBehaviour() {
				return null;
			}

			@Override
			public Collection<Endpoint> getEndpoints() {
				return Arrays.asList(new Endpoint());
			}
		};
	}

}
