package org.mqnaas.api;

/*
 * #%L
 * MQNaaS :: REST API Provider
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.mqnaas.api.providers.GenericListSerializationProvider;
import org.mqnaas.api.providers.MultiMapSerializationProvider;
import org.mqnaas.core.api.ICapability;

public class ServerFactory extends JAXRSServerFactoryBean {

	private static List<Object>	providers;

	static {
		providers = new ArrayList<Object>();
		providers.add(new GenericListSerializationProvider());
		providers.add(new MultiMapSerializationProvider());
	}

	public ServerFactory(Class<? extends ICapability> interfaceToBePublished, String uri) {
		super();

		setAddress("http://localhost:9000" + uri);

		setProviders(providers);

		ProxyClassLoader proxyClassLoader = new ProxyClassLoader(ServerFactory.this.getClass().getClassLoader());

		proxyClassLoader.addLoader(interfaceToBePublished.getClassLoader());

		getBus().setExtension(proxyClassLoader, ClassLoader.class);
	}

	public ClassLoader getClassLoader() {
		return getBus().getExtension(ClassLoader.class);
	}

	/**
	 * Utility class loader that can be used to create proxies in cases where the the client classes are not visible to the loader of the service
	 * class.
	 */
	// TODO Check the security constraints of the cxf ProxyClassLoader and replace this class loader
	private static class ProxyClassLoader extends ClassLoader {

		private final Class<?>			classes[];
		private final Set<ClassLoader>	loaders	= new HashSet<ClassLoader>();
		private boolean					checkSystem;

		public ProxyClassLoader(ClassLoader parent) {
			super(parent);
			classes = null;
		}

		public void addLoader(ClassLoader loader) {
			if (loader == null) {
				checkSystem = true;
			} else {
				loaders.add(loader);
			}
		}

		public Class<?> findClass(String name) throws ClassNotFoundException {

			if (classes != null) {
				for (Class<?> c : classes) {
					if (name.equals(c.getName())) {
						return c;
					}
				}
			}
			for (ClassLoader loader : loaders) {
				try {
					return loader.loadClass(name);
				} catch (ClassNotFoundException cnfe) {
					// Try next
				} catch (NoClassDefFoundError cnfe) {
					// Try next
				}
			}
			if (checkSystem) {
				try {
					return getSystemClassLoader().loadClass(name);
				} catch (ClassNotFoundException cnfe) {
					// Try next
				} catch (NoClassDefFoundError cnfe) {
					// Try next
				}
			}

			throw new ClassNotFoundException(name);
		}

		public URL findResource(String name) {
			for (ClassLoader loader : loaders) {
				URL url = loader.getResource(name);
				if (url != null) {
					return url;
				}
			}
			return null;
		}
	}

}
