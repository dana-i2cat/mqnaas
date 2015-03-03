package org.mqnaas.bundletree;

/*
 * #%L
 * MQNaaS :: BundleTree
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
 * Class Listener interface allowing to be registered in a {@link IBundleGuard} instance. <br/>
 * It contains two methods called back when:
 * <ul>
 * <li>a {@link Class} enters system classpath (method {@link #classEntered(Class)})</li>
 * <li>a {@link Class} leaves system classpath (method {@link #classLeft(Class))</li>
 * </ul>
 * 
 * @author Julio Carlos Barrera
 * 
 */
public interface IClassListener {

	/**
	 * Callback method invoked when a {@link Class} enters system classpath
	 * 
	 * @param clazz
	 *            Class entering system classpath
	 */
	public void classEntered(Class<?> clazz);

	/**
	 * Callback method invoked when a {@link Class} leaves system classpath
	 * 
	 * @param clazz
	 *            Class leaving system classpath
	 */
	public void classLeft(Class<?> clazz);

}
