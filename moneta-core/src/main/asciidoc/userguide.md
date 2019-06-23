# JavaMoney 'Moneta' User Guide
Author: Anatole Tresch <atsticks@gmail.com>

'Moneta' is an implementation of the JSR 354 'Java Money API'. The API is separated
so also other can provide their own implementations. This document will
mainly focus on the overall library usage from a user's perspective, when using 'Moneta'. Normally this document
will not explicitly differentiate between the JSR 354 API and this implementation, unless it is useful for the
common understanding.

## This document
This is a user guide that describes all relevant aspects of
Java Money, for using this API along with the 'Moneta' reference implementation.

For a shorter introduction you may check out the quick start guide (tbd).


## Introduction to Java Money

Java Money is a initiative lead by Credit Suisse to standardize monetary aspects in Java. The main part hereby is
JSR 354, which defines the money and currency API covering currencies, monetary amounts, rounding, currency conversion
and formatting. _Moneta_ is the JSR 354 reference implementation, also adding some additional aspects like
extended Lambda-Support and multiple amount implementation classes. Additionally there is the JavaMoney OSS library,
which contains additionally financial calculations and formulas, additional currency mapping, regions, historic
currencies, currency/region mapping and last but not least EE/CDI support. Below given the most important links:

* [JSR 354 API specification](https://jcp.org/en/jsr/detail?id=354).
* [JSR 354 on GitHub](https://github.com/jsr354-api).
* JavaMoney Umbrella Site: http://javamoney.org

Basically the API of JSR 354 provides the following packages:

* `javax.money` contains the main artifacts, such as `CurrencyUnit, MonetaryAmount, MonetaryContext, MonetaryOperator, MonetaryQuery, MonetaryRounding`, and the singleton accessors `MonetaryCurrencies, MonetaryAmounts, MonetaryRoundings`.
* `javax.money.convert` contains the conversion artifacts `ExchangeRate, ExchangeRateProvider, CurrencyConversion` and the according `MonetaryConversions` accessor singleton.
* `javax.money.format` contains the formatting artifacts `MonetaryAmountFormat, AmountFormatContext` and the according `MonetaryFormats` accessor singleton.
* `javax.money.spi` contains the SPI interfaces provided by the JSR 354 API and the bootstrap logic, to support
different runtime environments and component loading mechanisms.

Basically the JSR 354 API is complete, meaning users won't have to reference anything other than what is already part of
the JSR's API. As a consequence this reference implementation contains mostly components that are registered into the
API using the JSR's SPI mechanism. Only a few additions to the API are done, e.g. singletons providing Lambda-supporting
methods (`MonetaryFunctions`).


## Working with Currency Units
### Accessing Currency Units

Basically access to  currency units is based on the `javax.money.MonetaryCurrencies` singleton. Hereby you can access
currencies in different ways:

#### Access currencies by currency code

You can use the currency code to access currencies.

_Accessing currencies by currency code_
```java
CurrencyUnit currencyCHF = Monetary.getCurrency("CHF");
CurrencyUnit currencyUSD = Monetary.getCurrency("USD");
CurrencyUnit currencyEUR = Monetary.getCurrency("EUR");
```

Hereby all codes available from `java.util.Currency` in the underlying JDK are mapped by default.

#### Access currencies by Locale

You can use `java.util.Locale` to access currencies. Hereby the `Locale` instance, represents a
country. All available countries can be accessed by calling `Locale.getISOCountries()`. With the
given ISO country code a corresponding `Locale` can be created:

```java
String isoCountry = "USA";
Locale country = new Locale("", isoCountry);
```

Similarly to `java.util.Currency` a `CurrencyUnit` can be accessed using this `Locale`:

_Accessing currencies by Locale_
```java
CurrencyUnit currencyCHF = Monetary.getCurrency(new Locale("", "SUI")); // Switzerland
CurrencyUnit currencyUSD = Monetary.getCurrency(new Locale("", "USA")); // United States of America
CurrencyUnit currencyEUR = Monetary.getCurrency(new Locale("", "GER")); // Germany
```

Hereby all codes available in the underlying JDK are mapped by default.

#### Accessing all currencies

Also all currently known currencies can be accessed:

_Accessing all currencies_
```java
Collection<CurrencyUnit> allCurrencies = Monetary.getCurrencies();
```

Similarly to other access methods you can also explicitly specify the provider chain to be used. The _Moneta_
reference implementation provides the following currency providers:

* `default`: this currency provider (implemented by `org.javamoney.moneta.internal.JDKCurrencyProvider`) simply maps/adapts `java.util.Currency`.
* `ConfigurableCurrencyUnitProvider` (implemented by `org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider`)
  provides a configuration hook for programmatically add instances. This provider is autoconfigured. Ir provides
  static hooks for adding additional `CurrencyUnit` instances:

_Example of registering `CurrencyUnit` instances programmatically._
```java
 /**
 * Registers a bew currency unit under its currency code.
 * @param currencyUnit the new currency to be registered, not null.
 * @return any unit instance registered previously by this instance, or null.
 */
public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit);

/**
 * Registers a bew currency unit under the given Locale.
 * @param currencyUnit the new currency to be registered, not null.
 * @param locale the Locale, not null.
 * @return any unit instance registered previously by this instance, or null.
 */
public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit, Locale locale);

/**
 * Removes a CurrencyUnit.
 * @param currencyCode the currency code, not null.
 * @return any unit instance removed, or null.
 */
public static CurrencyUnit removeCurrencyUnit(String currencyCode);

/**
 * Removes a CurrencyUnit.
 * @param locale the Locale, not null.
 * @return  any unit instance removed, or null.
 */
public static CurrencyUnit removeCurrencyUnit(Locale locale);
```

The API is straightforward so far. For most cases the `BuildableCurrencyUnit` class can be used to create additional
currency instances that then can be registered using the static methods:

#### Registering Additional Currency Units

For adding additional CurrencyUnit instances to the `MonetaryCurrencies` singleton, you must implement an instance
of `CurrencyProviderSpi`. Following a minimal example, hereby also using the `BuildableCurrencyUnit` class, that
also provides currencies for Bitcoin:

_Implementing a Bitcoin currency provider_
```java
public final class BitCoinProvider implements CurrencyProviderSpi {

    private Set<CurrencyUnit> bitcoinSet = new HashSet<>();

    public BitCoinProvider() {
       bitcoinSet.add(CurrencyUnitBuilder.of("BTC", "MyCurrencyBuilder").build());
       bitcoinSet = Collections.unmodifiableSet(bitcoinSet);
    }

    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link javax.money.CurrencyQuery}.
     *
     * @param query the {@link javax.money.CurrencyQuery} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}s matching, never null.
     */
    @Override
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery query) {
       // only ensure BTC is the code, or it is a default query.
       if (query.isEmpty()
           || query.getCurrencyCodes().contains("BTC")
           || query.getCurrencyCodes().isEmpty()) {
           return bitcoinSet;
       }
       return Collections.emptySet();
    }

}
```

By default, the `BitCoinProvider` class must be configured as service to be loadable by `java.util.ServiceLoader`.
This can be achieved by adding a file `META-INF/services/javax.money.spi.CurrencyProviderSpi` with the following content
to your classpath:

_Contents of `META-INF/services/javax.money.spi.CurrencyProviderSpi`_
```properties
# assuming the class BitCoinProvider is in the package my.fully.qualified
my.fully.qualified.BitCoinProvider
```

Alternatively, if the JSR's `Bootstrap` logic uses CDI, it would also be possible to register the provider class as
normal CDI bean, e.g.

_Implementing a Bitcoin currency provider_
```java
@Singleton
public class BitCoinProvider implements CurrencyProviderSpi {
  ...
}
```

Now given this example it is obvious that the tricky part is to define, when exactly a given `CurrencyQuery`
should be targeted by this provider, or otherwise, be simply ignored. Our case just provides an additional
currency code, so it is a good idea to just only return data for _default_ query types. Additionally we only return our code
sublist, when the according code is requested, or a unspecified request is performed.


#### Building Custom Currency Units

You can use the MonetaryCurrencies static methods to register currencies as follows.

_Example of registering `CurrencyUnit` instances programmatically._
```java
CurrencyUnit unit = CurrencyUnitBuilder.of("FLS22", "MyCurrencyProvider")
    .setDefaultFractionDigits(3)
    .build();

// registering it
Monetary.registerCurrency(unit);
Monetary.registerCurrency(unit, Locale.MyCOUNTRY);
```

Fortunately `CurrencyUnitBuilder` is also capable of registering a currency on creation, by just passing
a register flag to the call: So the same can be rewritten as follows:

_Example of registering `CurrencyUnit` instances programmatically, using `CurrencyUnitBuilder`._
```java
CurrencyUnitBuilder.of("FLS22", "MyCurrencyProvider")
    .setDefaultFractionDigits(3)
    .build(true /* register */);
```

#### Provided Currencies

_Moneta_, by default provides only the same currencies as defined by `java.util.Currency`. Use the extended currency
module from the JavaMoney OSS library for additional currency support, e.g. current overloading of currencies
based on the actual input from the online ISO-4217 resources.

### Monetary Amounts

Monetary amounts are the key abstraction of JSR 354. _Moneta_ hereby provides different implementations of amounts:

* `Money` represents a effective implementation, which is based on `java.math.BigDecimal` internally for
  performing the arithmetic operations. The implementation is capable of supporting arbitrary precision
  and scale.
* `FastMoney` represents numeric representation that was optimized for speed. It represents a monetary amount only
  as a integral number of type `long`, hereby using a number scale of 100'000 (10^5).
* `RoundedMoney` finally provides an amount implementation that is implicitly rounded after each operation.

#### Choosing an Implementation

Basically, if the numeric capabilities of `FastMoney` are sufficient for your use cases, you may use this type. If
not sure, using `Money` is in general safe. `RoundedMoney` should only be used, if you are well aware of its usage,
since the immediate rounding may produce unwanted side effects (invalid values).

#### Creating new Amounts

As defined by the JSR's API you can access according `MonetaryAmountFactory` for all types listed above to create
new instances of amounts. E.g. instances of `FastMoney` can be created as follows:

_Creating instances of `FastMoney` using the `Monetary` singleton:_
```java
FastMoney m = Monetary.getAmountFactory(FastMoney.class).setCurrency("USD").setNumber(200.20).create();
```

Additionally _Moneta_ also supports static factory methods on the types directly. So the following code is equivalent:

_Creating instances of `FastMoney` using the static factory method:_
```java
FastMoney m = FastMoney.of(200.20, "USD");
```

Creation of `Money` instances is similar:

_Creating instances of `Money`:_
```java
Money m1 = Monetary.getAmountFactory(Money.class).setCurrency("USD").setNumber(200.20).create();
Money m2 = Money.of(200.20, "USD");
```

##### Configuring Instances of Money

The `Money` class is internally based on `java.math.BigDecimal`. Therefore the arithmetic precision and rounding
capabilities of `BigDecimal` are also usable with `Money`. Hereby, by default, instances
of `Money` internally are initialized with `MathContext.DECIMAL64`. Nevertheless instance also can be configured
explicitly by passing a `MathContext` as part of a `MonetaryContext`:

_Creating instances of `Money` configuring the `MathContext` to be used._
```java
Money money = Money.of(200, "CHF", MonetaryContextBuilder.of().set(MathContext.DECIMAL128).build());
```

Using the JSR's main API allows to achieve the same as follows:

_Creating instances of `Money` configuring the `MathContext` to be used, using the `MonetaryAmountFactory`._
```java
Money money = Monetary.getAmountFactory(Money.class)
                              .setCurrencyUnit("CHF").setNumber(200)
                              .setContext(MonetaryContextBuilder.of().set(MathContext.DECIMAL128).build())
                              .create();
```

Additionally the default `MathContext` can be configured with the `javamoney.properties` located in your classpath:

_Configuring the default `MathContext` to be used for `Money`._
```properties
org.javamoney.moneta.Money.defaults.mathContext=DECIMAL128
```

Alternatively you also can configure the precision and `RoundingMode` to be used:

_Configuring the default `MathContext` to be used for `Money` (alternative)._
```properties
org.javamoney.moneta.Money.defaults.precision=DECIMAL128
org.javamoney.moneta.Money.defaults.roundingMode=HALF_EVEN
```

#### Configuring Internal Rounding of FastMoney

The class `FastMoney` internally uses a single `long` value to model a monetary amount. Hereby it uses a fixed scale of
5 digits. Obviously this may require rounding in some cases. Hereby by default `FastMoney` rounds input values (of type
`MonetaryAmount`, or numbers) to its internal 5 digits scale. In most cases that makes sense and makes use of
this class easy and straight forward. Nevertheless there might be scenarios, where you want to throw
`ArithmeticException` if an entry value exceeds the maximal scale. This alternate, more rigid behaviour, can be
activated by adding the following configuration to `javamoney.properties`:

_Activating strict input number validation for `FastMoney`_
```properties
org.javamoney.moneta.FastMoney.enforceScaleCompatibility=true
```


#### Registering Additional Amount Implementations

By default, additional implementation classes are added, by registering an instance of
`MonetaryAmountFactoryProviderSpi` as JDK services loaded by `java.util.ServiceLoader`.
For this you have to add the following contents to `META-INF/services/javax.money.spi.MonetaryAmountFactoryProviderSpi`:

_Providing custom monetary amount implementations_
```properties
my.fully.qualified.MonetaryAmountFactoryProviderImplClass
```

For further ease of use, your implementations may furthermore provide static factory methods, e.g.

_Static factory methods of the custom monetary amount implementation:_
```java
public static MyMoney of(String currencyCode, double number);
public static MyMoney of(String currencyCode, long number);
public static MyMoney of(String currencyCode, Number number);
```

Hereby several commonly used functionality can be reused from the moneta RI, e.g. safe conversion of any JDK number type
to `BigDecimal` is available on `MoneyUtils`, along with additional helpful methods.


#### Mixing Amount Implementation Types

Basically the JSR supports mixing of different implementation types. Nevertheless there are some effects that are
important to mention, if doing so:

* the performance may decrease based on the slower implementation used. Hereby the type used as a base type (the
  type on which the operations are performed), is the type that basically determines overall performance.
* mixing of different amount implementation types may require internal rounding to be performed. Whereas the
  compatibility of precision is ensured, scale may be reduced silently as needed.

Nevertheless there are strategies to mitigate these possible issues. The most easy and obvious strategy hereby is
simply *converting explicitly to the required target type, before performing any operations*. This can
be easily achieved, since every implementation in _moneta_ provides corresponding static `from()` methods:

_Using the custom monetary amount implementation with `Money`:_
```java
MyMoney money1;
Money money = Money.from(myMoney);
FastMoney fastMoney = FastMoney.from(myMoney);

money = Money.from(fastMoney);
fastMoney = FastMoney.from(money);
```

In the above example, as long as the scale of 5 is never exceeded, no implicit rounding is performed. Bigger scales
require rounding, when creating new instances of `FastMoney`.


#### Other utility functions

The _moneta_ reference implementation also provides implementations for several commonly used simple monetary functions
in the `org.javamoney.moneta.functions` package:

* `MonetaryUtil.reciprocal()` provides an operator for calculating the reciprocal value of an amount (1/amount).
* `MonetaryUtil.permil(BigDecimal decimal), MonetaryUtil.permil(Number number), MonetaryUtil.permil(Number number, MathContext mathContext)` provides an operator for calculating permils.
* `MonetaryUtil.percent(BigDecimal decimal), MonetaryUtil.percent(Number number)` provides an operator for calculating percentages.
* `MonetaryUtil.minorPart()` provides an operator for extracting only the minor part of an amount.
* `MonetaryUtil.majorPart()` provides an operator for extracting only the major part of an amount.
* `MonetaryUtil.minorUnits()` provides a query for extracting only the minor units of an amount.
* `MonetaryUtil.majorUnits()` provides a query for extracting only the major units of an amount.

Additionally several aggregate functions are provided on `MonetaryFunctions`, they are specially useful
when combined with the new Java 8 Lambda/Streaming features:

* `Collector<MonetaryAmount, ?, Map<CurrencyUnit, List<MonetaryAmount>>> groupByCurrencyUnit()` provides a `Collector` to group by `CurrencyUnit`.
* `Collector<MonetaryAmount, MonetarySummaryStatistics, MonetarySummaryStatistics> summarizingMonetary()` create the summary of the `MonetaryAmount`.
* `Collector<MonetaryAmount, GroupMonetarySummaryStatistics, GroupMonetarySummaryStatistics> groupBySummarizingMonetary()` create `MonetaryAmount` group by MonetarySummary.
* `Comparator<MonetaryAmount> sortCurrencyUnit()` get a comparator for sorting currency units ascending.
* `Comparator<MonetaryAmount> sortCurrencyUnitDesc()` get a comparator for sorting currency units descending.
* `Comparator<MonetaryAmount> sortNumber()` ` access a comparator for sorting amount by number value ascending.
* `Comparator<MonetaryAmount> sortNumberDesc()` access a comparator for sorting amount by number value descending.
* `Predicate<MonetaryAmount> isCurrency(CurrencyUnit currencyUnit)` creates a predicate that filters by `CurrencyUnit`.
* `Predicate<MonetaryAmount> isNotCurrency(CurrencyUnit currencyUnit)` creates a predicate that filters by `CurrencyUnit`.
* `Predicate<MonetaryAmount> containsCurrencies(CurrencyUnit requiredUnit, CurrencyUnit... otherUnits)` creates a filtering predicate based on the given currencies.
* `Predicate<MonetaryAmount> isGreaterThan(MonetaryAmount amount)` creates a filter using `MonetaryAmount.isGreaterThan`.
* `Predicate<MonetaryAmount> isGreaterThanOrEqualTo(MonetaryAmount amount)` creates a filter using `MonetaryAmount.isGreaterThanOrEqualTo`.
* `Predicate<MonetaryAmount> isLessThan(MonetaryAmount amount)` creates a filter using `MonetaryAmount.isLess`.
* `Predicate<MonetaryAmount> isLessThanOrEqualTo(MonetaryAmount amount)` creates a filter using `MonetaryAmount.isLessThanOrEqualTo`.
* `Predicate<MonetaryAmount> isBetween(MonetaryAmount min, MonetaryAmount max)` creates a filter using the isBetween predicate.
* `MonetaryAmount sum(MonetaryAmount a, MonetaryAmount b)` adds two monetary together.
* `MonetaryAmount min(MonetaryAmount a, MonetaryAmount b)` returns the smaller of two `MonetaryAmount` values. If the arguments have the same value, the result is that same value.
* `MonetaryAmount max(MonetaryAmount a, MonetaryAmount b)` returns the greater of two `MonetaryAmount` values. If the arguments have the same value, the result is that same value.
* `BinaryOperator<MonetaryAmount> sum()` Creates a BinaryOperator to sum.
* `BinaryOperator<MonetaryAmount> min()` creates a BinaryOperator to calculate the minimum amount
* `BinaryOperator<MonetaryAmount> max()` creates a BinaryOperator to calculate the maximum amount.

#### Performance Aspects

Performance was not measured in deep. Nevertheless we have a simple test in place, which is executed during all
component test runs, which performs different monetary operations on the different implementation types provided:

_Simple Performance Test Code_
```java
M money1 = money1.add(M.of(EURO, 1234567.3444));
money1 = money1.subtract(M.of(EURO, 232323));
money1 = money1.multiply(3.4);
money1 = money1.divide(5.456);
money1 = money1.with(Monetary.getRounding());
```

All tests were executed on a notebook with an `Intel i7 2.6GHz` processor with SSD.
The VM was not configured in any special way.

This test is executed 100000 times for each monetary amount class `M`:

_Performance Test Results for monetary arithmetic, no implementation mix_
```properties
Duration for 100000 operations (Money,BD): 2107 ms (21 ns per loop) -> EUR 1657407.95
Duration for 100000 operations (FastMoney,long): 1011 ms (10 ns per loop) -> EUR 1657407.95000
```

The same test is also done, hereby mixing different implementation types. Also this test is executed 100000 times for
each monetary amount class `M`:

_Performance Test Results for monetary arithmetic, mixing implementations_
```properties
Duration for 100000 operations (FastMoney/Money mixed): 899 ms (8 ns per loop) -> EUR 1657407.95000
Duration for 100000 operations (Money/FastMoney mixed): 1883 ms (18 ns per loop) -> EUR 1657407.95
```


### Rounding

_Moneta_ provides different roundings, all accessible from the `MonetaryRoundings` singleton.

#### Arithmetic Roundings

You can acquire instances of arithmetic roundings by passing the target scale and `RoundingMode` to be used within
the `RoundingQuery` passed:

_Access and apply arithmetic rounding._
```java
MonetaryRounding rounding = Monetary.getRounding(
                               RoundingQueryBuilder.of().setScale(4).set(RoundingMode.HALF_UP).build());
MonetaryAmount amt = ...;
MonetaryAmount roundedAmount = amt.with(rounding);
```

#### Default Roundings

Also a _default_ `MonetaryRounding` can be accessed, which basically falls back to the according _default_ rounding
based on the current amount instance to be rounded:

_Access and apply default rounding._
```java
MonetaryRounding rounding = Monetary.getDefaultRounding();
MonetaryAmount amt = ...;
MonetaryAmount roundedAmount = amt.with(rounding); // implicitly uses Monetary.getRounding(CurrencyUnit);
```

Also you can access the default rounding for a given `CurrencyUnit`. By default this will return an arithmetic rounding
based on the currency's _default fraction digits_, but it may also return a non standard rounding, where useful.

_Access and apply default currency rounding._
```java
CurrencyUnit currency = ...;
MonetaryRounding rounding = Monetary.getRounding(currency);
MonetaryAmount amt = ...;
MonetaryAmount roundedAmount = amt.with(rounding); // uses Monetary.getRounding(CurrencyUnit);
```

For Swiss Francs also a corresponding cash rounding is accessible. In Switzerland the smallest minor in cash are
5 Rappen, so everything must be rounded to minors dividable by 5. This rounding can be accessed by setting the
`cashRounding=true` property, when accessing a currency rounding for CHF:

_Access Swiss Francs Cash Rounding_
```java
MonetaryRounding rounding = Monetary.getRounding(Monetary.getCurrency("CHF"),
  RoundingQueryBuilder.of().set("cashRounding", true).build()
);
MonetaryAmount amt = ...;
MonetaryAmount roundedAmount = amt.with(rounding); // amount rounded in CHF cash rounding
```

#### Register your own Roundings

You can add additional roundings by registering instances of `RoundingProviderSpi`. Be default this has to be done
based on the mechanism as defined by the Java `ServiceLoader`.

_Implement a `RoundingProviderSpi` providing a currency rounding for "BTC" (Bitcoin)_
```java
public final class TestRoundingProvider implements RoundingProviderSpi {

    private static final MonetaryRounding ROUNDING = new MyCurrencyRounding();

    private final Set<String> roundingNames;

    public TestRoundingProvider() {
        Set<String> names = new HashSet<>();
        names.add("custom1");
        this.roundingNames = Collections.unmodifiableSet(names);
    }

    @Override
    public MonetaryRounding getRounding(RoundingQuery roundingQuery) {
        CurrencyUnit cu = roundingQuery.getCurrency();
        if (cu != null && "BTC".equals(cu.getCurrencyCode())) {
            return ROUNDING;
        }
        return null;
    }

    @Override
    public Set<String> getRoundingNames() {
        return Collections.emptySet();
    }

}
```


## Currency Conversion

### Basics

Basically converting of amounts into other currencies is based on the concept of `MonetaryOperator`, which transforms
an amount into another amount (of the same implementation type). A conversion hereby is based on `ExchangeRate`
that defines the transformation between amount A in currency Ca to amount B in currency Cb.

Hereby exchange rates can be accessed through an instanceof `ExchangeRateProvider`, which can be accessed from
the `MonetaryConversions` singleton:

_Access an `ExchangeRateProvider` and get an `ExchangeRate`_
```java
ExchangeRateProvider rateProvider = MonetaryConversions.getExchangeRateProvider("IMF");
ExchangeRate chfToUsdRate = rateProvider.getExchangeRate("CHF", "USD");
```

As you see above we can access a provider by passing its (unique) name. But we can also combine multiple providers
to an compound provider, by passing a chain of provider names. This defines the chain of providers to be used
to evaluate a rate required. By default, the first result returned by a provider in the chain is returned. So if we
want to use the "ECB" provider first and only use the "IMF" provider for currencies not covered by the "ECB" provider
we can write the following code:

_Access a compound `ExchangeRateProvider` and get an `ExchangeRate`_
```java
ExchangeRateProvider rateProvider = MonetaryConversions.getExchangeRateProvider("ECB", "IMF");
ExchangeRate eurToChfRate = rateProvider.getExchangeRate("EUR", "CHF");
```

Finally we can also omit the definition of a provider chain. This will use the default provider chain:

_Access an `ExchangeRate` using the default provider chain_
```java
ExchangeRateProvider rateProvider = MonetaryConversions.getExchangeRateProvider();
ExchangeRate eurToChfRate = rateProvider.getExchangeRate("EUR", "CHF");
```

#### Extracting a CurrencyConversion

A `CurrencyConversion` extends `MonetaryOperator` and is therefore directly applicable on every `MonetaryAmount`.
Hereby a `CurrencyConversion` instance is always bound to a terminating currency and an underlying `ExchangeRateProvider`.
As a consequence each `ExchangeRateProvider` allows to get a `CurrencyConversion` instance by passing the terminating
currency:

_Getting a `CurrencyConversion` from an `ExchangeRateProvider`_
```java
ExchangeRateProvider rateProvider = MonetaryConversions.getExchangeRateProvider();
CurrencyConversion conversion = rateProvider.getCurrencyConversion("CHF");

MonetaryAmount amountInUSD = ...;
MonetaryAmount amountInCHF = amountInUSD.with(conversion);
```


### Exchange Rate Providers

_Moneta_ provides quite powerful conversion providers, which allows you to perform currency conversion for most commonly used
currencies, in some cases event back until 1995:

* `ECB` connects to the online resources of the European Central Bank, which provides daily exchange rates related
  to EURO.
* `ECB-HIST90` connects the historic currencies feed of the European Central Bank, which provides exchange rates back
  for the last 90 days.
* `ECB-HIST` connects the historic currencies feed of the European Central Bank, which provides exchange rates back
  until 1999.
* `IMF` connects to the data-feed of the International Monetary Fund, which provides daily exchange rates for
almost all important currencies. Hereby the IMF feeds are internally build up as derived rates, since IMF
provides data using the intermediate `SDR` currency unit.
* `IDENT` provides rates with a factor of 1.0, where base and target currency are the same.

By default the chain of rate providers is configured as `IDENT,ECB,IMF,ECB-HIST,ECB-HIST90`. As defined by the JSR the conversion
provider chain can be configured in `javamoney.properties` as follows:

Overriding the conversion provider chain
```properties
#Currency Conversion
conversion.default-chain=IDENT,ECB,IMF,ECB-HIST,ECB-HIST90
```

#### Configuring the Exchange Rate Providers

The exchange rate providers provided provide several options to be configured, especially also the locations of
data feeds and the (re)load/update settings:

_Configuring the provided exchange rate providers_
```properties
# ResourceLoader-Configuration (optional)
# ECB Rates
load.ECBCurrentRateProvider.type=SCHEDULED
load.ECBCurrentRateProvider.period=03:00
load.ECBCurrentRateProvider.resource=/java-money/defaults/ECB/eurofxref-daily.xml
load.ECBCurrentRateProvider.urls=https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml

load.ECBHistoric90RateProvider.type=SCHEDULED
load.ECBHistoric90RateProvider.period=03:00
#load.ECBHistoric90RateProvider.at=12:00
load.ECBHistoric90RateProvider.resource=/java-money/defaults/ECB/eurofxref-hist-90d.xml
load.ECBHistoric90RateProvider.urls=https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml

load.ECBHistoricRateProvider.type=SCHEDULED
load.ECBHistoricRateProvider.period=24:00
load.ECBHistoricRateProvider.delay=01:00
load.ECBHistoricRateProvider.at=07:00
load.ECBHistoricRateProvider.resource=/java-money/defaults/ECB/eurofxref-hist.xml
load.ECBHistoricRateProvider.urls=https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml

# IMF Rates
load.IMFRateProvider.type=SCHEDULED
load.IMFRateProvider.period=06:00
#load.IMFRateProvider.delay=12:00
#load.IMFRateProvider.at=12:00
load.IMFRateProvider.resource=/java-money/defaults/IMF/rms_five.xls
load.IMFRateProvider.urls=https://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y
```


## Formatting Monetary Amounts

`MonetaryAmountFormat` instances can be accessed from the `MonetaryFormats` singleton. Similar to the Java
platform, formats can be accessed by passing a country `Locale`. But JSR 354 also supports accessing formats by
a (unique) name or even given a complex query, that allows to pass any number of parameters to configure the
format to use. In contrast to DecimalFormat, the JSR 354 formats are thread-safe and immutable.

_Accessing Amount Formats_
```java
MonetaryAmountFormat formatCountry = MonetaryFormats.getAmountFormat(Locale.GERMANY);
MonetaryAmountFormat formatNamed = MonetaryFormats.getAmountFormat("MyCustomFormat");
MonetaryAmountFormat formatQueried = MonetaryFormats.getAmountFormat(
  AmountFormatQueryBuilder.of("MyCustomFormat2")
    .set("strict", true)
    .set("omitNegative", true)
    .set("omitNegativeSign", "N/A")
    .build()
);
```

Given a `MonetaryAmountFormat` instance we can use it to format amounts:

```java
MonetaryAmountFormat format = ...;
MonetaryAmount amount = ...;
String formattedString = format.format(amount);
```

Basically a `MonetaryAmountFormat` instance can also reverse the operation by parsing an amount back:

```java
MonetaryAmountFormat format = ...;
String formattedString = ...;
MonetaryAmount amount = format.parse(formattedString);
```

**NOTE:** Be aware that parsing back an amount in a reverse operation may not always work. If a formatter implements
      only a unidirectional formatting operation, a `MonetaryParseException` will be thrown.


### Customizing the Default Amount Formatters

_Moneta_ basically provides similar formatting options to the one of DecimalFormat.
It is possible to pass a `DecimalFormat` pattern string
as a parameter for a `Locale` based format query:

```java
MonetaryAmountFormat formatQueried = MonetaryFormats.getAmountFormat(
  AmountFormatQueryBuilder.of(Locale.GERMANY)
    .set(AmountFormatParams.PATTERN, "####,####")
    .build()
);
```


### Registering your own Formats

You can add additional formats by registering instances of `MonetaryAmountFormatProviderSpi`. Be default this has to be
done based on the mechanism as defined by the Java `ServiceLoader`.

_Implement a `MonetaryAmountFormatProviderSpi` providing a format for "GKC" (GeeCoin)_
```java
public final class GeeCoinFormatProviderSpi implements MonetaryAmountFormatProviderSpi {

    private static final String PROVIDER_NAME = "GeeCoin";
    private static final String STYLE_NAME = "GeeCoin";

    /** The supported locales. */
    private Set<Locale> supportedSets = new HashSet<>();
    /** The provided formats, by name. */
    private Set<String> formatNames = new HashSet<>();

    public GeeCoinFormatProviderSpi() {
        supportedSets.add(Locale.CHINA);
        supportedSets = Collections.unmodifiableSet(supportedSets);
        formatNames.add("GeeCoin");
        formatNames = Collections.unmodifiableSet(formatNames);
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.money.spi.MonetaryAmountFormatProviderSpi#getProviderName()
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /*
     * (non-Javadoc)
     * @see
     * javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(javax.money.format.AmountFormatContext)
     */
    @Override
    public Collection<MonetaryAmountFormat> getAmountFormats(AmountFormatQuery amountFormatQuery) {
        Objects.requireNonNull(amountFormatQuery, "AmountFormatContext required");
        if (!amountFormatQuery.getProviderNames().isEmpty()
            && !amountFormatQuery.getProviderNames().contains(getProviderName())) {
            return Collections.emptySet();
        }
        if (!(amountFormatQuery.getFormatName() == null
            || STYLE_NAME.equals(amountFormatQuery.getFormatName()))) {
            return Collections.emptySet();
        }
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(PROVIDER_NAME);
        if (amountFormatQuery.getLocale() != null) {
            builder.setLocale(amountFormatQuery.getLocale());
        }
        builder.importContext(amountFormatQuery, false);
        builder.setMonetaryAmountFactory(amountFormatQuery.getMonetaryAmountFactory());
        return Arrays.asList(new MonetaryAmountFormat[]{new GeeCoinAmountFormat(builder.build())});
    }

    @Override
    public Set<Locale> getAvailableLocales() {
        return supportedSets;
    }

    @Override
    public Set<String> getAvailableFormatNames() {
        return formatNames;
    }

}
```


### Overriding values in javamoney.properties

The reference implementation supports overriding of the values in `javamoney.properties` by prefixing the keys with
a priority value in brackets. Hereby the mechanism reads all `javamoney.properties` resources visible on the
classpath. If no priority is annotated, `priority=0` is assumed:

_Overriding a Configuration Value using a Priority_
```properties
{100}myKey=myValue
```

If two entries have the same priority an exception is thrown.
