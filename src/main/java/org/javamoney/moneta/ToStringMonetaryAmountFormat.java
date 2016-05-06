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
		return Optional.ofNullable(amount).map(MonetaryAmount::toString)
				.orElse("null");
    }

    @Override
    public AmountFormatContext getContext() {
        return context;
    }

    @Override
    public void print(Appendable appendable, MonetaryAmount amount)
            throws IOException {
        appendable.append(queryFrom(amount));

    }

    @Override
    public MonetaryAmount parse(CharSequence text)
            throws MonetaryParseException {
		try {
			ParserMonetaryAmount amount = parserMonetaryAmount(text);
			return style.to(amount);
		} catch (Exception e) {
			throw new MonetaryParseException(e.getMessage(), text, 0);
		}
    }

    private ParserMonetaryAmount parserMonetaryAmount(CharSequence text) throws Exception {
        String[] array = Objects.requireNonNull(text).toString().split(" ");
        if(array.length != 2) {
        	throw new MonetaryParseException("An error happened when try to parse the Monetary Amount.",text,0);
        }
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

        private static final long serialVersionUID = 6606016328162974467L;
        abstract MonetaryAmount to(ParserMonetaryAmount amount);
    }

}
