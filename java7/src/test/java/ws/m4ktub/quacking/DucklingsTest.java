package ws.m4ktub.quacking;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class DucklingsTest {

	interface DonaldDuckQuartet {
		String sailor();

		String green();

		String blue();

		String red();
	}

	class Donald {
		public String sailor() {
			return "donald";
		}
	}

	class Louie {
		public String green() {
			return "louie";
		}
	}

	class Dewey {
		public String blue() {
			return "dewey";
		}
	}

	class Huey {
		public String red() {
			return "huey";
		}
	}

	class Fethry {
		public String red() {
			return "fethry";
		}
	}

	@Test
	public void is() {
		Mixin mixin = new Mixin();
		mixin.mix(new Donald());
		mixin.mix(new Louie());
		mixin.mix(new Dewey());
		mixin.mix(new Huey());

		assertTrue(mixin.is(DonaldDuckQuartet.class));
	}

	@Test
	public void as() {
		Mixin mixin = new Mixin();
		mixin.mix(new Donald());
		mixin.mix(new Louie());
		mixin.mix(new Dewey());
		mixin.mix(new Huey());

		assertThat(mixin.as(DonaldDuckQuartet.class).sailor(), equalTo("donald"));
		assertThat(mixin.as(DonaldDuckQuartet.class).green(), equalTo("louie"));
		assertThat(mixin.as(DonaldDuckQuartet.class).blue(), equalTo("dewey"));
		assertThat(mixin.as(DonaldDuckQuartet.class).red(), equalTo("huey"));
	}

	@Test
	public void asOrder() {
		Mixin mixin = new Mixin();
		mixin.mix(new Donald());
		mixin.mix(new Louie());
		mixin.mix(new Dewey());
		mixin.mix(new Fethry()); // added before Huey
		mixin.mix(new Huey());

		assertThat(mixin.as(DonaldDuckQuartet.class).red(), equalTo("fethry"));
	}

	@Test
	public void asas() {
		Mixin mixin = new Mixin();
		mixin.mix(new Donald());
		mixin.mix(new Louie());
		mixin.mix(new Dewey());
		mixin.mix(new Huey());
		mixin.mix(new Fethry()).as(DonaldDuckQuartet.class); // added before Huey

		assertThat(mixin.as(DonaldDuckQuartet.class).red(), equalTo("fethry"));
	}

}
