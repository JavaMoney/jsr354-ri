package org.javamoney.moneta;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.Monetary;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;

/**
 * class to format and parse a text string such as 'EUR 25.25' or vice versa.
 * This class will used to toString and parse in all implementation on Moneta.
 * {@link Money#toString()}
 * {@link Money#parse(CharSequence)}
 * {@link FastMoney#toString()}
 * {@link FastMoney#parse(CharSequence)}
 * {@link RoundedMoney#toString()}
 * {@link RoundedMoney#parse(CharSequence)}
 * @author Otavio Santana
 */
public final class ToStringMonetaryAmountFormat implements MonetaryAmountFormat {

    private static final String CONTEXT_PREFIX = "ToString_";

    private final ToStringMonetaryAmountFormatStyle style;

    private final AmountFormatContext context;

    private ToStringMonetaryAmountFormat(ToStringMonetaryAmountFormatStyle style) {
        this.style = Objects.requireNonNull(style);
        context = AmountFormatContextBuilder.of(CONTEXT_PREFIX + style).build();
    }

    public static ToStringMonetaryAmountFormat of(
            ToStringMonetaryAmountFormatStyle style) {
        return new ToStringMonetaryAmountFormat(style);
    }

    @Override
    public String queryFrom(MonetaryAmount amount) {
        if (Objects.isNull(amount)) {
            return null;
        }
        return amount.toString();
    }

    @Override
    public AmountFormatContext getContext() {
        return context;
    }

    @Override
    public void print(Appendable appendable, MonetaryAmount amount)
            throws IOException {
        appendable.append(Optional.ofNullable(amount)
                .map(MonetaryAmount::toString).orElse("null"));

    }

    @Override
    public MonetaryAmount parse(CharSequence text)
            throws MonetaryParseException {
        ParserMonetaryAmount amount = parserMonetaryAmount(text);
        return style.to(amount);
    }

    private ParserMonetaryAmount parserMonetaryAmount(CharSequence text) {
        String[] array = Objects.requireNonNull(text).toString().split(" ");
        CurrencyUnit currencyUnit = Monetary.getCurrency(array[0]);
        BigDecimal number = new BigDecimal(array[1]);
        return new ParserMonetaryAmount(currencyUnit, number);
    }

    private class ParserMonetaryAmount {
        ParserMonetaryAmount(CurrencyUnit currencyUnit, BigDecimal number) {
            this.currencyUnit = currencyUnit;
            this.number = number;
        }

        private final CurrencyUnit currencyUnit;
        private final BigDecimal number;
    }

    /**
     * indicates with implementation will used to format or parser in
     * ToStringMonetaryAmountFormat
     */
    public enum ToStringMonetaryAmountFormatStyle {
    	/**
    	 * {@link Money}
    	 */
        MONEY {
            @Override
            MonetaryAmount to(ParserMonetaryAmount amount) {
                return Money.of(amount.number, amount.currencyUnit);
            }
        },
        /**
    	 * {@link FastMoney}
    	 */
        FAST_MONEY {
            @Override
            MonetaryAmount to(ParserMonetaryAmount amount) {
                return FastMoney.of(amount.number, amount.currencyUnit);
            }
        },
        /**
    	 * {@link RoundedMoney}
    	 */
        ROUNDED_MONEY {
            @Override
            MonetaryAmount to(ParserMonetaryAmount amount) {
                return RoundedMoney.of(amount.number, amount.currencyUnit);
            }
        };

        abstract MonetaryAmount to(ParserMonetaryAmount amount);
    }

}
