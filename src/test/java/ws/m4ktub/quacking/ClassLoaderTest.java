package ws.m4ktub.quacking;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ClassLoaderTest {

	class DuckList {

		public int size() {
			return 2;
		}

	}

	@Test
	public void boostrap() {
		Mixin mixin = new Mixin(new DuckList());
		assertThat(mixin.as(List.class).size(), equalTo(2));
	}

}
