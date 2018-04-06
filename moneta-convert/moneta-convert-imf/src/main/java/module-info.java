import org.javamoney.moneta.convert.internal.imf.IMFHistoricRateProvider;
import org.javamoney.moneta.convert.internal.imf.IMFRateProvider;

/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
module info {
    requires org.javamoney.moneta.convert;
    requires javax.money;
    requires javax.money.convert;
    requires javax.money.format;
    requires javax.money.spi;
    requires java.util;
    requires java.util.stream;
    requires java.util.logging;
    requires java.util.concurrent;
    requires java.io;
    requires java.math;
    provides javax.money.convert.ExchangeRateProvider with IMFRateProvider;
    provides javax.money.convert.ExchangeRateProvider with IMFHistoricRateProvider;
    uses org.javamoney.moneta.spi.LoaderService;
    uses org.javamoney.moneta.spi.MonetaryAmountProducer;
}