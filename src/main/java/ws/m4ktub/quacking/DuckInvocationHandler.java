package ws.m4ktub.quacking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class DuckInvocationHandler implements InvocationHandler {

	private Mixin mixin;

	public DuckInvocationHandler(Mixin mixin) {
		this.mixin = mixin;
	}

	@Override
	public Object invoke(Object proxy, Method intfMethod, Object[] args) throws Throwable {
		// implement the interface common to all proxies directly
		if (intfMethod.getDeclaringClass().equals(DuckType.class)) {
			return intfMethod.invoke(mixin, args);
		}

		// ensure special case of proxy.equals(proxy)
		if (intfMethod.getDeclaringClass().equals(Object.class) && intfMethod.getName().equals("equals") && args[0] == proxy) {
			return true;
		}

		// delegate method invocation to instances
		for (Object instance : mixin.getInstances(intfMethod.getDeclaringClass())) {
			try {
				Method implMethod = instance.getClass().getMethod(intfMethod.getName(), intfMethod.getParameterTypes());
				return implMethod.invoke(instance, args);
			} catch (NoSuchMethodException e) {
				// ignore to process next instance
			}
		}

		// no value was returned so throw exception
		String message = String.format("The mixin does not support the method %s. The implemented method must be public and accept the same arguments.", intfMethod);
		throw new UnsupportedOperationException(message);
	}

	

}
