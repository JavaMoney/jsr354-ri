JSR 354: Money and Currency: Moneta Reference Implementation
===========================================================

[![Maven Central](https://img.shields.io/maven-central/v/org.javamoney/moneta.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.javamoney%22%20AND%20a%3A%22moneta%22)
[![Build Status](https://api.travis-ci.org/JavaMoney/jsr354-ri.png?branch=master)](https://travis-ci.org/JavaMoney/jsr354-ri) 
[![Coverage Status](https://coveralls.io/repos/JavaMoney/jsr354-ri/badge.svg?branch=master)](https://coveralls.io/r/JavaMoney/jsr354-ri?branch=master)
[![License](https://img.shields.io/badge/license-Apache2-red.svg)](http://opensource.org/licenses/apache-2.0)
[![Join the chat at https://gitter.im/JavaMoney/jsr354-ri](https://badges.gitter.im/JavaMoney/jsr354-ri.svg)](https://gitter.im/JavaMoney/jsr354-ri?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[JSR 354 JavaMoney](https://jcp.org/en/jsr/detail?id=354) provides an API for representing, transporting, and performing comprehensive calculations with Money and Currency. 
This module (moneta) implements the JSR 354 Money & Currency. Hereby basic implementations of amounts, currency and roundings 
are provided.
The library supports JDK8 and later but also is available a limited backport to JDK7 [jsr354-ri-bp](https://github.com/JavaMoney/jsr354-ri-bp).


Usage
-----

You can access the RI by adding the following Maven dependencies:
```xml
<dependency>
  <groupId>org.javamoney</groupId>
  <artifactId>moneta</artifactId>
  <version>1.2.1</version>
  <type>pom</type>
</dependency>
```

The same for Gradle:
```groovy
compile group: 'org.javamoney', name: 'moneta', version: '1.2.1', ext: 'pom'
```

SBT:
```scala
libraryDependencies += "org.javamoney" % "moneta" % "1.2.1" pomOnly()
```

The release artifacts are accessible from the following repositories:
* [Maven Central](https://mvnrepository.com/artifact/org.javamoney/moneta/) 
* [JCenter by Bintray](https://jcenter.bintray.com/org/javamoney/moneta/)


Then refer to [Moneta User Guide](/moneta-core/src/main/asciidoc/userguide.adoc)


Release Notes
-------------

- [All release notes](https://github.com/JavaMoney/jsr354-ri/releases)
- *1.0*    First release along with with JSR 354 API
- *1.1*    Bugfix release.
- *1.2*    Modularized release (with separated conversion and conversion providers), Java 9 module support
- *1.2.1*  Fix release for invalid/missing Java 9 descriptor for conversion base. Compatible with Java 8, 9 and beyond.

Help and support
----------------
Ask your question at StackOverflow with tag [java-money](https://stackoverflow.com/questions/tagged/java-money+jsr354) or join the [Gitter chat](https://gitter.im/orgs/JavaMoney/rooms).
