package ws.m4ktub.quacking;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

public class ClassLoaderTest {

	static class CustomClassLoader extends ClassLoader {

		public CustomClassLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			// load other classes in parent 
			if (!name.startsWith(ClassLoaderTest.class.getName())) {
				return super.loadClass(name, resolve);
			}
			
			// load quacking classes in this class loader
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			
			try {
				String file = "/" + name.replace('.', '/') + ".class";
				URL resource = ClassLoaderTest.class.getResource(file);
				try (BufferedInputStream stream = new BufferedInputStream(resource.openStream())) {
					int data;
					while ((data = stream.read()) != -1) {
						bytes.write(data);
					}
				}
			} catch (IOException ex) {
				throw new ClassNotFoundException("failed to read class bytes", ex);
			}
			
			return defineClass(name, bytes.toByteArray(), 0, bytes.size());
		}

	}

	public static class DuckList {

		public int size() {
			return 42;
		}

	}
	
	public static class CustomTest {
		
		interface CustomList {
			int size();
		}

		public static void test() {
			Mixin mixin = new Mixin(new DuckList());
			assertThat(mixin.as(CustomList.class).size(), equalTo(42));
		}

	}

	@Test
	public void boostrap() {
		Mixin mixin = new Mixin(new DuckList());
		assertThat(mixin.as(List.class).size(), equalTo(42));
	}

	@Test
	public void custom() throws Exception {
		CustomClassLoader loader = new CustomClassLoader(ClassLoaderTest.class.getClassLoader());
	    Class<?> duckMyListClass = loader.loadClass(CustomTest.class.getName());
	    duckMyListClass.getMethod("test", new Class[0]).invoke(null, new Object[0]);
	}

}
