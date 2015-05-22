package ws.m4ktub.quacking.helpers;

/**
 * Utility methods related with casts and type checking warnings.
 * 
 * @author m4ktub
 */
public final class Casts {

	/**
	 * Unsafely casts any object to the desired type and suppresses all warnings
	 * 
	 * @param o
	 *            Any object
	 * @return The object in the desired type.
	 * 
	 * @throws ClassCastException
	 *             If the object cannot be casted. That is not suppressed.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T ucast(Object o) {
		return (T) o;
	}

}
