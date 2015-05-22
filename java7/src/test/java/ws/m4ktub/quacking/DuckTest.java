package ws.m4ktub.quacking;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test the basic mechanisms of verifying if a mixin is an interface and of
 * converting a mixin to an interface.
 */
public class DuckTest {

	interface Cat {
		void walk();

		void purr();

		String meow();
	}

	interface Quacker {
		String quack();
	}

	interface Duck {
		void walk();

		void swim();

		String quack();
	}

	class Bird {
		public void fly() {
		}

		public void walk() {
		}

		public void swim() {
		}

		public String quack() {
			return "quack";
		}
	}

	/**
	 * The <tt>ShyBird</tt> does not have public methods. If the method is not
	 * public then it is not accessible, by default, to the mixin invocation
	 * handler.
	 */
	class ShyBird {
		void fly() {
		}

		void walk() {
		}

		void swim() {
		}

		String quack() {
			return "quack";
		}
	}

	@Test
	public void isInterface() {
		Mixin bird = new Mixin();
		bird.mix(new Bird());
		assertTrue(bird.is(Duck.class));
		assertFalse(bird.is(Cat.class));
	}

	@Test
	public void isNull() {
		Mixin bird = new Mixin();
		bird.mix(new Bird());
		assertFalse(bird.is(null)); // nothing is null
	}

	@Test
	public void asInterface() {
		Mixin bird = new Mixin();
		bird.mix(new Bird());
		assertThat(bird.as(Duck.class).quack(), is("quack"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void asShy() {
		Mixin bird = new Mixin();
		bird.mix(new ShyBird());
		assertThat(bird.as(Duck.class).quack(), is("quack"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void asNullError() {
		Mixin bird = new Mixin();
		bird.mix(new Bird());
		assertThat(bird.as(null), equalTo(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void mixNull() {
		Mixin nul = new Mixin();
		nul.mix(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void asClass() {
		Mixin mixin = new Mixin();
		mixin.mix(new Object());
		mixin.as(Bird.class); // not an interface
	}

	@Test
	public void asChainInstanceOf() {
		Mixin bird = new Mixin();
		bird.mix(new Bird());
		Duck duck = bird.as(Duck.class);
		assertTrue(duck instanceof DuckType);
	}

	@Test
	public void asChain() {
		Mixin bird = new Mixin();
		bird.mix(new Bird());
		Duck duck = bird.as(Duck.class);
		DuckType duckType = (DuckType) duck; // to upper
		Quacker quacker = duckType.as(Quacker.class);
		assertThat(quacker.quack(), is("quack"));
	}

	@Test
	public void asObject() {
		Bird birdImpl = new Bird();
		Mixin bird = new Mixin();
		bird.mix(birdImpl);
		Duck duck = bird.as(Duck.class);
		assertThat(duck.hashCode(), equalTo(birdImpl.hashCode()));
		assertThat(duck.toString(), equalTo(birdImpl.toString()));
		assertTrue(duck.equals(birdImpl));
		assertTrue(duck.equals(duck));
	}

}
