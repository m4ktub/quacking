package ws.m4ktub.quacking;

import static org.junit.Assert.*;

import java.io.Closeable;
import java.util.List;

import org.junit.Test;

public class GenericsTest {

	public interface Pitch {
	}

	public interface Volume {
	}

	public interface Quack {

	}

	public interface Hearable {

	}

	class Sound implements Volume {

		@Override
		public String toString() {
			return "sound";
		}

	}

	class QuackingSound extends Sound implements Pitch {

		@Override
		public String toString() {
			return "quack";
		}

	}

	class SimpleQuack implements Quack {
		private String description;

		public SimpleQuack(String description) {
			super();
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	class SoftQuack extends SimpleQuack {

		public SoftQuack(String description) {
			super(description);
		}

	}

	class HearableQuack extends SimpleQuack implements Hearable {

		public HearableQuack(String description) {
			super(description);
		}

	}

	public interface Quacker<Q extends Quack & Hearable, S extends Volume & Pitch, P> {
		Q quack(S sound, P properties);
	}

	class PrimitiveQuacker {
		// return type is not a quack
		public String quack(Object sound, Object properties) {
			return "primitive " + sound + " " + properties;
		}
	}

	class SoftQuacker {
		// return type is not Hearable
		public SoftQuack quack(Object sound, Object properties) {
			return new SoftQuack("soft " + sound + " " + properties);
		}
	}

	class RealQuacker {
		// return and parameter type are compatible, properties parameter is a super type
		public HearableQuack quack(QuackingSound sound, Object properties) {
			return new HearableQuack("real " + sound + " " + properties);
		}
	}
	
	class BadQuackTypeParameterQuacker<S, P> {
		@SuppressWarnings("unchecked")
		public <Q extends Quack> Q quack(S sound, P properties) {
			return (Q) new HearableQuack("generic " + sound + " " + properties);
		}
	}

	class BadInputsTypeParameterQuacker<S extends List<? extends Closeable>, P> {
		@SuppressWarnings("unchecked")
		public <Q extends HearableQuack> Q quack(S sound, P properties) {
			return (Q) new HearableQuack("generic " + sound + " " + properties);
		}
	}
	
	class GenericTypeParameterQuacker<S, P> {
		@SuppressWarnings("unchecked")
		public <Q extends Quack & Hearable> Q quack(S sound, P properties) {
			return (Q) new HearableQuack("generic " + sound + " " + properties);
		}
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void returnTypeCheck() {
		Mixin mixin = new Mixin();
		mixin.mix(new PrimitiveQuacker());
		mixin.mix(new SoftQuacker());
		mixin.mix(new RealQuacker());

		Quacker quacker = mixin.as(Quacker.class);
		assertEquals("real quack loud", quacker.quack(new QuackingSound(), "loud").toString());
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void returnGenericImplementations() {
		Mixin mixin = new Mixin();
		mixin.mix(new BadQuackTypeParameterQuacker<String, String>());
		mixin.mix(new BadInputsTypeParameterQuacker<List<Closeable>, String>());
		mixin.mix(new GenericTypeParameterQuacker<String, String>());
		
		Quacker quacker = mixin.as(Quacker.class);
		assertEquals("generic quack loud", quacker.quack(new QuackingSound(), "loud").toString());
	}
	
}
