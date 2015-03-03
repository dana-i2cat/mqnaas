package org.mqnaas.core.api;

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
 * <code>ILockingBehaviour</code> defines the locking behavior of a {@link IRootResource} in MQNaaS.
 * </p>
 * <p>
 * A resource may
 * <ol>
 * <li>utilize an integrated locking mechanism,</li>
 * <li>the MQNaaS internal locking mechanism (which is the default), or</li>
 * <li>can implement it's own behavior by implementing this interface.</li>
 * </ol>
 */
public interface ILockingBehaviour {

	boolean lock(ExecutionContext executionContext, IRootResource resource);

	boolean isLocked(ExecutionContext executionContext, IRootResource resource);

	boolean unlock(ExecutionContext executionContext, IRootResource resource);

}
