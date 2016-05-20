package ws.m4ktub.quacking;

import java.lang.reflect.Method;

/**
 * Simple representation of an method invocation that can be wrapped in a
 * {@link DuckWing}.
 * 
 * @author m4ktub
 */
public class Invocation {

	private final DuckWing DEFAULT = new DuckWing.Default();

	private Object target;
	private DuckWing around;
	private Method method;
	private Object[] args;

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
	 * @param args
	 *            The default arguments to pass in the invocation.
	 */
	public Invocation(Object target, DuckWing around, Method method, Object[] args) {
		super();
		this.target = target;
		this.around = around;
		this.method = method;
		this.args = args;
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
		return args;
	}

	private DuckWing around() {
		return around == null ? DEFAULT : around;
	}

	/**
	 * Performs the invocation with the default arguments.
	 * 
	 * @return The result of the invocation.
	 * @throws Throwable
	 *             When the invocation results in an error.
	 */
	public Object proceeed() throws Throwable {
		return around().wrap(getTarget(), getMethod(), getArgs());
	}

	/**
	 * Performs the invocation with the given arguments.
	 * 
	 * @return The result of the invocation.
	 * @throws Throwable
	 *             When the invocation results in an error.
	 */
	public Object proceeed(Object[] args) throws Throwable {
		return around().wrap(getTarget(), getMethod(), args);
	}

}
