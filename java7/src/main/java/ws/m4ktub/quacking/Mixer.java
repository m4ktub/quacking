package ws.m4ktub.quacking;

/**
 * A <tt>Mixer</tt> is an implementation that when used in a {@link Mixin}
 * expects to receive then mixin to produce the actual implementation. This can
 * also be used to store the mixing and invoke other methods through the mixin.
 * In that case care must be taken if the object is used in several mixins.
 * 
 * <pre>
 * class SelfAwareDuckling implements Mixer, Closeable {
 * 
 * 	private Mixing theMixin;
 * 
 * 	public Object in(Mixin mixin) {
 * 		theMixin = mixin;
 * 		return this;
 * 	}
 * 
 * 	public void close() {
 * 		theMixin.as(Collection.class).clear();
 * 	}
 * 
 * }
 * </pre>
 * 
 * @author m4ktub
 */
public interface Mixer {

	/**
	 * Produces the object implementation to be used in the given mixin instead
	 * of the actual instance. Nevertheless instances of <tt>Mixer</tt> are free
	 * to return <tt>this</tt> and be used as the implementation to be used.
	 * 
	 * @param mixin
	 *            The mixing that will be using the object implementation.
	 * @return The actual implementation used by the mixin when resolving
	 *         methods.
	 */
	Object in(Mixin mixin);

}
