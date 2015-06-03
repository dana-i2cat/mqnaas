package org.mqnaas.core.api.user;

import org.mqnaas.core.api.ICapability;

/*
 * #%L
 * MQNaaS :: Core.API
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

/**
 * <p>
 * Capability managing the list of users of the MQNaaS framework.
 * </p>
 * <p>
 * First draft of the capability defines users as {@link String}s representing the username.
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public interface IUserManagement extends ICapability {

	/**
	 * Return all the usernames of the framework users.
	 * 
	 * @return {@link User} wrapper class, containing the list of all usernames of the MQNaaS framework.
	 */
	Users getUsers();

}
