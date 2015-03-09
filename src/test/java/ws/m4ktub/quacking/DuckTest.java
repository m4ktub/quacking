package ws.m4ktub.quacking;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

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
		Mixin bird = new Mixin(new Bird());
		assertTrue(bird.is(Duck.class));
		assertFalse(bird.is(Cat.class));
	}

	@Test
	public void isNull() {
		Mixin bird = new Mixin(new Bird());
		Mixin nul = new Mixin(null);
		assertFalse(bird.is(null)); // nothing is null
		assertFalse(nul.is(Duck.class)); // null implements nothing
	}

	@Test
	public void asInterface() {
		Mixin bird = new Mixin(new Bird());
		assertThat(bird.as(Duck.class).quack(), is("quack"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void asShy() {
		Mixin bird = new Mixin(new ShyBird());
		assertThat(bird.as(Duck.class).quack(), is("quack"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void asNullError() {
		Mixin bird = new Mixin(new Bird());
		assertThat(bird.as(null), equalTo(null));
	}

	@Test
	public void asNull() {
		Mixin nul = new Mixin(null);
		assertThat(nul.as(Duck.class), equalTo(null));
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void asClass() {
		Mixin mixin = new Mixin(new Object());
		mixin.as(Bird.class);
	}

	@Test
	public void asChain() {
		Mixin bird = new Mixin(new Bird());
		Duck duck = bird.as(Duck.class);
		DuckType duckType = (DuckType) duck;
		Quacker quacker = duckType.as(Quacker.class);
		assertThat(quacker.quack(), is("quack"));
	}

	@Test
	public void asObject() {
		Bird birdImpl = new Bird();
		Mixin bird = new Mixin(birdImpl);
		Duck duck = bird.as(Duck.class);
		assertThat(duck.hashCode(), equalTo(birdImpl.hashCode()));
		assertThat(duck.toString(), equalTo(birdImpl.toString()));
		assertTrue(duck.equals(birdImpl));
		assertTrue(duck.equals(duck));
	}

}
