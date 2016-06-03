package ws.m4ktub.quacking.utils;

import ws.m4ktub.quacking.Mixin;

/**
 * Static utilities for the combination of several objects in mixins.
 * 
 * @author m4ktub
 */
public final class Mixins {

	/**
	 * Start a mixing with the given instance.
	 * 
	 * @param instance
	 *            The initial instance to mix.
	 * @return A on-going mixing to continue configuring the current instance or
	 *         mix other instances.
	 */
	public static Mixing mix(Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("The instance must not be null.");
		}

		Mixin mixin = new Mixin();
		return new MixinProcess(mixin, mixin.mix(instance));
	}

	/**
	 * Create a mixin from one or more objects that do not require
	 * configuration.
	 * 
	 * @param objects
	 *            The objects to mix in order.
	 * @return A mixin with all objects mixed.
	 */
	public static Mixin create(Object... objects) {
		if (objects == null) {
			throw new IllegalArgumentException("The objects array must not be null. Check if you have passed a null array as argument.");
		}

		Mixin mixin = new Mixin();
		for (Object o : objects) {
			mixin.mix(o);
		}

		return mixin;
	}

	/**
	 * Create a mixin from one or more objects for which the only required
	 * configuration is to define the preferred interface.
	 * 
	 * @param objects
	 *            The objects to mix in order.
	 * @param interfaces
	 *            The interfaces preferred by each object.
	 * @return A mixin with all objects mixed and configured.
	 */
	public static Mixin create(Object[] objects, Class<?>[] interfaces) {
		if (objects == null) {
			throw new IllegalArgumentException("The objects must not be null.");
		}

		if (interfaces == null) {
			throw new IllegalArgumentException("The interfaces must not be null.");
		}

		if (objects.length != interfaces.length) {
			throw new IllegalArgumentException("The objects and interfaces array must have the same length.");
		}

		Mixin mixin = new Mixin();
		for (int i = 0; i < objects.length; i++) {
			mixin.mix(objects[i]).preferring(interfaces[i]);
		}

		return mixin;
	}

}
