package org.mqnaas.bundletree;

/**
 * Class Filter interface allowing to define an arbitrary logic to filter {@link Class}es in the method {@link #filter(Class)}.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public interface IClassFilter {

	/**
	 * Defines an arbitrary logic to filter {@link Class}es.
	 * 
	 * @param clazz
	 *            Class to be filtered
	 * @return true if given Class fulfils implemented logic, false otherwise
	 */
	public boolean filter(Class<?> clazz);

}
