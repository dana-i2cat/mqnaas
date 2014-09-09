package org.mqnaas.core.impl.utils;

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
