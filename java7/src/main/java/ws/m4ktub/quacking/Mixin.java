package ws.m4ktub.quacking;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ws.m4ktub.quacking.helpers.Casts;

/**
 * The {@linkplain Mixin} is the main {@link DuckType} instance. You create a
 * mixin then mix additional instances to provide implementations for particular
 * interface methods. You can check if a mixin fulfills all the methods for an
 * interface with the {@link #is(Class)} method. Even if it does not, you can
 * always convert a mixin into an instance of that interface, with the {@ink
 * #as(Class)} method, and invoke any of the implemented methods.
 * 
 * <p>
 * As an example you can mix two different instances to provide an iterator.
 * 
 * <pre>
 * public class NextChecker {
 *   public boolean hasNext() {
 *     ....
 *   }
 * }
 * 
 * public class NextGenerator {
 *   public Object next() {
 *     ...
 *   }
 * }
 * 
 * Mixin mixin = new Mixin();
 * mixin.mix(new NextChecker());
 * mixin.mix(new NextGenerator());
 * ...
 * Iterator iter = mixin.as(Iterator.class);
 * while (iter.hasNext()) {
 *   Object value = iter.next();
 *   ...
 * }
 * </pre>
 * 
 * <p>
 * Currently the mixin does not support generic types which means you are
 * limited to Object types and must cast explicitly. Also, since there can be
 * multiple implementations for the same method, you can specialize and
 * implementation after mixin.
 * 
 * <pre>
 * mixin.mix(new NextGenerator()).as(Iterator.class);
 * </pre>
 * 
 * This makes the <tt>NextGenerator</tt> instance to always be tried first
 * regardless of the order of mixin. Otherwise implementations are used in the
 * order they are mixed together.
 * 
 * @author m4ktub
 */
public class Mixin implements DuckType {

	private Map<Class<?>, Object> interfaceMap;
	private List<Object> implementations;
	private DuckInvocationHandler invocationHandler;

	/**
	 * Creates a new mixin with no implementations.
	 */
	public Mixin() {
		implementations = new ArrayList<Object>();
		interfaceMap = new HashMap<Class<?>, Object>();
		invocationHandler = new DuckInvocationHandler(this);
	}

	/**
	 * Obtains a list with the instances that were specialized for the given
	 * interface and then all the other implementations.
	 * 
	 * @param kind
	 *            The interface being used.
	 * @return A list with an ordered list of implementations where specialized
	 *         implementations come first and are followed by other
	 *         implementations in the order they were mixed.
	 */
	protected List<Object> getInstances(Class<?> kind) {
		ArrayList<Object> candidates = new ArrayList<Object>();

		// first add specialized object if available
		if (interfaceMap.containsKey(kind)) {
			candidates.add(interfaceMap.get(kind));
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
		return Casts.ucast(Proxy.newProxyInstance(classLoader, interfaces, invocationHandler));
	}

	/**
	 * Mixes a given instance as an possible implementations.
	 * 
	 * @param instance
	 *            The instance to use as implementations of some methods.
	 * @return A {@link Mixed} abstraction that allows to fine tune the mixed
	 *         instance.
	 */
	public Mixed mix(Object instance) {
		if (instance == null) {
			String message = String.format("Tried to mix null. Cannot use null as an implementation.");
			throw new IllegalArgumentException(message);
		}

		implementations.add(instance);
		return new Mixed(instance);
	}

	/**
	 * An abstraction for a mixed instance. This allows to control how the
	 * instance is used in the mixin.
	 */
	public class Mixed {

		private Object instance;

		Mixed(Object instance) {
			this.instance = instance;
		}

		/**
		 * Specialize the mixed instance to the given interface. This means that
		 * whenever the mixin is used as the given interface this
		 * implementations will be used before the others that where mixed
		 * before.
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
		 * mixin.mix(new Constant(2)).as(Value.class);
		 * mixin.mix(new Constant(3));
		 * 
		 * assertTrue(mixin.as(Value.class).getValue() == 2);
		 * </pre>
		 * 
		 * @param kind
		 *            The interface to specialize in.
		 * @return The same mixed abstraction to allow fluid configuration.
		 */
		public Mixed as(Class<?> kind) {
			if (kind == null) {
				throw new IllegalArgumentException("Cannot pass null when specializing a mixed instance.");
			}

			if (!kind.isInterface()) {
				String message = String.format("An interface must be used. Cannot specialize for class \"%s\".", kind.getName());
				throw new IllegalArgumentException(message);
			}

			interfaceMap.put(kind, instance);
			return this;
		}

	}

}
