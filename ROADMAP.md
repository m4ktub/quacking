Java Quacking Roadmap
=====================

These are the features planned for the Java Quacking library up to the 1.0 
version. When the library reaches that version number then it's free for
all and features will be implemented as the wind blows under the wings of
quacking ducks.

Version 1.0
-----------

  * Support Java 8 Lambda Expressions as implementations.
  * Support for Generics and variance where possible.
  * Allow the use of subtypes in return types. 
  * Allow the use of equivalent primitive and wrapper types.
  * Allow to rename calls. Calling method named A can actually call method
    named B in the implementation.
  * Allow to curry implementation methods. A call for method A with N arguments
    can be implemented by a method B with more arguments.
  * Allow before, after and around methods before dispatch.
  * Collaboration between mixed instances. A mixed instance can receive the 
    mixing and call other implementations through the mixin.
