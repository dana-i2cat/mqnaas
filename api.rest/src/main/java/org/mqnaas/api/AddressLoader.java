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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Class defining the public address of the REST API.
 * </p>
 * <p>
 * The AddressLoader reads the address where the REST API should be published from a configuration file located in Karaf's file system. Therefore,
 * this class is linked to OSGI specific features, since it requires the {@link BundleContext} to get the {@link ConfigurationAdmin}, which is
 * responsible of reading those configuration files.
 * </p>
 * <p>
 * If configuration file does not exist, or the user defines invalid information, the default address is returned: <literal>
 * http://0.0.0.0:9000</literal>
 * </p>
 * 
 * @author Adrián Roselló Rey
 *
 */
public class AddressLoader {

	private static final Logger	log					= LoggerFactory.getLogger(AddressLoader.class);

	private static final String	ADDRESS_CONFIG_FILE	= "org.mqnaas.ws.configuration";
	private static final String	ADDRESS_PROPERTY	= "ws.address";
	private static final String	DEFAULT_ADDRESS		= "http://0.0.0.0:9000";

	/**
	 * Reads the address where the REST API should be published from <literal>org.mqnaas.ws.configuration.cfg</literal> configuration file. If no
	 * information or file is provided, it returns the default address.
	 * 
	 * @return Address configured in > in <literal>ws.address</literal> property of file <literal>org.mqnaas.ws.configuration.cfg</literal>, with no
	 *         final slash at the end. Default <literal>http://0.0.0.0:9000</literal> address if file does not exist, or a malformed URL was defined
	 *         in it.
	 */
	public static String getServerAddress() {

		ConfigurationAdmin configAdmin = getConfigurationAdmin();

		if (configAdmin == null) {
			log.warn("Could not get ConfigurationAdmin. Using defult address: " + DEFAULT_ADDRESS);
			return DEFAULT_ADDRESS;
		}
		try {
			Dictionary<String, Object> wsProperties = configAdmin.getConfiguration(ADDRESS_CONFIG_FILE).getProperties();
			String address = (String) wsProperties.get(ADDRESS_PROPERTY);

			if (address == null) {
				log.warn("No URI present in WS configuration file. Using defult address: " + DEFAULT_ADDRESS);
				return DEFAULT_ADDRESS;
			}

			try {
				new URI(address);
			} catch (URISyntaxException e) {
				log.warn("Malformed URI in WS configuration file. Using defult address: " + DEFAULT_ADDRESS, e);
				return DEFAULT_ADDRESS;
			}

			return StringUtils.removeEnd(address, "/");

		} catch (Exception e) {
			log.warn("Could not get WS address from configuration file. Using defult address: " + DEFAULT_ADDRESS, e);
			return DEFAULT_ADDRESS;
		}

	}

	/**
	 * Uses the {@link BundleContext} to retrieve the {@link ConfigurationAdmin} from the OSGI Service Registry.
	 * 
	 * @return {@link ConfigurationAdmin} service proxy retrieved from the OSGI Service Registry. <code>null</code> if the
	 *         <code>ConfigurationAdmin</code> could not be retrieved.
	 */
	private static ConfigurationAdmin getConfigurationAdmin() {
		BundleContext bundleContext = FrameworkUtil.getBundle(AddressLoader.class).getBundleContext();

		if (bundleContext == null)
			return null;

		ServiceReference<ConfigurationAdmin> configAdminReference = bundleContext.getServiceReference(ConfigurationAdmin.class);

		if (configAdminReference == null)
			return null;

		return bundleContext.getService(configAdminReference);
	}
}
