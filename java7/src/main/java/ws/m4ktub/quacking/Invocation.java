package ws.m4ktub.quacking;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Simple representation of an method invocation that can be wrapped in a
 * {@link DuckWing}.
 * 
 * @author m4ktub
 */
public class Invocation {

	public static final Object CURRY_MISS = new Object();

	private final DuckWing DEFAULT = new DuckWing.Default();

	private Object target;
	private DuckWing around;
	private Method method;
	private Object[] curryArgs;

	/**
	 * Creates an immutable invocation with all properties.
	 * 
	 * @param target
	 *            The target instance of the invocation.
	 * @param around
	 *            The around logic to apply or <code>null</code> if the method
	 *            should be invoked directly.
	 * @param method
	 *            The method to invoke on the target.
	 * @param curryArgs
	 *            The template argument array with the curried arguments and
	 *            holes for the interface arguments or <code>null</code> if no
	 *            curry is used and only the original arguments will be passed.
	 */
	public Invocation(Object target, DuckWing around, Method method, Object[] curryArgs) {
		super();
		this.target = target;
		this.around = around;
		this.method = method;
		this.curryArgs = curryArgs;
	}

	public Object getTarget() {
		return target;
	}

	public DuckWing getAround() {
		return around;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return curryArgs;
	}

	private DuckWing around() {
		return around == null ? DEFAULT : around;
	}

	/**
	 * Performs the invocation with the given arguments.
	 * 
	 * @return The result of the invocation.
	 * @throws Throwable
	 *             When the invocation results in an error.
	 */
	public Object proceeed(Object[] args) throws Throwable {
		return around().wrap(getTarget(), getMethod(), getFinalArgs(args));
	}

	protected Object[] getFinalArgs(Object[] intfArgs) {
		if (this.curryArgs == null) {
			return intfArgs;
		} else {
			int misses = 0;
			for (int i = 0; i < curryArgs.length; i++) {
				if (curryArgs[i] == CURRY_MISS) {
					misses++;
				}
			}
			
			if (intfArgs == null) {
				intfArgs = new Object[0];
			}
			
			Object[] finalArgs = new Object[this.curryArgs.length + intfArgs.length - misses];
			System.arraycopy(this.curryArgs, 0, finalArgs, 0, this.curryArgs.length);
			if (misses == 0) {
				System.arraycopy(intfArgs, 0, finalArgs, curryArgs.length, intfArgs.length);
			} else {
				Arrays.fill(finalArgs, this.curryArgs.length, finalArgs.length, CURRY_MISS);
				for (int i = 0, j = 0; i < finalArgs.length; i++) {
					if (finalArgs[i] == CURRY_MISS) {
						finalArgs[i] = intfArgs[j++];
					}
				}
			}

			return finalArgs;
		}
	}

}
