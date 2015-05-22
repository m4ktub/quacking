package ws.m4ktub.quacking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class DuckInvocationHandler implements InvocationHandler {

	private Mixin mixin;

	public DuckInvocationHandler(Mixin mixin) {
		this.mixin = mixin;
	}

	public final Mixin getMixin() {
		return mixin;
	}

	@Override
	public Object invoke(Object proxy, Method intfMethod, Object[] args) throws Throwable {
		// handle DuckType interface explicitly with mixin
		if (intfMethod.getDeclaringClass().equals(DuckType.class)) {
			return intfMethod.invoke(mixin, args);
		}

		// ensure special case of proxy.equals(proxy)
		if (intfMethod.getDeclaringClass().equals(Object.class) && intfMethod.getName().equals("equals") && args[0] == proxy) {
			return true;
		}

		// delegate method invocation to instances
		for (Object instance : mixin.getInstances(intfMethod.getDeclaringClass())) {
			Method implMethod = getMethod(intfMethod, instance);
			if (implMethod == null) {
				// continue with next instance
				continue;
			}

			try {
				// perform invocation
				return implMethod.invoke(instance, args);
			} catch (IllegalAccessException e) {
				// found method but could not invoke it
				String message = String.format("The mixin failed to invoke method %s on %s. The method was not accessible.", intfMethod, instance);
				throw new UnsupportedOperationException(message, e);
			}
		}

		// no value was returned so throw an exception
		String message = String.format("The mixin does not support the method %s. The implemented method must be public and accept the same arguments.", intfMethod);
		throw new UnsupportedOperationException(message);
	}

	protected Method getMethod(Method intfMethod, Object instance) {
		if (intfMethod.getDeclaringClass().isAssignableFrom(instance.getClass())) {
			return intfMethod;
		}

		try {
			return instance.getClass().getMethod(intfMethod.getName(), intfMethod.getParameterTypes());
		} catch (NoSuchMethodException e) {
			// transform exception into null
			return null;
		}
	}

}
