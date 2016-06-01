Java Quacking
=============

[![Build Status](https://travis-ci.org/m4ktub/quacking.svg)](https://travis-ci.org/m4ktub/quacking)

This is simple library helping to implement some sort of *duck typing*. Only
standard Java features are used so *duck typing* is accomplished with dynamic
proxys, reflection, and some sort of type checking.

Instead of creating adapters between interfaces, or wrappers for decoration, we
can mix instances and override methods without even implementing the interface,
which forces us to implement all it's methods (without a default implementation).

For example, in the typical situation where you want your servlets to be able to 
obtain the character encoding from the request and ensure it's never `null`, you
have to wrap the request, delegate all methods (but `getCharacterEnconding()`)
to a real request, and implement `getCharacterEnconding()` to perform what
you want.

Fortunately there is the `HttpServletRequestWrapper` which starts
by delegating everything to a real request.

```java
public class ForceCharacterEncodingWrapper extends HttpServletRequestWrapper {

    private String encoding;
    
    public ForceCharacterEncodingWrapper(HttpServletRequest request, String encoding) {
        super(request);
        this.encoding = encoding;
    }
    
    @Override
    public String getCharacterEncoding() {
        return this.encoding;
    }
    
}
```

And then, somewhere, you would wrap the real request and pass it on.

```java
HttpServletRequest wrappedRequest = new ForceCharacterEncodingWrapper(getRequest(), "UTF-8");
Assert.assertEquals("UTF-8", wrappedRequest.getCharacterEncoding());
```

By using this form of mixins you can use a more _liberal_ pattern. The object
you implement only has the method you want to override and does not even need
to implement the request's interface.

```java
public class ForceCharacterEncoding {
	
	private String encoding;
	
	public ForceCharacterEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getCharacterEncoding() {
		return this.encoding;
	}
	
}
```

Then you can mix this object in front of a real request. The resulting mixing
can be used as a `HttpServletRequest` and forwarded to the servlet.

```java
Mixin mixin = Mixins.create(new ForceCharacterEncoding("UTF-8"), getRequest());
HttpServletRequest mixedRequest = mixin.as(HttpServletRequest.class);
Assert.assertEquals("UTF-8", mixedRequest.getCharacterEncoding());
```

Note that `ForceCharacterEncoding` has no dependency on the Servlet API. Also, 
the same object could be mixed in front of a `HttpServletResponse` because the
method has the same name. If the method has a different name, web could still
mix it by renaming an interface method to the object's `getCharacterEncoding`.

```java
Mixin mixin = new Mixin();
mixin.mix(new ForceCharacterEncoding("UTF-8")).rename("toString", "getCharacterEncoding");
mixin.mix(getRequest());

HttpServletRequest mixedRequest = mixin.as(HttpServletRequest.class);
Assert.assertEquals("UTF-8", mixedRequest.toString());
```

These are trivial examples but the main idea is that you can avoid dependencies
and use mixing as a more reusable way to adapt and extend frameworks that don't
quite work together but have all the required methods just different interfaces,
names, arguments, etc.

License
-------

The library is under *The MIT License*. You can check `LICENSE` for the full
license.
