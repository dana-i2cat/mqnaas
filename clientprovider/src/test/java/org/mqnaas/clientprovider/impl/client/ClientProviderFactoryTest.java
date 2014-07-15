package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

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
		TestBundleGuard bg = new TestBundleGuard();
		ClientProviderFactory cpf = new ClientProviderFactory();
		injectBundleGuard(cpf, bg);
		cpf.onDependenciesResolved();

		// add two InternalClientProvider's
		bg.throwClassEntered(getInternalClassListener(cpf), TestInternalClientProvider.class);

		//
		TestClient client = cpf.getClientProvider(TestClientProvider.class).getClient();
		Assert.assertTrue("", client instanceof TestClient);
	}

	/*
	 * Test IBundleGuard able to be called in order to generate fake events
	 */
	private class TestBundleGuard implements IBundleGuard {

		@Override
		public void onDependenciesResolved() {
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

		public TestClient getClient();

		public TestClient getClient(TesClientConfiguration clientConfiguration);

	}

	private static class TestClient {
	}

	private class TesClientConfiguration {
	}
}
