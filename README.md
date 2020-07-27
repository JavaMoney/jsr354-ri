JSR 354: Money and Currency: Moneta Reference Implementation
===========================================================

[![Maven Central](https://img.shields.io/maven-central/v/org.javamoney/moneta.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.javamoney%22%20AND%20a%3A%22moneta%22)
[![Build Status](https://api.travis-ci.org/JavaMoney/jsr354-ri.png?branch=master)](https://travis-ci.org/JavaMoney/jsr354-ri) 
[![Coverage Status](https://coveralls.io/repos/JavaMoney/jsr354-ri/badge.svg?branch=master)](https://coveralls.io/r/JavaMoney/jsr354-ri?branch=master)
[![Stability: Maintenance](https://masterminds.github.io/stability/maintenance.svg)](https://masterminds.github.io/stability/maintenance.html)
[![License](https://img.shields.io/badge/license-Apache2-red.svg)](http://opensource.org/licenses/apache-2.0)
[![Join the chat at https://gitter.im/JavaMoney/jsr354-ri](https://badges.gitter.im/JavaMoney/jsr354-ri.svg)](https://gitter.im/JavaMoney/jsr354-ri?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The Moneta is a reference implementation (RI) of the [JSR 354 Money & Currency API](http://javamoney.org) and provides:
* Monetary amounts: fixed sized `FastMoney` and `Money` for big amounts.
* Currency conversion and rate providers.
* Custom currencies support like the Bitcoin.

See [Moneta User Guide](/moneta-core/src/main/asciidoc/userguide.adoc) for an introduction.


Usage
-----

You can access the RI by adding the following Maven dependencies:
```xml
<dependency>
  <groupId>org.javamoney</groupId>
  <artifactId>moneta</artifactId>
  <version>1.4.2</version>
  <type>pom</type>
</dependency>
```

The same for Gradle:
```groovy
compile group: 'org.javamoney', name: 'moneta', version: '1.4.2', ext: 'pom'
```

SBT:
```scala
libraryDependencies += "org.javamoney" % "moneta" % "1.4.2" pomOnly()
```

The release artifacts are accessible from the following repositories:
* [Maven Central](https://mvnrepository.com/artifact/org.javamoney/moneta/) 
* [JCenter by Bintray](https://jcenter.bintray.com/org/javamoney/moneta/)

[Release notes](https://github.com/JavaMoney/jsr354-ri/releases)

The implementation supports JDK8 and later.

Help and support
----------------
Ask your question at StackOverflow with tag [java-money](https://stackoverflow.com/questions/tagged/java-money+jsr354) or join the [Gitter chat](https://gitter.im/orgs/JavaMoney/rooms).
