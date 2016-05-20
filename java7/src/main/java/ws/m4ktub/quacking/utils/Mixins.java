package ws.m4ktub.quacking.utils;

import ws.m4ktub.quacking.Mixin;

public final class Mixins {

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
