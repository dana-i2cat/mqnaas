package org.mqnaas.bundletree;

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
