package ws.m4ktub.quacking;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DuckMatcher {

	private Class<?> kind;
	private List<Method> missing;

	public DuckMatcher(Class<?> kind) {
		if (kind == null || !kind.isInterface()) {
			throw new IllegalArgumentException("A duck matcher can only be created for an interface class.");
		}

		this.kind = kind;
		reset();
	}

	public void reset() {
		this.missing = new ArrayList<Method>(Arrays.asList(kind.getMethods()));
	}

	public boolean matchDuck(Object object) {
		reset();
		match(object);
		return isDuck();
	}

	public boolean matchDuckling(Object object) {
		match(object);
		return isDuckling();
	}

	protected void match(Object object) {
		if (object == null) {
			return;
		}

		Class<?> objClass = object.getClass();

		Iterator<Method> missingIterator = missing.iterator();
		while (missingIterator.hasNext()) {
			try {
				// get equivalent method
				Method intfMethod = missingIterator.next();
				Method implMethod = objClass.getMethod(intfMethod.getName(), intfMethod.getParameterTypes());

				// check compatible return types
				Class<?> intfMethodReturnType = intfMethod.getReturnType();
				Class<?> implMethodReturnType = implMethod.getReturnType();

				if (!intfMethodReturnType.isAssignableFrom(implMethodReturnType)) {
					break;
				}

				// method matches, remove it
				missingIterator.remove();
			} catch (NoSuchMethodException e) {
				// skip and test next method
			}
		}

	}

	public boolean isDuck() {
		return missing.isEmpty();
	}

	public boolean isDuckling() {
		return missing.size() < kind.getMethods().length;
	}

}
