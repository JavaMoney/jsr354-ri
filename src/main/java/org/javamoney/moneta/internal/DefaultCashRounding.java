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
package org.javamoney.moneta.internal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Implementation class providing cash rounding {@link javax.money.MonetaryOperator}
 * instances for {@link CurrencyUnit} instances. modeling rounding based on
 * minimal minor units available for cash payments.
 * <p/>
 * This class is thread safe.
 *
 * @author Anatole Tresch
 */
final class DefaultCashRounding implements MonetaryOperator{

    /**
     * The {@link RoundingMode} used.
     */
    private final RoundingMode roundingMode;
    /**
     * The scale to be applied.
     */
    private final int scale;
    /**
     * The minimal minors available in cash.
     */
    private final int minimalMinors;

    /**
     * Creates an rounding instance.
     *
     * @param roundingMode The {@link RoundingMode} to be used, not {@code null}.
     * @param scale        The target scale.
     */
    DefaultCashRounding(int scale, RoundingMode roundingMode, int minimalMinors){
        if(scale < 0){
            throw new IllegalArgumentException("scale < 0");
        }
        if(roundingMode == null){
            throw new IllegalArgumentException("roundingMode missing");
        }
        this.scale = scale;
        this.roundingMode = roundingMode;
        this.minimalMinors = minimalMinors;
    }

    /**
     * Creates an {@link DefaultCashRounding} for rounding
     * {@link MonetaryAmount} instances given a currency.
     *
     * @param currency The currency, which determines the required precision. As
     *                 {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
     *                 is sued.
     * @return a new instance {@link javax.money.MonetaryOperator} implementing the
     * rounding.
     */
    DefaultCashRounding(CurrencyUnit currency, RoundingMode roundingMode, int minimalMinors){
        this(currency.getDefaultFractionDigits(), roundingMode, minimalMinors);
    }

    /**
     * Creates an {@link MonetaryOperator} for rounding {@link MonetaryAmount}
     * instances given a currency.
     *
     * @param currency The currency, which determines the required precision. As
     *                 {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
     *                 is sued.
     * @return a new instance {@link MonetaryOperator} implementing the
     * rounding.
     */
    DefaultCashRounding(CurrencyUnit currency, int minimalMinors){
        this(currency, RoundingMode.HALF_UP, minimalMinors);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryFunction#apply(java.lang.Object)
     */
    @Override
    public <T extends MonetaryAmount> T apply(T value){
        Objects.requireNonNull(value, "Amount required.");
        // 1 extract BD value, round according the default fraction units
        BigDecimal num = value.getNumber().numberValue(BigDecimal.class).setScale(scale, roundingMode);
        // 2 evaluate minor units and remainder
        long minors = num.movePointRight(num.scale()).longValueExact();
        long factor = minors / minimalMinors;
        long low = minimalMinors * factor;
        long high = minimalMinors * (factor + 1);
        if(minors - low > high - minors){
            minors = high;
        }else if(minors - low < high - minors){
            minors = low;
        }else{
            switch(roundingMode){
                case HALF_UP:
                case UP:
                case HALF_EVEN:
                    minors = high;
                    break;
                default:
                    minors = low;
            }
        }
        return (T) value.getFactory().setCurrency(value.getCurrency())
                .setNumber(BigDecimal.valueOf(minors).movePointLeft(scale)).create();
    }

}
