/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.convert.internal;

import java.math.BigDecimal;
import java.net.MalformedURLException;

import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;

import org.javamoney.moneta.DefaultExchangeRate;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;

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
    private static final ProviderContext CONTEXT =
            new ProviderContextBuilder("IDENT", RateType.OTHER).set("providerDescription", "Identitiy Provider")
                    .build();

    /**
     * Constructor, also loads initial data.
     *
     * @throws java.net.MalformedURLException
     */
    public IdentityRateProvider() throws MalformedURLException{
        super(CONTEXT);
    }

    /**
     * Check if this provider can provide a rate, which is only the case if base and term are equal.
     *
     * @param conversionQuery the required {@link ConversionQuery}, not {@code null}
     * @return
     */
    public boolean isAvailable(ConversionQuery conversionQuery){
        return conversionQuery.getBaseCurrency().getCurrencyCode()
                .equals(conversionQuery.getTermCurrency().getCurrencyCode());
    }

    public ExchangeRate getExchangeRate(ConversionQuery query){
        if(query.getBaseCurrency().getCurrencyCode().equals(query.getTermCurrency().getCurrencyCode())){
            DefaultExchangeRate.Builder builder =
                    new DefaultExchangeRate.Builder(new ConversionContextBuilder(CONTEXT, RateType.OTHER).build());
            builder.setBase(query.getBaseCurrency());
            builder.setTerm(query.getTermCurrency());
            builder.setFactor(DefaultNumberValue.of(BigDecimal.ONE));
            return builder.build();
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
        if(rate.getConversionContext().getProvider().equals(CONTEXT.getProvider())){
            return new DefaultExchangeRate.Builder(rate.getConversionContext()).setTerm(rate.getBase())
                    .setBase(rate.getTerm()).setFactor(new DefaultNumberValue(BigDecimal.ONE)).build();
        }
        return null;
    }

}