/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.convert.internal;

import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import javax.xml.parsers.SAXParser;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

/**
 * This class implements an {@link javax.money.convert.ExchangeRateProvider} that provides exchange rate with factor
 * one for identical base/term currencies.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IdentityRateProvider extends AbstractRateProvider{

    /**
     * The {@link javax.money.convert.ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT = new ProviderContext.Builder("IDENT").setRateTypes(RateType.OTHER)
            .set("Identitiy Provider", "providerDescription").create();

    /**
     * Constructor, also loads initial data.
     *
     * @throws java.net.MalformedURLException
     */
    public IdentityRateProvider() throws MalformedURLException{
        super(CONTEXT);
    }


    protected ExchangeRate getExchangeRateInternal(CurrencyUnit base, CurrencyUnit term, ConversionContext context){
        if(base.getCurrencyCode().equals(term.getCurrencyCode())){
            ExchangeRate.Builder builder = new ExchangeRate.Builder(
                    ConversionContext.of(CONTEXT.getProviderName(), RateType.DEFERRED, context.getTimestamp()));
            builder.setBase(base);
            builder.setTerm(term);
            builder.setFactor(DefaultNumberValue.of(BigDecimal.ONE));
            return builder.create();
        }
        return null;
    }

    /*
     * (non-Javadoc)
	 *
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getReversed(javax.money.convert
	 * .ExchangeRate)
	 */
    @Override
    public ExchangeRate getReversed(ExchangeRate rate){
        if(rate.getConversionContext().getProvider().equals(CONTEXT.getProviderName())){
            return new ExchangeRate.Builder(rate.getConversionContext()).setTerm(rate.getBase()).setBase(rate.getTerm())
                    .setFactor(new DefaultNumberValue(BigDecimal.ONE)).create();
        }
        return null;
    }

}