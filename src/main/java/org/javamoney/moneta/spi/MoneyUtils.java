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
package org.javamoney.moneta.spi;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Platform RI: This utility class simplifies implementing {@link MonetaryAmount},
 * by providing the common functionality. The different explicitly typed methods
 * are all reduced to methods using {@link BigDecimal} as input, hereby
 * performing any conversion to {@link BigDecimal} as needed. Obviously this
 * takes some time, so implementors that want to avoid this overhead should
 * implement {@link MonetaryAmount} directly.
 *
 * @author Anatole Tresch
 */
public final class MoneyUtils {


    private MoneyUtils() {
    }


    // Supporting methods

    /**
     * Creates a {@link BigDecimal} from the given {@link Number} doing the
     * valid conversion depending the type given.
     *
     * @param num the number type
     * @return the corresponding {@link BigDecimal}
     */
    public static BigDecimal getBigDecimal(long num) {
        return BigDecimal.valueOf(num);
    }

    /**
     * Creates a {@link BigDecimal} from the given {@link Number} doing the
     * valid conversion depending the type given.
     *
     * @param num the number type
     * @return the corresponding {@link BigDecimal}
     */
    public static BigDecimal getBigDecimal(double num) {
        if (num == Double.NaN) {
            throw new ArithmeticException("Invalid input Double.NaN.");
        } else if (num == Double.POSITIVE_INFINITY) {
            throw new ArithmeticException("Invalid input Double.POSITIVE_INFINITY.");
        } else if (num == Double.NEGATIVE_INFINITY) {
            throw new ArithmeticException("Invalid input Double.NEGATIVE_INFINITY.");
        }
        return new BigDecimal(String.valueOf(num));
    }

    /**
     * Creates a {@link BigDecimal} from the given {@link Number} doing the
     * valid conversion depending the type given.
     *
     * @param num the number type
     * @return the corresponding {@link BigDecimal}
     */
    public static BigDecimal getBigDecimal(Number num) {
        return ConvertBigDecimal.of(num);
    }

    /**
     * Creates a {@link BigDecimal} from the given {@link Number} doing the
     * valid conversion depending the type given, if a {@link MonetaryContext}
     * is given, it is applied to the number returned.
     *
     * @param num the number type
     * @return the corresponding {@link BigDecimal}
     */
    public static BigDecimal getBigDecimal(Number num, MonetaryContext moneyContext) {
        BigDecimal bd = getBigDecimal(num);
        if (Objects.nonNull(moneyContext)) {
            return new BigDecimal(bd.toString(), getMathContext(moneyContext, RoundingMode.HALF_EVEN));
        }
        return bd;
    }

    /**
     * Evaluates the {@link MathContext} from the given {@link MonetaryContext}.
     *
     * @param monetaryContext the {@link MonetaryContext}
     * @param defaultMode     the default {@link RoundingMode}, to be used if no one is set
     *                        in {@link MonetaryContext}.
     * @return the corresponding {@link MathContext}
     */
	public static MathContext getMathContext(MonetaryContext monetaryContext, RoundingMode defaultMode) {
		MathContext ctx = monetaryContext.get(MathContext.class);
		if (Objects.nonNull(ctx)) {
			return ctx;
		}
		RoundingMode roundingMode = monetaryContext.get(RoundingMode.class);
		if (roundingMode == null) {
			roundingMode = Optional.ofNullable(defaultMode).orElse(RoundingMode.HALF_EVEN);
		}
		return new MathContext(monetaryContext.getPrecision(), roundingMode);
	}

    /**
     * Method to check if a currency is compatible with this amount instance.
     *
     * @param amount       The monetary amount to be compared to, never null.
     * @param currencyUnit the currency unit to compare, never null.
     * @throws MonetaryException If the amount is null, or the amount's {@link CurrencyUnit} is not
     *                           compatible, meaning has a different value of
     *                           {@link CurrencyUnit#getCurrencyCode()}).
     */
    public static void checkAmountParameter(MonetaryAmount amount, CurrencyUnit currencyUnit) {
        Objects.requireNonNull(amount, "Amount must not be null.");
        final CurrencyUnit amountCurrency = amount.getCurrency();
        if (!(currencyUnit.getCurrencyCode().equals(amountCurrency.getCurrencyCode()))) {
            throw new MonetaryException("Currency mismatch: " + currencyUnit + '/' + amountCurrency);
        }
    }

    /**
     * Internal method to check for correct number parameter.
     *
     * @param number the number to be checked.
     * @throws IllegalArgumentException If the number is null
     */
    public static void checkNumberParameter(Number number) {
        Objects.requireNonNull(number, "Number is required.");
    }

}
