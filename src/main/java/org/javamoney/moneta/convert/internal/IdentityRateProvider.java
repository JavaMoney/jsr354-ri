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

import static org.javamoney.moneta.convert.internal.ProviderConstants.TIMESTAMP;

import org.javamoney.moneta.DefaultExchangeRate;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import java.math.BigDecimal;
import java.net.MalformedURLException;

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
    private static final ProviderContext CONTEXT = new ProviderContext.Builder("IDENT",RateType.OTHER)
            .setAttribute("providerDescription", "Identitiy Provider").build();

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
            DefaultExchangeRate.Builder builder = new DefaultExchangeRate.Builder(
                    new ConversionContext.Builder(CONTEXT, RateType.DEFERRED)
                            .setAttribute(TIMESTAMP, context.getNamedAttribute(TIMESTAMP, Long.class)
                            ).build()
            );
            builder.setBase(base);
            builder.setTerm(term);
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
            return new DefaultExchangeRate.Builder(rate.getConversionContext()).setTerm(rate.getBase()).setBase(rate.getTerm())
                    .setFactor(new DefaultNumberValue(BigDecimal.ONE)).build();
        }
        return null;
    }

}