package ws.m4ktub.quacking;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import ws.m4ktub.quacking.utils.Mixins;

public class UtilsTest {

	public interface None {
		
	}
	
	public interface SuperDuck {
		String megaQuack();
		String superPunch();
	}
	
	public class LoudDuck {
		public String megaQuack() {
			return "mega quack";
		}
	}
	
	public class BoxerDuck {
		public String superPunch() {
			return "super punch";
		}
	}
	
	public class WonderDuck {
		public String megaQuack() {
			return "wonder quack";
		}

		public String superPunch() {
			return "wonder punch";
		}
	}
	
	@Test
	public void mixinsCreateObjects() {
		Mixin sdMix = Mixins.create(new LoudDuck(), new BoxerDuck());
		assertTrue(sdMix.is(SuperDuck.class));
		SuperDuck sd = sdMix.as(SuperDuck.class);
		assertThat(sd.megaQuack() + " " + sd.superPunch(), equalTo("mega quack super punch"));
	}

	@Test
	public void mixinsCreateObjectsInterfaces() {
		Mixin sdMix = Mixins.create(new Object[] { new WonderDuck(), new LoudDuck() }, new Class<?>[] { None.class, SuperDuck.class });
		assertTrue(sdMix.is(SuperDuck.class));
		SuperDuck sd = sdMix.as(SuperDuck.class);
		assertThat(sd.megaQuack() + " " + sd.superPunch(), equalTo("mega quack wonder punch"));
	}
	
}
