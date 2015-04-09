package org.javamoney.moneta.function;

import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;
import static org.javamoney.moneta.function.StreamFactory.EURO;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.Money;

/**
 * This class mock the exchange rate to test some {@link MonetaryFunctions} that
 * needs an exchange provider
 *
 * @author otaviojava
 */
class CurrencyConversionMock implements CurrencyConversion {

    private CurrencyUnit currency;

    private Conversation conversation;


    CurrencyConversionMock(CurrencyUnit currency) {
        this.currency = currency;
        if (DOLLAR.equals(currency)) {
            this.conversation = new DollarConversation();
        } else if (EURO.equals(currency)) {
            this.conversation = new EuroConversation();
        } else if (BRAZILIAN_REAL.equals(currency)) {
            this.conversation = new RealConversation();
        }
    }

    @Override
    public MonetaryAmount apply(MonetaryAmount monetaryAmount) {
        return conversation.convert(monetaryAmount);
    }

    @Override
    public CurrencyUnit getCurrency() {
        return currency;
    }

    @Override
    public ConversionContext getContext() {
        return null;
    }

    @Override
    public ExchangeRate getExchangeRate(MonetaryAmount sourceAmount) {
        return null;
    }

    @Override
    public ExchangeRateProvider getExchangeRateProvider() {
        return null;
    }

    private class DollarConversation implements Conversation {
        private CurrencyUnit currency = DOLLAR;

        @Override
        public MonetaryAmount convert(MonetaryAmount monetaryAmount) {

            CurrencyUnit currencyUnit = Objects.requireNonNull(monetaryAmount)
                    .getCurrency();

            if (currencyUnit.equals(currency)) {
                return monetaryAmount;
            }

            if (BRAZILIAN_REAL.equals(currencyUnit)) {
                double val = monetaryAmount.getNumber().doubleValue() / 2.42;
                return Money.of(val, this.currency);
            } else if (EURO.equals(currencyUnit)) {
                double val = monetaryAmount.getNumber().doubleValue() / 0.79;
                return Money.of(val, this.currency);
            }
            return null;
        }

    }

    private class EuroConversation implements Conversation {

        private CurrencyUnit currency = EURO;

        @Override
        public MonetaryAmount convert(MonetaryAmount monetaryAmount) {

            CurrencyUnit currencyUnit = Objects.requireNonNull(monetaryAmount)
                    .getCurrency();

            if (currencyUnit.equals(currency)) {
                return monetaryAmount;
            }

            if (BRAZILIAN_REAL.equals(currencyUnit)) {
                double val = monetaryAmount.getNumber().doubleValue() / 2.42;
                return Money.of(val, this.currency);
            } else if (DOLLAR.equals(currencyUnit)) {
                double val = monetaryAmount.getNumber().doubleValue() / 1.79;
                return Money.of(val, this.currency);
            }
            return null;
        }
    }

    private class RealConversation implements Conversation {

        private CurrencyUnit currency = BRAZILIAN_REAL;

        @Override
        public MonetaryAmount convert(MonetaryAmount monetaryAmount) {

            CurrencyUnit currencyUnit = Objects.requireNonNull(monetaryAmount)
                    .getCurrency();

            if (currencyUnit.equals(currency)) {
                return monetaryAmount;
            }

            if (DOLLAR.equals(currencyUnit)) {
                double val = monetaryAmount.getNumber().doubleValue() * 2.42;
                return Money.of(val, this.currency);
            } else if (EURO.equals(currencyUnit)) {
                double val = monetaryAmount.getNumber().doubleValue() * 1.79;
                return Money.of(val, this.currency);
            }
            return null;
        }

    }

    private interface Conversation {
        MonetaryAmount convert(MonetaryAmount monetaryAmount);
    }

}
