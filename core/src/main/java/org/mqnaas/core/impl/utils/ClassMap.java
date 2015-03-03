package org.mqnaas.core.impl.utils;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Class encapsulating a {@link Map} of classes and instances.
 * </p>
 * <p>
 * The key of the map is a class specification, and the value is a class instance of the key's type. Therefore, only an instance of each map should be
 * stored in the map. For example, if you want to put the {@link String} "abcd", you should use the {@link ClassMap#put(Class, Object)} method like
 * this:
 * <ul>
 * <li><code>ClassMap.put(String.class, "abcd")</code></li>
 * </ul>
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ClassMap {

	protected Map<Class<?>, Object>	map;

	public ClassMap() {
		map = new HashMap<Class<?>, Object>();
	}

	public <T> Object get(Class<T> c) {
		return map.get(c);
	}

	public <T> void put(Class<T> c, T t) {
		map.put(c, t);
	}

	public Map<Class<?>, Object> getAll() {
		return map;
	}

	public Set<Class<?>> keySet() {
		return map.keySet();
	}

	public <T> void remove(Class<T> c) {
		map.remove(c);
	}

}
