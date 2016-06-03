package ws.m4ktub.quacking.utils;

import ws.m4ktub.quacking.DuckWing;
import ws.m4ktub.quacking.Mixed;
import ws.m4ktub.quacking.Mixin;

/**
 * Represents an on-going mixing over a {@link Mixin}.
 * 
 * @see Mixins#mix(Object)
 * @author m4ktub
 */
public interface Mixing {

	/**
	 * @return The mixin in it's current state.
	 */
	Mixin get();

	/**
	 * Equivalent to {@link #get()}.{@link Mixin#as(Class) as(kind)}.
	 * 
	 * @param kind
	 *            The interface to convert to.
	 * @return A proxy instance of the specified kind.
	 */
	<T> T as(Class<T> kind);

	/**
	 * Mixes a new instance. That mixed instance will be the target of any
	 * configuration.
	 * 
	 * @param instance
	 *            The instance to mix.
	 * @return The same on-going mixing.
	 * 
	 * @see Mixed
	 */
	Mixing mix(Object instance);

	/**
	 * @see Mixed#preferring(Class)
	 */
	Mixing preferring(Class<?> kind);

	/**
	 * @see Mixed#around(String, DuckWing)
	 */
	Mixing around(String callName, DuckWing handler);

	/**
	 * @see Mixed#curry(String, Class[], Object[])
	 */
	Mixing curry(String callName, Class<?>[] types, Object[] args);

	/**
	 * @see Mixed#rename(String, String)
	 */
	Mixing rename(String callName, String methodName);

}
