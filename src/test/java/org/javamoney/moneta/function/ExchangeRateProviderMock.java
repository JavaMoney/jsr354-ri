package org.javamoney.moneta.function;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;

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
