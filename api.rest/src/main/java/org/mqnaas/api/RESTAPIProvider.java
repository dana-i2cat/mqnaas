package org.mqnaas.api;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.api.mapping.APIMapper;
import org.mqnaas.api.writers.InterfaceWriter;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the REST API provision.
 * 
 * Supports the publication of specific capability interfaces implemented by a capability implementation at a given URI.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public class RESTAPIProvider implements IRESTAPIProvider {

	private static final Logger												log				= LoggerFactory.getLogger(RESTAPIProvider.class);

	private RESTAPIGenerator												apiGenerator	= new RESTAPIGenerator();

	private Map<Pair<ICapability, Class<? extends ICapability>>, Server>	servers			= new HashMap<Pair<ICapability, Class<? extends ICapability>>, Server>();

	public void publish(ICapability capability, Class<? extends ICapability> interfaceToBePublished, String uri)
			throws InvalidCapabilityDefinionException {

		// 2. Add the classloader of the interface to be published to the proxy classloader of the bean
		ServerFactory factoryBean = new ServerFactory(interfaceToBePublished, uri);

		// 3. Create the API interface reflecting the interface to be published at the given uri
		InterfaceWriter interfaceWriter = new InterfaceWriter(interfaceToBePublished, uri);

		Class<?> apiInterface = interfaceWriter.toClass(factoryBean.getClassLoader());

		// 4. Create a mapper mapping the both interfaces
		APIMapper mapper = apiGenerator.createAPIInterface(apiInterface, interfaceWriter, interfaceToBePublished, capability);

		// 5. Publish the API interface using the mapper as the invocation handler of the proxy
		factoryBean.setResourceClasses(apiInterface);

		Object proxy = Proxy.newProxyInstance(factoryBean.getClassLoader(), new Class<?>[] { apiInterface }, mapper);

		factoryBean.setResourceProvider(apiInterface, new SingletonResourceProvider(proxy));

		Server server = factoryBean.create();

		servers.put(new ImmutablePair<ICapability, Class<? extends ICapability>>(capability, interfaceToBePublished), server);

		// System.out.println("Published to " + uri);
		// System.out.println(interfaceWriter);

		log.debug("Published {} at {}", interfaceToBePublished, factoryBean.getAddress() + uri);
	}

	@Override
	public boolean unpublish(ICapability capability, Class<? extends ICapability> interfaceToUnPublish) {
		Pair<ICapability, Class<? extends ICapability>> key = new ImmutablePair<ICapability, Class<? extends ICapability>>(capability,
				interfaceToUnPublish);
		Server server = servers.get(key);

		boolean unpublished = false;

		if (server != null) {
			String addr = server.getEndpoint().getEndpointInfo().getAddress();
			server.destroy();
			servers.remove(key);

			unpublished = true;

			// System.out.println("Unpublished from " + addr);
			log.debug("Unpublished {} at {}", interfaceToUnPublish, addr);
		}

		return unpublished;
	}

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType() == Specification.Type.CORE;
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

}
