package ws.m4ktub.quacking;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class MixedTest {

	public interface Calc {

		int add(int a, int b);

		int sub(int a, int b);

	}

	class SignsCalc {
		public int plus(int a, int b) {
			return a + b;
		}

		public int minus(int a, int b) {
			return a - b;
		}
	}

	class GenericCalc {
		static final int OP_SUM = 1;
		static final int OP_SUB = 2;

		public int binOp(int op, int a, int b) {
			switch (op) {
			case OP_SUM:
				return a + b;
			case OP_SUB:
				return a - b;
			default:
				throw new IllegalArgumentException("Invalid operation.");
			}
		}
	}

	@Test
	public void rename() {
		Mixin mixin = new Mixin();

		mixin.mix(new SignsCalc()) //
				.rename("add", "plus") //
				.rename("sub", "minus");

		Calc c = mixin.as(Calc.class);
		assertEquals(3, c.add(1, 2));
		assertEquals(1, c.sub(2, 1));
	}

	@Test
	public void curry() {
		Mixin mixin = new Mixin();

		Class<?>[] types = new Class<?>[] { Integer.TYPE };

		mixin.mix(new GenericCalc()) //
				.rename("add", "binOp").curry("add", types, new Object[] { GenericCalc.OP_SUM }) //
				.rename("sub", "binOp").curry("sub", types, new Object[] { GenericCalc.OP_SUB }); //

		Calc c = mixin.as(Calc.class);
		assertEquals(3, c.add(1, 2));
		assertEquals(1, c.sub(2, 1));
	}

	@Test
	public void arround() {
		Mixin mixin = new Mixin();

		DuckWing handler = new DuckWing() {

			@Override
			public Object wrap(Object target, Method method, Object[] args) throws Throwable {
				int a = (int) args[0];
				int b = (int) args[1];

				String methodName = method.getName();
				if (methodName.equals("add")) {
					a += 1;
				} else {
					b += 1;
				}

				return method.invoke(target, new Object[] { a, b });
			}
		};

		mixin.mix(new SignsCalc()) //
				.rename("add", "plus").around("add", handler) //
				.rename("sub", "minus").around("sub", handler);

		Calc c = mixin.as(Calc.class);
		assertEquals(4, c.add(1, 2));
		assertEquals(0, c.sub(2, 1));
	}

}
