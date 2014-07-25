package org.mqnaas.api.writers;

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
			name = clazz.getName();
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
			System.exit(1);
		}

		return clazz;
	}

}
