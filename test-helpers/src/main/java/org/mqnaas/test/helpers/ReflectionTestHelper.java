package org.mqnaas.test.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection Test Helpers
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ReflectionTestHelper {

	/**
	 * Injects a private {@link Field} fieldInstance (instance of F) into classInstance (instance of T). It uses fieldName to look for private field.
	 * 
	 * @param classInstance
	 *            target instance
	 * @param fieldInstance
	 *            field instance to be injected
	 * @param fieldName
	 *            target field name
	 * @throws SecurityException
	 *             if this exception is thrown during the process
	 * @throws IllegalArgumentException
	 *             if fieldName could not be found in classInstance {@link Class} or if fieldInstance is not a compatible type to be injected
	 * @throws IllegalAccessException
	 *             if this exception is thrown during the process
	 */
	public static <T, F> void injectPrivateField(T classInstance, F fieldInstance, String fieldName) throws SecurityException,
			IllegalArgumentException, IllegalAccessException {

		List<Class<?>> classes = getSuperClasses(classInstance.getClass());
		for (Class<?> clazz : classes) {

			Field field;
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// try next class
				continue;
			}
			if (!field.getType().isAssignableFrom(fieldInstance.getClass())) {
				throw new IllegalArgumentException("Invalid fieldName received, fieldInstance can not assigned to field.");
			}

			field.setAccessible(true);
			field.set(classInstance, fieldInstance);
			field.setAccessible(false);
			return;

		}

		throw new IllegalArgumentException("Invalid fieldName received, a field with this name can not be found in this class or its superclasses.");
	}

	/**
	 * Retrieves all the super classes of given {@link Class} including itself.
	 * 
	 * @param clazz
	 *            target Class
	 * @return {@link List} containing all the super classes
	 */
	public static List<Class<?>> getSuperClasses(Class<?> clazz) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		classList.add(clazz);
		Class<?> superClass = clazz.getSuperclass();
		while (superClass != null) {
			classList.add(superClass);
			superClass = superClass.getSuperclass();
		}
		return classList;
	}

}
