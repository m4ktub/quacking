package ws.m4ktub.quacking;

import static org.junit.Assert.*;

import org.junit.Test;

public class MixerTest {

	public interface Duck {
		public String speak();
	}

	public interface Noise {
		public String produce();
	}

	public static class Quacker {
		public String speak() {
			return "quack";
		}
	}
	
	public static class DuckNoise {
		private Mixin m;
		
		public DuckNoise(Mixin m) {
			super();
			this.m = m;
		}

		public String produce() {
			return m.as(Duck.class).speak() + " noise";
		}
		
	}

	public static class OtherMixer implements Mixer {
		@Override
		public Object in(Mixin mixin) {
			return new Quacker();
		}
	}

	public static class SelfMixer implements Mixer {
		@Override
		public Object in(Mixin mixin) {
			return this;
		}
	}

	public static class CollabMixer implements Mixer {
		@Override
		public Object in(Mixin mixin) {
			return new DuckNoise(mixin);
		}
	}
	
	@Test
	public void other() {
		Mixin mixin = new Mixin();
		mixin.mix(new OtherMixer());
		
		assertEquals("quack", mixin.as(Duck.class).speak());
	}

	@Test
	public void same() {
		Mixin mixin = new Mixin();
		SelfMixer mixer = new SelfMixer();
		mixin.mix(mixer);
		
		assertEquals(mixer, mixin.as(Mixer.class).in(mixin));
	}

	@Test
	public void collaborator() {
		Mixin mixin = new Mixin();
		mixin.mix(new CollabMixer());
		mixin.mix(new Quacker());
		
		assertEquals("quack noise", mixin.as(Noise.class).produce());
	}
	
}
