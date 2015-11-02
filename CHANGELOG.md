Java Quacking Change Log
========================

The following is the list of released versions and the included changes.

Version 0.4
-----------

Support for generics.

  * Support for Generics and variance where possible.

Version 0.3
-----------

Dynamic dispatching and subtyping.

  * Allow the use of subtypes in return types. 
  * Allow the use of equivalent primitive and wrapper types.
  * Perform dynamic dispatch of method invocation.

Version 0.2
-----------

Java 8 compatibility.

  * Added support for Java 8 lambda expressions.
  * Keep compatibility with Java 7 (two projects).
  * Added tests for samples in documentation.

Version 0.1
-----------

Initial version of the library.

  * Mixin multiple implementations.
  * Ability to cast to any interface.
  * Be able to query an interface full implementation. 
  * Ensure a certain implementation is used for a given interface.
  * Basic support for class loaders, when creating proxies.
  * Implementation of object methods for proxies.
  * Ability to recover original mixin from proxy.