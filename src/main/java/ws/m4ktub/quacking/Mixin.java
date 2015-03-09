package ws.m4ktub.quacking;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@linkplain Mixin} is the main duck type instance. You create a mixin
 * from a base instance and then mix additional instances to provide
 * implementations for particular interface methods.
 * 
 * <pre>
 * class Store {
 *   public boolean isEmpty() {
 *     ...
 *   }
 * }
 * 
 * class NextChecker {
 *   public boolean hasNext() {
 *     ....
 *   }
 * }
 * 
 * class NextGenerator {
 *   public Object next() {
 *     ...
 *   }
 * }
 * 
 * Mixin mixin = new Mixin(new Store());
 * ...
 * if (!mixin.as(Collection.class).isEmpty()) {
 *   Iterator iter = mixin.as(Iterator.class);
 *   while (iter.hasNext()) {
 *     Object value = iter.next();
 *     ...
 *   }
 * }
 * </pre>
 * 
 * @author m4ktub
 */
public class Mixin implements DuckType {

	private Map<Class<?>, Object> interfaces = new HashMap<Class<?>, Object>();
	private List<Object> implementations = new ArrayList<Object>();

	public Mixin(Object instance) {
		if (instance != null) {
			implementations.add(instance);
		}
	}

	protected List<Object> getInstances(Class<?> kind) {
		ArrayList<Object> candidates = new ArrayList<Object>();

		// first add specialized object if available
		if (interfaces.containsKey(kind)) {
			candidates.add(interfaces.get(kind));
		}

		// second add implementations
		candidates.addAll(implementations);

		return candidates;
	}

	@Override
	public boolean is(Class<?> kind) {
		// mixin is never null
		if (kind == null) {
			return false;
		}

		// can only be an interface
		if (!kind.isInterface()) {
			return false;
		}

		DuckMatcher matcher = new DuckMatcher(kind);
		for (Object object : getInstances(kind)) {
			matcher.matchDuckling(object);
		}

		return matcher.isDuck();
	}

	@Override
	public <T> T as(Class<T> kind) {
		if (kind == null) {
			String message = String.format("An interface must be used instead of null.");
			throw new IllegalArgumentException(message);
		}

		if (!kind.isInterface()) {
			String message = String.format("An interface must be used. Cannot reimplement class \"%s\".", kind.getName());
			throw new IllegalArgumentException(message);
		}

		// threat the null implementation case
		if (implementations.isEmpty()) {
			return null;
		}

		// ensure a classloader
		ClassLoader classLoader = kind.getClassLoader();
		if (classLoader == null) {
			classLoader = this.getClass().getClassLoader();
		}

		// create proxy ensuring kind and DuckType classes
		Class<?>[] interfaces = new Class<?>[] { DuckType.class, kind };
		DuckInvocationHandler h = new DuckInvocationHandler(this);
		return ucast(Proxy.newProxyInstance(classLoader, interfaces, h));
	}

	@SuppressWarnings("unchecked")
	private static <T> T ucast(Object o) {
		return (T) o;
	}

	public Mixed mix(Object instance) {
		implementations.add(instance);
		return new Mixed(instance);
	}

	public class Mixed {

		private Object instance;

		public Mixed(Object instance) {
			this.instance = instance;
		}

		public Mixed as(Class<?> kind) {
			if (kind == null) {
				throw new IllegalArgumentException("Cannot pass null when specializing a mixed instance.");
			}

			if (!kind.isInterface()) {
				String message = String.format("An interface must be used. Cannot specialize for class \"%s\".", kind.getName());
				throw new IllegalArgumentException(message);
			}

			interfaces.put(kind, instance);
			return this;
		}

	}

}
