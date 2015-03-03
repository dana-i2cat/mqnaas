package org.mqnaas.api.writers;

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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

class AbstractWriter implements Opcodes {

	private static Map<Class<?>, String>	PRIMITIVE_CLASS_NAMES	= new HashMap<Class<?>, String>();

	static {
		PRIMITIVE_CLASS_NAMES.put(char.class, "C");
		PRIMITIVE_CLASS_NAMES.put(double.class, "D");
		PRIMITIVE_CLASS_NAMES.put(float.class, "F");
		PRIMITIVE_CLASS_NAMES.put(int.class, "I");
		PRIMITIVE_CLASS_NAMES.put(long.class, "J");
		PRIMITIVE_CLASS_NAMES.put(short.class, "S");
		PRIMITIVE_CLASS_NAMES.put(boolean.class, "Z");
		PRIMITIVE_CLASS_NAMES.put(void.class, "V");
	}

	protected static String toBytecodeName(Class<?> clazz) {

		boolean isArray = clazz.isArray();

		String name;

		if (isArray) {
			name = "[" + toBytecodeName(clazz.getComponentType());
		} else {
			if (clazz.isPrimitive()) {
				name = PRIMITIVE_CLASS_NAMES.get(clazz);
			} else {
				name = "L" + clazz.getName().replace(".", "/") + ";";
			}
		}

		return name;
	}

	protected static Class<?> loadClass(ClassLoader loader, String className, byte[] b) {
		// override classDefine (as it is protected) and define the class.
		Class<?> clazz = null;
		try {
			// ClassLoader loader = AbstractWriter.class.getClassLoader(); // ClassLoader.getSystemClassLoader();
			Class<?> cls = Class.forName("java.lang.ClassLoader");
			Method method =
					cls.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });

			// protected method invocation
			method.setAccessible(true);
			try {
				Object[] args = new Object[] { className, b, new Integer(0), new Integer(b.length) };
				clazz = (Class<?>) method.invoke(loader, args);
			} finally {
				method.setAccessible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return clazz;
	}

}
