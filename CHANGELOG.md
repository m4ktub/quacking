Java Quacking Change Log
========================

The following is the list of released versions and the included changes.

Version 0.9.1
-------------

Fixed unexpected behaviors and some bugs.

  * Now implementation methods can return values for `void` interface methods.
    The returned value is simply ignored.
  * Changed `Invocation` to be static in relation with the call arguments
    and cache the invocation resolution in the mixed instance. Speeds things
    a little bit.
  * Fixed problem with compatibility of primitive types. In some situations 
    `int`, for example, was being compared with `Number` instead of stopping
    in the `Integer` wrapper type.

Version 0.9
-----------

Allow to redirect calls from an interface method to an implementation method
with a different signature (name, arguments, etc).

  * Renamed `Mixed.as(...)` to `Mixed.preferring(...)` to avoid 
    reusing the same name for the `DuckType` interface.
  * Extracted `Mixed` class to top level.
  * Added renaming currying and wraping functionalities as configurations 
    at the `Mixed` class level.
  * Changed reflection logic to handle `Mixed` wrappers instance of the
    final object instances directly.
  * Added documentation to several public methods.
  * Updated year in license.

Version 0.5
-----------

Support collaboration between mixed instances.

  * Added the `Mixer` interface.
  * An implementation can implement the Mixer interface to obtain a reference
    to the Mixin and invoke other ducklings.

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