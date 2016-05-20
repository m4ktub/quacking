package ws.m4ktub.quacking;

import java.lang.reflect.Method;

/**
 * A simple invocation handler that allows to wrap a method invocation.
 * 
 * @author m4ktub
 * @see Mixed
 */
public interface DuckWing {

	/**
	 * The default wing simply calls the method.
	 * 
	 * @author m4ktub
	 */
	public static class Default implements DuckWing {

		@Override
		public Object wrap(Object target, Method method, Object[] args) throws Throwable {
			return method.invoke(target, args);
		}

	};

	/**
	 * Wraps the invocation represented by the given object, method, and
	 * arguments. This allows, for example, to perform actions before or after
	 * the invocation, suppress the invocation entirely, or change the
	 * arguments.
	 * 
	 * @param target
	 *            The target instance of the invocation.
	 * @param method
	 *            The selected method to invoke.
	 * @param args
	 *            The arguments to the method.
	 * @return The result of the invocation.
	 */
	Object wrap(Object target, Method method, Object[] args) throws Throwable;

}
