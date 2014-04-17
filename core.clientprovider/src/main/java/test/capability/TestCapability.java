package test.capability;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opennaas.core.client.Dependency;
import org.opennaas.core.client.application.ApplicationConfiguration;
import org.opennaas.core.client.application.IApplicationClient;
import org.opennaas.core.client.cxf.CXFConfiguration;
import org.opennaas.core.client.cxf.ICXFAPIProvider;
import org.opennaas.core.client.cxf.InternalCXFClientProvider;
import org.opennaas.core.client.netconf.INetconfClientProvider;
import org.opennaas.core.client.netconf.InternalNetconfClientProvider;
import org.opennaas.core.client.netconf.NetconfClient;
import org.opennaas.core.client.netconf.NetconfConfiguration;
import org.opennaas.core.clientprovider.IAPIProvider;
import org.opennaas.core.clientprovider.IAPIProviderFactory;
import org.opennaas.core.clientprovider.IClientProvider;
import org.opennaas.core.clientprovider.IClientProviderFactory;
import org.opennaas.core.clientprovider.IInternalAPIProvider;
import org.opennaas.core.clientprovider.IInternalClientProvider;

public class TestCapability {

	@Dependency
	IClientProviderFactory clientProviderFactory;

	@Dependency
	IAPIProviderFactory apiProviderFactory;

	public TestCapability() {

		// 0. This initialization step is done by the framework
		initFields();

		// 1. Static client provisioning
		INetconfClientProvider cp = clientProviderFactory
				.getClientProvider(INetconfClientProvider.class);

		// Client w/o configuration
		NetconfClient netconfClient1 = cp.getClient();
		netconfClient1.doNetconfSpecificThing1();

		// Client with (client specific) configuration
		NetconfConfiguration netconfConfiguration = new NetconfConfiguration();

		NetconfClient netconfClient2 = cp.getClient(netconfConfiguration);
		netconfClient2.doNetconfSpecificThing2();

		// 2. Dynamic client provisioning
		ICXFAPIProvider ap = apiProviderFactory
				.getAPIProvider(ICXFAPIProvider.class);

		// Dynamic client w/o configuration
		IApplicationClient applicationSpecificClient1 = ap
				.getClient(IApplicationClient.class);
		applicationSpecificClient1.methodA();
		applicationSpecificClient1.methodB();

		// Dynamic client with (client specific) configuration
		IApplicationClient applicationSpecificClient2 = ap
				.getClient(IApplicationClient.class,
						new CXFConfiguration().uri("U R I 1"));
		applicationSpecificClient2.methodA();
		applicationSpecificClient2.methodB();

		// Dynamic client with client specific configuration and application
		// specific configuration
		IApplicationClient applicationSpecificClient3 = ap.getClient(
				IApplicationClient.class,
				new CXFConfiguration().uri("U R I 2"),
				new ApplicationConfiguration());
		applicationSpecificClient3.methodA();
		applicationSpecificClient3.methodB();

	}

	private void initFields() {

		// List of available IInternalClientProvider must be maintained by
		// classpath scanning
		final List<IInternalClientProvider<?, ?>> internalClientProviders = new ArrayList<IInternalClientProvider<?, ?>>();
		internalClientProviders.add(new InternalNetconfClientProvider());

		// List of available IInternalAPIProvider must be maintained by
		// classpath scanning
		final List<IInternalAPIProvider<?>> internalAPIProviders = new ArrayList<IInternalAPIProvider<?>>();
		internalAPIProviders.add(new InternalCXFClientProvider());

		// In the final implementation a classpath scan will deliver the
		// implementations for IClientProviderFactory and IAPIProviderFactory
		clientProviderFactory = new IClientProviderFactory() {

			@SuppressWarnings("unchecked")
			@Override
			public <T, CC, C extends IClientProvider<T, CC>> C getClientProvider(
					Class<C> clientProviderClass) {
				// Match against list of providers...
				for (IInternalClientProvider<?, ?> internalClientProvider : internalClientProviders) {
					Class<?> internalClientProviderClass = internalClientProvider
							.getClass();

					if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS,
							clientProviderClass, internalClientProviderClass, 2)) {
						
						C c = (C) Proxy.newProxyInstance(clientProviderClass.getClassLoader(),
								new Class[] { clientProviderClass },
								new ClientProviderAdapter(internalClientProvider));

						return c;
					}
				}

				return null;
			}
		};

		apiProviderFactory = new IAPIProviderFactory() {

			@Override
			public <CC, C extends IAPIProvider<CC>> C getAPIProvider(
					Class<C> apiProviderClass) {
				// Match against list of providers...
				for (IInternalAPIProvider<?> internalApiProvider : internalAPIProviders) {
					Class<?> internalAPIProviderClass = internalApiProvider.getClass();

					if (doTypeArgumentsMatch(VALID_API_PROVIDERS,
							apiProviderClass, internalAPIProviderClass, 1)) {
						
						C c = (C) Proxy.newProxyInstance(internalAPIProviderClass.getClassLoader(),
								new Class[] { apiProviderClass },
								new APIProviderAdapter(internalApiProvider));
						
						return c;
					}
				}
				return null;
			}
		};

	}

	static Set<Type> VALID_CLIENT_PROVIDERS, VALID_API_PROVIDERS;

	static {
		VALID_CLIENT_PROVIDERS = new HashSet<Type>();
		VALID_CLIENT_PROVIDERS.add(IClientProvider.class);
		VALID_CLIENT_PROVIDERS.add(IInternalClientProvider.class);

		VALID_API_PROVIDERS = new HashSet<Type>();
		VALID_API_PROVIDERS.add(IAPIProvider.class);
		VALID_API_PROVIDERS.add(IInternalAPIProvider.class);
	}

	private static boolean doTypeArgumentsMatch(Set<Type> validTypes,
			Class<?> clazz1, Class<?> class2, int numArgs) {

		for (int i = 0; i < numArgs; i++) {
			if (!getTypeArgument(validTypes, i, clazz1).equals(
					getTypeArgument(validTypes, i, class2))) {
				return false;
			}
		}

		return true;
	}

	private static Type getTypeArgument(Set<Type> validTypes, int index,
			Class<?> clientProviderClass) {

		// Look for the specific generic interfaces...
		for (Type type : clientProviderClass.getGenericInterfaces()) {

			ParameterizedType parameterizedType = (ParameterizedType) type;

			if (validTypes.contains(parameterizedType.getRawType())) {
				return parameterizedType.getActualTypeArguments()[index];
			}
		}

		return null;
	}

	public static void main(String[] args) {
		new TestCapability();
	}

}
