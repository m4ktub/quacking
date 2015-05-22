package ws.m4ktub.quacking;

/**
 * A <tt>DuckType</tt> represents a type that is assignable to any interface but
 * does not necessarily implement that interface. As in any duck typing system
 * an instance may only need to quack to pass as a duck in certain situations.
 * This means a mixin may provide implementation for a single method and be used
 * with {@link #as(Class)}.</p>
 * 
 * <pre>
 * class DuckMap {
 * 	public Object get(Object key) {
 * 		return &quot;duck&quot;.equals(key) ? &quot;quack&quot; : null;
 * 	}
 * }
 * 
 * Mixin mixin = new Mixin(new DuckMap());
 * String quack = mixin.as(Map.class).get(&quot;duck&quot;);
 * </pre>
 * 
 * <p>
 * The full implementation of an interface can be checked with the
 * {@link #is(Class)}. Nevertheless, as shown, that is not a requirement to
 * convert an instance to a particular interface and call a few methods.
 * </p> 
 * 
 * @author m4ktub
 */
public interface DuckType {

	/**
	 * Verifies if the instance implements all methods of the given interface.
	 * 
	 * <p>
	 * The instance does not need to implement the interface but it needs to
	 * provide an implementation for each of the interface's methods.
	 * </p>
	 * 
	 * @param kind
	 *            An interface to check for full implementation.
	 * @return <code>true</code> if the instance contains an implementation for
	 *         every one of the interface's methods; <code>false</code>
	 *         otherwise.
	 */
	boolean is(Class<?> kind);

	/**
	 * Converts this instance into an instance of the given interface. This
	 * allows to call methods from that interface even if the instance does not
	 * provide an implementation for all methods.
	 * <p>
	 * Instances obtained from this conversion can always be cast to
	 * {@linkplain DuckType} but not to any other interface directly. This means
	 * that you must cast to {@linkplain DuckType} and then use
	 * {@linkplain #as(Class)} to convert to another interface.
	 * </p>
	 * 
	 * @param kind
	 *            The interface to convert to.
	 * @return A proxy instance of the specified kind.
	 * 
	 * @throws IllegalArgumentException
	 *             if <tt>kind</tt> is <code>null</code> or not an interface.
	 */
	<T> T as(Class<T> kind);

}
