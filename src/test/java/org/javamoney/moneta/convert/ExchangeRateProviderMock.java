/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.convert;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;

import org.javamoney.moneta.function.MonetaryFunctions;

/**
 * This class mock the exchange rate to test some {@link MonetaryFunctions} that
 * needs an exchange provider
 *
 * @author otaviojava
 */
class ExchangeRateProviderMock implements ExchangeRateProvider {

    @Override
    public ProviderContext getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CurrencyConversion getCurrencyConversion(
            ConversionQuery conversionQuery) {
        CurrencyUnit currencyUnit = conversionQuery.get("Query.termCurrency",
                CurrencyUnit.class);
        return new CurrencyConversionMock(currencyUnit);
    }

}
