package ws.m4ktub.quacking;

import static org.junit.Assert.*;

import org.junit.Test;

import ws.m4ktub.quacking.utils.Mixins;

public class SubTypesTest {

	public interface PrimitiveQuacking {

		boolean booleanQuacking();

		byte byteQuacking();

		char charQuacking();

		short shortQuacking();

		int intQuacking();

		long longQuacking();

		float floatQuacking();

		double doubleQuacking();

		void voidQuacking();

	}

	public interface WrapperQuacking {

		Boolean booleanQuacking();

		Byte byteQuacking();

		Character charQuacking();

		Short shortQuacking();

		Integer intQuacking();

		Long longQuacking();

		Float floatQuacking();

		Double doubleQuacking();

		Void voidQuacking();

	}

	class PrimitiveQuacker {

		public boolean booleanQuacking() {
			return false;
		}

		public byte byteQuacking() {
			return 0;
		}

		public char charQuacking() {
			return 0;
		}

		public short shortQuacking() {
			return 0;
		}

		public int intQuacking() {
			return 0;
		}

		public long longQuacking() {
			return 0;
		}

		public float floatQuacking() {
			return 0;
		}

		public double doubleQuacking() {
			return 0;
		}

		public void voidQuacking() {

		}

	}

	class WrapperQuacker {

		public Boolean booleanQuacking() {
			return false;
		}

		public Byte byteQuacking() {
			return 0;
		}

		public Character charQuacking() {
			return 0;
		}

		public Short shortQuacking() {
			return 0;
		}

		public Integer intQuacking() {
			return 0;
		}

		public Long longQuacking() {
			return 0L;
		}

		public Float floatQuacking() {
			return 0f;
		}

		public Double doubleQuacking() {
			return 0.0;
		}

		public Void voidQuacking() {
			return null;
		}

	}

	class LiarWrapperQuacker {

		public Boolean booleanQuacking() {
			return null;
		}

	}
	
	class VoidReturnQuacker {
		
		public String voidQuacking() {
			return "something";
		}
		
	}

	public interface Quack {
	}
	
	public class SonicQuack implements Quack {
		@Override
		public String toString() {
			return "sonic quack";
		}
	}

	public class SubsonicQuack implements Quack {
		@Override
		public String toString() {
			return "subsonic quack";
		}
	}

	public class StrangeQuack implements Quack {
		@Override
		public String toString() {
			return "strange quack";
		}
	}

	public interface SpecializedQuacker {
		Quack quack();
	}

	class GenericQuacker {
		public String quack() {
			return "quack";
		}
	}

	class SonicQuacker {
		public SonicQuack quack() {
			return new SonicQuack();
		}
	}

	public interface QuackListener {
		String hear(Quack quack);
	}

	public interface QuackSteroListener {
		String hear(Quack left, Quack right);
	}

	class GenericListener {
		public String hear(String quack) {
			return "listened to " + quack;
		}
	}
	
	class SpecializedListener {
		public String hear(SonicQuack quack) {
			return "sonic hear " + quack;
		}
		public String hear(SubsonicQuack quack) {
			return "subsonic hear " + quack;
		}
	}

	class StereoListener {
		public String hear(Object left, Object right) {
			return "regular hear " + left + " " + right;
		}
		public String hear(SonicQuack left, Object right) {
			return "left sonic hear " + left + " " + right;
		}
		public String hear(Object left, SubsonicQuack right) {
			return "right subsonic hear " + left + " " + right;
		}
	}

	@Test
	public void testWrapperDuckIsPrimitive() {
		Mixin mixin = Mixins.create(new WrapperQuacker());
		assertTrue(mixin.is(PrimitiveQuacking.class));
	}

	@Test
	public void testPrimitiveDuckIsWrapper() {
		Mixin mixin = Mixins.create(new PrimitiveQuacker());
		assertTrue(mixin.is(WrapperQuacking.class));
	}

