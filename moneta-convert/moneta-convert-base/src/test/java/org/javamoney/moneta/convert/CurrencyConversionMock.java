package org.javamoney.moneta.convert;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

/**
 * This class mock the exchange rate to test some {@link MonetaryFunctions} that
 * needs an exchange provider
 *
 * @author otaviojava
 * @author keilw
 */
class CurrencyConversionMock implements CurrencyConversion {

	static final CurrencyUnit EURO = Monetary.getCurrency("EUR");
	static final CurrencyUnit DOLLAR = Monetary.getCurrency("USD");
	static final CurrencyUnit BRAZILIAN_REAL = Monetary
			.getCurrency("BRL");
	
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

	static Stream<MonetaryAmount> currencies() {
		Money r1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money r2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money r3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);

		Money e1 = Money.of(BigDecimal.TEN, EURO);
		Money e2 = Money.of(BigDecimal.ZERO, EURO);
		Money e3 = Money.of(BigDecimal.ONE, EURO);

		Money d1 = Money.of(BigDecimal.TEN, DOLLAR);
		Money d2 = Money.of(BigDecimal.ZERO, DOLLAR);
		Money d3 = Money.of(BigDecimal.ONE, DOLLAR);
		return Stream.of(r1, r2, r3, e1, e2, e3, d1, d2, d3);
	}

	static Stream<MonetaryAmount> currenciesToSummary() {
		Money r1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money r2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money r3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r4 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r5 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r6 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r7 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r8 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);

		Money e1 = Money.of(BigDecimal.TEN, EURO);
		Money d1 = Money.of(BigDecimal.ONE, DOLLAR);
		return Stream.of(r1, r2, r3, r4, r5, r6, r7, r8, e1, d1);
	}
}
