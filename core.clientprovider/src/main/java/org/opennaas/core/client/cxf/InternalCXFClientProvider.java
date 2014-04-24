package org.opennaas.core.client.cxf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.opennaas.core.clientprovider.api.apiclient.IInternalAPIProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

/**
 * This is an example implementation of how to implement a specific api provider
 *
 */
public class InternalCXFClientProvider implements IInternalAPIProvider<CXFConfiguration> {

	@Override
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) {
		return getClient(apiClass, ep, c, null, null);
	}

	@Override
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CXFConfiguration configuration) {
		return getClient(apiClass, ep, c, configuration, null);
	}

	@Override
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CXFConfiguration configuration, Object applicationSpecificConfiguration) {
		
//		String switchId = (String) sessionContext.getSessionParameters().get(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
		// TODO use switch id to instantiate the client

		// create CXF client
		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(apiClass.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		if ( configuration != null ) {
			bean.setAddress(configuration.getUri());
		}
//		bean.setProvider(new CustomJSONProvider()); // TODO initialize the rest from the configuration
		bean.setResourceClass(apiClass);
		bean.setClassLoader(classLoader);

		return //bean.create(apiClass);	
				(API) Proxy.newProxyInstance(classLoader, new Class[]{ apiClass }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				System.out.println("Invoking cxf api method " + method + " with args " + args);
				return null;
			}
		});
	}

}
