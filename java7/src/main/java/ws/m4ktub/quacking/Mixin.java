package ws.m4ktub.quacking;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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

	private List<Mixed> implementations;
	private DuckInvocationHandler invocationHandler;

	/**
	 * Creates a new mixin with no implementations.
	 */
	public Mixin() {
		implementations = new ArrayList<Mixed>();
		invocationHandler = new DuckInvocationHandler(this);
	}

	/**
	 * Obtains a list with the mixed instances that were specialized for the
	 * given interface and then all the other mixed instances.
	 * 
	 * @param kind
	 *            The interface being used.
	 * @return A list with an ordered list of mixed instances where specialized
	 *         implementations come first and are followed by other
	 *         implementations in the order they were mixed.
	 */
	protected List<Mixed> getMixed(Class<?> kind) {
		ArrayList<Mixed> candidates = new ArrayList<Mixed>(implementations.size());

		int preferencePos = 0;
		for (Mixed mixed : implementations) {
			if (mixed.hasPreferenceFor(kind)) {
				// add candidates with preference first in order
				candidates.add(preferencePos++, mixed);
			} else {
				// add others in order at the end
				candidates.add(mixed);
			}
		}

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
		for (Object object : getMixed(kind)) {
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
	 * Mixes a given instance as a possible implementation.
	 * 
	 * <p>
	 * If the instance implements {@link Mixer} then the object returned by the
	 * mixer will be the actual implementation being used.
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

		if (instance instanceof Mixer) {
			instance = ((Mixer) instance).in(this);

			if (instance == null) {
				String message = String.format("Mixer produced null. Cannot use null as an implementation.");
				throw new IllegalStateException(message);
			}
		}

		Mixed mixed = new Mixed(instance);
		implementations.add(mixed);
		return mixed;
	}

}
