package ws.m4ktub.quacking.j8;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import ws.m4ktub.quacking.Mixin;

public class DefaultMethodsTest {

	interface DefaultShyDuck { // not accessible
		default String quack() {
			return "quack";
		}
	}

	class DefaultShyBird implements DefaultShyDuck {

	}

	public interface DefaultDuck { // is public and accessible
		default String quack() {
			return "quack";
		}
	}

	class DefaultBird implements DefaultDuck { // does not need to be accessible

	}

	public class ExplicitBird {
		public String quack() {
			return "explicit quack";
		}
	}

	@Test
	public void defaultMethod() {
		Mixin bird = new Mixin();
		bird.mix(new DefaultBird());
		assertTrue(bird.is(DefaultDuck.class));
		assertThat(bird.as(DefaultDuck.class).quack(), is("quack"));
	}

	@Test
	public void explicitMethod() {
		Mixin bird = new Mixin();
		bird.mix(new ExplicitBird());
		assertTrue(bird.is(DefaultDuck.class));
		assertThat(bird.as(DefaultDuck.class).quack(), is("explicit quack"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void inaccessibleDefaultMethod() {
		Mixin bird = new Mixin();
		bird.mix(new DefaultShyBird());
		assertTrue(bird.is(DefaultShyDuck.class));
		assertThat(bird.as(DefaultShyDuck.class).quack(), is("quack"));
	}

}
