import org.javamoney.moneta.internal.*;
import org.javamoney.moneta.internal.format.DefaultAmountFormatProviderSpi;
import org.javamoney.moneta.internal.loader.DefaultLoaderService;

import javax.money.DefaultMonetaryRoundingsSingletonSpi;
import javax.money.format.MonetaryFormats;
import javax.money.format.MonetaryFormats.DefaultMonetaryFormatsSingletonSpi;
import javax.money.spi.MonetaryFormatsSingletonSpi;

/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
module org.javamoney.moneta {
    exports org.javamoney.moneta;
    exports org.javamoney.moneta.format;
    exports org.javamoney.moneta.function;
    exports org.javamoney.moneta.spi;
    requires transitive java.money;
    requires transitive java.base;
    requires transitive java.logging;
    requires transitive java.annotation;
    requires static org.osgi.core;
    requires static org.osgi.compendium;
    requires static org.osgi.annotation;
    provides javax.money.spi.CurrencyProviderSpi with JDKCurrencyProvider;
    provides javax.money.spi.MonetaryAmountFactoryProviderSpi with MoneyAmountFactoryProvider,FastMoneyAmountFactoryProvider;
    provides javax.money.spi.MonetaryAmountFormatProviderSpi with DefaultAmountFormatProviderSpi;
    provides javax.money.spi.MonetaryAmountsSingletonQuerySpi with DefaultMonetaryAmountsSingletonQuerySpi;
    provides javax.money.spi.MonetaryAmountsSingletonSpi with DefaultMonetaryAmountsSingletonSpi;
    provides javax.money.spi.RoundingProviderSpi with DefaultRoundingProvider;
    provides javax.money.spi.ServiceProvider with PriorityAwareServiceProvider;
    provides org.javamoney.moneta.spi.LoaderService with DefaultLoaderService;

    uses org.javamoney.moneta.spi.LoaderService;
    uses org.javamoney.moneta.spi.MonetaryAmountProducer;
}