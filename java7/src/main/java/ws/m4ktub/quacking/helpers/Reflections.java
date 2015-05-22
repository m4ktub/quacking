package ws.m4ktub.quacking.helpers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Utility methods related with reflection classes.
 * 
 * @author m4ktub
 */
public final class Reflections {

	/**
	 * Verify if a method is accessible and make it accessible if needed. If the
	 * method is public or is already accessible then nothing is done.
	 * 
	 * @param method
	 *            The method to make accessible.
	 * @throws SecurityException
	 *             If the method is not accessible and there is a security
	 *             manager that forbids the change in the method's
	 *             accessibility.
	 */
	public static void ensureAccessible(Method method) {
		boolean isMethodPublic = Modifier.isPublic(method.getModifiers());
		boolean isClassPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
		if ((!isMethodPublic || !isClassPublic) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

}
