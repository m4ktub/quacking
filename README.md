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
obtain the character encoding from the request and ensure it's never null, you
have to wrap the request, delegate all methods, but `getCharacterEnconding()`,
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

```
HttpServletRequest wrappedRequest = new ForceCharacterEncodingWrapper(getRequest(), "UTF-8");
Assert.assertEquals("UTF-8", wrappedRequest.getCharacterEncoding());
```

License
-------

The library is under *The MIT License*. You can check `LICENSE` for the full
license.

[1]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/filter/CharacterEncodingFilter.html