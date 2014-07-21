package org.mqnaas.client.cxf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an example implementation of how to implement a specific api provider
 * 
 */
public class InternalCXFClientProvider implements IInternalAPIProvider<CXFConfiguration> {

	private static final Logger	log	= LoggerFactory.getLogger(InternalCXFClientProvider.class);

	@Override
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) {
		return getClient(apiClass, ep, c, null, null);
	}

	@Override
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CXFConfiguration configuration) {
		return getClient(apiClass, ep, c, configuration, null);
	}

	@Override
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CXFConfiguration configuration,
			Object applicationSpecificConfiguration) {

		if (configuration != null) {
			if (configuration.getUseDummyClient())
				return createDummyClient(apiClass);
		}

		if (ep == null || ep.getUri() == null) {
			// FIXME fail gracefully
			log.warn("Attempt to create JAX-RS client without target address. Using dummyClient instead");
			return createDummyClient(apiClass);
		}

		// String switchId = (String) sessionContext.getSessionParameters().get(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
		// TODO use switch id to instantiate the client

		// create CXF client
		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(apiClass.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(ep.getUri().toString());

		if (configuration != null && !configuration.getProviders().isEmpty())
			bean.setProviders(configuration.getProviders());

		bean.setResourceClass(apiClass);
		bean.setClassLoader(classLoader);

		API api = bean.create(apiClass);

		if (configuration.isCNChecked()) {
			TLSClientParameters clientParams = new TLSClientParameters();
			clientParams.setDisableCNCheck(true);
			WebClient.getConfig(api).getHttpConduit().setTlsClientParameters(clientParams);
		}

		return api;
	}

	private <API> API createDummyClient(Class<API> apiClass) {

		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(apiClass.getClassLoader());

		return (API) Proxy.newProxyInstance(classLoader, new Class[] { apiClass }, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				log.debug("Invoking cxf api method " + method + " with args " + args);
				return null;
			}
		});
	}

}
