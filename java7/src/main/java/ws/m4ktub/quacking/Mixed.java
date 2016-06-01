package ws.m4ktub.quacking;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstraction for a mixed instance. This allows the configuration of how the
 * instance is used in the mixin.
 * 
 * @author m4ktub
 */
public class Mixed {

	private static class MethodArgTypes {
		public Method method;
		public Type[] argTypes;

		public MethodArgTypes(Method method, Type[] argTypes) {
			this.method = method;
			this.argTypes = argTypes;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(argTypes) + method.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			MethodArgTypes other = (MethodArgTypes) obj;
			if (!Arrays.equals(argTypes, other.argTypes)) {
				return false;
			}

			if (!method.equals(other.method)) {
				return false;
			}

			return true;
		}

	}

	private Object instance;
	private List<Class<?>> preferredInterfaces;
	private Map<String, MethodConfiguration> configurations;
	private Map<MethodArgTypes, Invocation> methodCache = new HashMap<MethodArgTypes, Invocation>();

	/**
	 * Allows the specification or several configurations for methods in the
	 * mixed instance.
	 * 
	 * @author m4ktub
	 */
	public static class MethodConfiguration {
		private String rename;
		public Class<?>[] curriedTypes;
		private Object[] curriedArgs;
		private DuckWing wing;

		public boolean isRenamed() {
			return rename != null;
		}

		public String getRename() {
			return rename;
		}

		public boolean isCurried() {
			return curriedArgs != null && curriedArgs.length > 0;
		}

		public Object[] getCurriedArgs() {
			return curriedArgs;
		}

		public Class<?>[] getCurriedTypes() {
			return curriedTypes;
		}

		public boolean isWrapped() {
			return wing != null;
		}

		public DuckWing getAroundHandler() {
			return wing;
		}
	}

	/**
	 * Creates a new mixed instance allowing configuration of calls.
	 * 
	 * @param instance
	 *            The original instance made configurable.
	 */
	public Mixed(Object instance) {
		this.instance = instance;
	}

	/**
	 * @return The original instance made configurable.
	 */
	public Object getInstance() {
		return instance;
	}

	/**
	 * If this instance has preference for a particular interface. It basically
	 * tells if {@link #preferring(Class)} was called with the same interface.
	 * 
	 * <p>
	 * This method will not fail if the given class is not an interface. In that
	 * case it will simply return <code>false</code> as not preference could
	 * have been established. Also, the interface hierarchy is not considered. A
	 * instance that is <tt>preferring(List.class)</tt> is not preferring
	 * <tt>Collection.class</tt>.
	 * 
	 * @param kind
	 *            The interface to test for preference.
	 * @return <code>true</code> when this instance was configured as a
	 *         preferred instance for the given interface.
	 */
	public boolean hasPreferenceFor(Class<?> kind) {
		return this.preferredInterfaces != null && this.preferredInterfaces.contains(kind);
	}

	/**
	 * If this instance has any configuration for the given interface method
	 * name.
	 * 
	 * @param methodName
	 *            The name of the interface method that would be called.
	 * @return <code>true</code> if some configuration was established for the
	 *         identified method.
	 */
	public boolean hasConfigurationFor(String methodName) {
		return configurations != null && configurations.containsKey(methodName);
	}

	/**
	 * Obtains the existing configuration object for the given interface method
	 * name.
	 * 
	 * @param methodName
	 *            The name of the interface method that would be called.
	 * @return The existing method configuration object or <code>null</code> if
	 *         nothing was configured for the identified method.
	 */
	public MethodConfiguration getConfigurationFor(String methodName) {
		return configurations == null ? null : configurations.get(methodName);
	}