	@Test
	public void testPrimitiveDuckAsWrapper() {
		Mixin mixin = Mixins.create(new PrimitiveQuacker());
		WrapperQuacking asWrapper = mixin.as(WrapperQuacking.class);

		assertEquals(asWrapper.booleanQuacking(), Boolean.valueOf(false));
		assertEquals(asWrapper.byteQuacking(), Byte.valueOf((byte) 0));
		assertEquals(asWrapper.charQuacking(), Character.valueOf((char) 0));
		assertEquals(asWrapper.doubleQuacking(), Double.valueOf(0.0));
		assertEquals(asWrapper.floatQuacking(), Float.valueOf(0.0f));
		assertEquals(asWrapper.intQuacking(), Integer.valueOf(0));
		assertEquals(asWrapper.longQuacking(), Long.valueOf(0));
		assertEquals(asWrapper.shortQuacking(), Short.valueOf((short) 0));
		assertEquals(asWrapper.voidQuacking(), null);
	}

	@Test
	public void testWrapperDuckAsPrimitive() {
		Mixin mixin = Mixins.create(new WrapperQuacker());
		PrimitiveQuacking asWrapper = mixin.as(PrimitiveQuacking.class);

		assertEquals(asWrapper.booleanQuacking(), false);
		assertEquals(asWrapper.byteQuacking(), 0);
		assertEquals(asWrapper.charQuacking(), 0);
		assertEquals(asWrapper.doubleQuacking(), 0.0, 0.0);
		assertEquals(asWrapper.floatQuacking(), 0.0f, 0.0f);
		assertEquals(asWrapper.intQuacking(), 0);
		assertEquals(asWrapper.longQuacking(), 0L);
		assertEquals(asWrapper.shortQuacking(), 0);
		asWrapper.voidQuacking();
	}

	@Test(expected = NullPointerException.class)
	public void testLiarWrapperDuckAsPrimitive() {
		Mixin mixin = Mixins.create(new LiarWrapperQuacker());
		PrimitiveQuacking asWrapper = mixin.as(PrimitiveQuacking.class);

		asWrapper.booleanQuacking();
	}
	
	@Test
	public void testVoidQuackingWithReturn() {
		Mixin mixin = new Mixin();
		mixin.mix(new VoidReturnQuacker());

		PrimitiveQuacking quacker = mixin.as(PrimitiveQuacking.class);
		quacker.voidQuacking();
	}
	
	@Test
	public void testSubtypingReturn() {
		Mixin mixin = new Mixin();
		mixin.mix(new GenericQuacker());
		mixin.mix(new SonicQuacker());

		assertTrue(mixin.is(SpecializedQuacker.class));

		SpecializedQuacker specializedQuacker = mixin.as(SpecializedQuacker.class);
		assertEquals(specializedQuacker.quack().toString(), "sonic quack");
	}

	@Test
	public void testSubtypingParameters() {
		Mixin mixin = new Mixin();
		mixin.mix(new GenericListener());
		mixin.mix(new SpecializedListener());

		QuackListener listener = mixin.as(QuackListener.class);
		assertEquals(listener.hear(new SonicQuack()), "sonic hear sonic quack");
		assertEquals(listener.hear(new SubsonicQuack()), "subsonic hear subsonic quack");
	}
	
	@Test
	public void testMultiDispatchParameters() {
		Mixin mixin = new Mixin();
		mixin.mix(new StereoListener());

		QuackSteroListener listener = mixin.as(QuackSteroListener.class);
		assertEquals(listener.hear(new SonicQuack(), new SonicQuack()), "left sonic hear sonic quack sonic quack");
		assertEquals(listener.hear(new SonicQuack(), new SubsonicQuack()), "left sonic hear sonic quack subsonic quack");
		assertEquals(listener.hear(new SubsonicQuack(), new SubsonicQuack()), "right subsonic hear subsonic quack subsonic quack");
		assertEquals(listener.hear(new StrangeQuack(), new SubsonicQuack()), "right subsonic hear strange quack subsonic quack");
		assertEquals(listener.hear(new StrangeQuack(), new SonicQuack()), "regular hear strange quack sonic quack");
	}
	
}
