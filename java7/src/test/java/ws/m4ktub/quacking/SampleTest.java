package ws.m4ktub.quacking;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import ws.m4ktub.quacking.Mixin.Mixed;

public class SampleTest {

	public interface Value {
		int getValue();
	}

	public class Constant {
		int value;

		public Constant(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public class NextChecker {
		public boolean hasNext() {
			return Math.random() < 0.5; // ...
		}
	}

	public class NextGenerator {
		public Object next() {
			return Math.random(); // ...
		}
	}

	/**
	 * @see Mixin
	 */
	@Test
	@SuppressWarnings("unused")
	public void sampleMixin() {
		Mixin mixin = new Mixin();
		mixin.mix(new NextChecker());
		mixin.mix(new NextGenerator());
		// ...
		Iterator<?> iter = mixin.as(Iterator.class);
		while (iter.hasNext()) {
			Object value = iter.next();
			// ...
		}
	}

	/**
	 * @see Mixed#as(Class)
	 */
	@Test
	public void sampleMixedAs() {
		Mixin mixin = new Mixin();
		mixin.mix(new Constant(1));
		mixin.mix(new Constant(2)).as(Value.class);
		mixin.mix(new Constant(3));

		assertTrue(mixin.as(Value.class).getValue() == 2);
	}
}
