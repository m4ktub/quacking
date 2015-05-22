package ws.m4ktub.quacking.j8;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import ws.m4ktub.quacking.Mixin;

public class LambdaExpressionsTest {

	public interface FunctionalDuck {
		String quack();
	}
	
	@Test
	public void lambdaImpl() {
		Mixin lambdaBird = new Mixin();
		lambdaBird.mix((FunctionalDuck) () -> "lambda quack");
		assertTrue(lambdaBird.is(FunctionalDuck.class));
		assertThat(lambdaBird.as(FunctionalDuck.class).quack(), is("lambda quack"));
	}
	
}
