package ws.m4ktub.quacking.utils;

import ws.m4ktub.quacking.DuckWing;
import ws.m4ktub.quacking.Mixed;
import ws.m4ktub.quacking.Mixin;

class MixinProcess implements Mixing {

	private Mixin mixin;
	private Mixed current;

	public MixinProcess(Mixin mixin, Mixed mixed) {
		this.mixin = mixin;
		this.current = mixed;
	}

	@Override
	public Mixin get() {
		return mixin;
	}

	@Override
	public Mixing mix(Object instance) {
		current = mixin.mix(instance);
		return this;
	}

	@Override
	public Mixing preferring(Class<?> kind) {
		current.preferring(kind);
		return this;
	}

	@Override
	public Mixing around(String callName, DuckWing handler) {
		current.around(callName, handler);
		return this;
	}

	@Override
	public Mixing curry(String callName, Class<?>[] types, Object[] args) {
		current.curry(callName, types, args);
		return this;
	}

	@Override
	public Mixing rename(String callName, String methodName) {
		current.rename(callName, methodName);
		return this;
	}

	@Override
	public <T> T as(Class<T> kind) {
		return mixin.as(kind);
	}

}
