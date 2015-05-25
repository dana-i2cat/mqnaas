package org.mqnaas.core.impl;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.user.IUserManagement;
import org.mqnaas.core.api.user.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Basic implementation of the {@link IUserManagement} capability.
 * </p>
 * <p>
 * This capability binds only to Core resource. During activation process, it reads the list of users from a configuration file located in the karaf
 * configuration folder.
 * </p>
 * 
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public class UserManagement implements IUserManagement {

	private static final Logger	LOG					= LoggerFactory.getLogger(BindingManagement.class);

	private static final String	USERS_CONFIG_FILE	= "etc/org.mqnaas.users";

	Set<String>					users;

	@Resource
	IResource					resource;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType().equals(Specification.Type.CORE);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		LOG.info("Initializing UserManagement capability for resource " + resource.getId());
		try {
			readUsersFromConfigFile();
		} catch (IOException e) {
			LOG.warn("Could not read users from configuration file. File does not exists.");
		}
		LOG.info("Initialized UserManagement capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		users.clear();
	}

	@Override
	public Users getUsers() {
		return new Users(users);
	}

	private void readUsersFromConfigFile() throws IOException {

		users = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(USERS_CONFIG_FILE));

		String user;
		while ((user = br.readLine()) != null)
			if (!StringUtils.isEmpty(user))
				users.add(user);

	}
}
