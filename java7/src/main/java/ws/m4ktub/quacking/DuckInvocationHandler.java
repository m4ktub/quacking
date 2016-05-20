package ws.m4ktub.quacking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ws.m4ktub.quacking.helpers.Reflections;

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
		for (Mixed mixed : mixin.getMixed(intfMethod.getDeclaringClass())) {
			Invocation invocation = Reflections.getMethodInvocation(mixed, intfMethod, args);
			if (invocation == null) {
				// continue with next instance
				continue;
			}

			try {
				return invocation.proceeed();
			} catch (IllegalAccessException e) {
				// found method but could not invoke it
				String message = String.format("The mixin failed to invoke method %s on %s. The method was not accessible.", intfMethod, mixed);
				throw new UnsupportedOperationException(message, e);
			}
		}

		// no value was returned so throw an exception
		String message = String.format("The mixin does not support the method %s. The implemented method must be public and accept the same arguments.", intfMethod);
		throw new UnsupportedOperationException(message);
	}

}