	/**
	 * Specialize the mixed instance to the given interface. This means that
	 * whenever the mixin is used as the given interface this implementations
	 * will be used before the others that where mixed before.
	 * 
	 * <pre>
	 * public interface Value {
	 *   int getValue();
	 * }
	 * 
	 * public class Constant {
	 *   int value;
	 *   public Constant(int value) {
	 *   	this.value = value;
	 *   }
	 *   
	 *   public int getValue() {
	 *     return value;
	 *   }
	 * }
	 * 
	 * Mixin mixin = new Mixin();
	 * mixin.mix(new Constant(1));
	 * mixin.mix(new Constant(2)).preferring(Value.class);
	 * mixin.mix(new Constant(3));
	 * 
	 * assertTrue(mixin.as(Value.class).getValue() == 2);
	 * </pre>
	 * 
	 * @param kind
	 *            The interface to specialize in.
	 * @return The same mixed abstraction to allow fluid configuration.
	 */
	public Mixed preferring(Class<?> kind) {
		if (kind == null) {
			throw new IllegalArgumentException("Cannot pass null when specializing a mixed instance.");
		}

		if (!kind.isInterface()) {
			String message = String.format("An interface must be used. Cannot specialize for class \"%s\".", kind.getName());
			throw new IllegalArgumentException(message);
		}

		if (this.preferredInterfaces == null) {
			this.preferredInterfaces = new ArrayList<Class<?>>(1);
		}

		this.preferredInterfaces.add(kind);
		return this;
	}

	/**
	 * Ensures a method configuration object exists for the interface method
	 * name. If the configuration object does not exist it will be created.
	 * 
	 * @param methodName
	 *            The name of the interface method that would be called.
	 * @return The method configuration object for the identified method.
	 */
	protected MethodConfiguration configureMethod(String methodName) {
		if (this.configurations == null) {
			this.configurations = new HashMap<String, MethodConfiguration>();
			this.configurations.put(methodName, new MethodConfiguration());
		} else if (!this.configurations.containsKey(methodName)) {
			this.configurations.put(methodName, new MethodConfiguration());
		}

		return this.configurations.get(methodName);
	}

	/**
	 * Allows to match a method of the instance with an interface method despite
	 * not having exactly the same name.
	 * 
	 * @param callName
	 *            The name of the interface method that would be called.
	 * @param methodName
	 *            The new name method name to used instead with this instance.
	 * @return The same mixed abstraction to allow fluid configuration.
	 */
	public Mixed rename(String callName, String methodName) {
		configureMethod(callName).rename = methodName;
		return this;
	}

	/**
	 * Allows to match a method of the instance with an interface method despite
	 * having a different number of parameters. The extra parameters can be
	 * given constant values, as arguments, that will be passed together with
	 * the interface method's arguments. Note that the interface values are
	 * never suppressed which means the instance method will have more
	 * parameters.
	 * 
	 * <p>
	 * If the interface's arguments are to be passed as the last arguments of
	 * the instance's method then you just need to specify the constants.
	 * Otherwise you have to use {@value #CURRY_SKIP} to mark a positions as not
	 * curried.
	 * 
	 * @param callName
	 *            The name of the interface method that would be called.
	 * @param types
	 *            The concrete types of the instance method arguments.
	 * @param args
	 *            The constants to be passed to the method in each invocation or
	 *            the value {@value #CURRY_SKIP} to leave as position for an
	 *            interface argument.
	 * @return The same mixed abstraction to allow fluid configuration.
	 */
	public Mixed curry(String callName, Class<?>[] types, Object[] args) {
		configureMethod(callName).curriedTypes = types;
		configureMethod(callName).curriedArgs = args;
		return this;
	}

	/**
	 * Allows to put an method invocation under a {@link DuckWing} handler to
	 * perform a more advanced control of the invocation.
	 * 
	 * @param callName
	 *            The name of the interface method that would be called.
	 * @param handler
	 *            The method invocation handler to be used in any invocation of
	 *            the given method.
	 * @return The same mixed abstraction to allow fluid configuration.
	 */
	public Mixed around(String callName, DuckWing handler) {
		configureMethod(callName).wing = handler;
		return this;
	}

	public Invocation getCachedInvocation(Method intfMethod, Type[] argTypes) {
		return methodCache.get(new MethodArgTypes(intfMethod, argTypes));
	}

	public Mixed cacheInvocation(Method intfMethod, Type[] argTypes, Invocation invocation) {
		methodCache.put(new MethodArgTypes(intfMethod, argTypes), invocation);
		return this;
	}

}