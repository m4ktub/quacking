package ws.m4ktub.quacking;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ws.m4ktub.quacking.helpers.Reflections;

/**
 * The duck matcher can be used to check if an object implements some or all of
 * the methods in an interface. If all methods have an implementation then we
 * have a duck. If only some methods are implemented by the object it's called a
 * duckling.
 * 
 * <pre>
 * DuckMatcher matcher = new DuckMatcher(Channel.class);
 * if (matcher.matchDuck(object)) {
 * 	object.getClass().getMethod(&quot;close&quot;).invoke(object);
 * }
 * </pre>
 * 
 * @author m4ktub
 */
public class DuckMatcher {

	private Class<?> kind;
	private List<Method> missing;

	/**
	 * Creates a matcher for the given interface type.
	 * 
	 * @param kind
	 *            An interface class.
	 */
	public DuckMatcher(Class<?> kind) {
		if (kind == null || !kind.isInterface()) {
			throw new IllegalArgumentException("A duck matcher can only be created for an interface class.");
		}

		this.kind = kind;
		reset();
	}

	/**
	 * Resets the matcher so previous matches do not affect the next match. As a
	 * that both {@link #isDuck()} and {@link #isDuckling()} will return
	 * <code>false</code> after the reset.
	 */
	public void reset() {
		this.missing = new ArrayList<Method>(Arrays.asList(kind.getMethods()));
	}

	/**
	 * Applies the matcher to the given object and checks if all methods from
	 * the interface have an implementation in the given object. The matcher is
	 * reset, before any matching takes place, to ensure all methods are
	 * implemented by the given object.
	 * 
	 * @param object
	 *            An instance or a {@link Mixed object} to test for the
	 *            implementation of all methods.
	 * @return <code>true</code> if all methods from the matcher's interface
	 *         have a compatible method in the object.
	 */
	public boolean matchDuck(Object object) {
		reset();
		match(object);
		return isDuck();
	}

	/**
	 * Applies the matcher to the given object and checks if at least one method
	 * from the interface has an implementation. The matcher is not reset before
	 * performing the matching. This allows to test for {@link #isDuck()} after
	 * matching each duckling.
	 * 
	 * @param object
	 *            An instance or a {@link Mixed object} to test for the
	 *            implementation of some methods.
	 * @return <code>true</code> if some method was matched by the given object.
	 */
	public boolean matchDuckling(Object object) {
		return match(object);
	}

	private boolean match(Object object) {
		if (object == null) {
			return false;
		}

		Mixed mixed = object instanceof Mixed ? (Mixed) object : new Mixed(object);
		boolean matched = false;

		Iterator<Method> missingIterator = missing.iterator();
		while (missingIterator.hasNext()) {
			Method method = missingIterator.next();
			Method implMethod = Reflections.getCompatibleMethod(mixed, method, method.getParameterTypes());
			if (implMethod == null) {
				continue;
			}

			// found compatible method, remove it
			missingIterator.remove();
			matched = true;
		}

		return matched;
	}

	/**
	 * @return <code>true</code> if all the methods for the matcher's interface
	 *         where matched with a method from one of the matched objects.
	 */
	public boolean isDuck() {
		return missing.isEmpty();
	}

	/**
	 * @return <code>true</code> if at least one of the methods for the
	 *         matcher's interface was matched with a method from one of the
	 *         matched objects but not all methods have been matched.
	 */
	public boolean isDuckling() {
		return missing.size() < kind.getMethods().length;
	}

}
