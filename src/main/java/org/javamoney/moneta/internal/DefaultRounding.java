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

import javax.money.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation class providing rounding {@link javax.money.MonetaryOperator} instances
 * for {@link CurrencyUnit} instances. modeling rounding based on standard JDK
 * math, a scale and {@link RoundingMode}.
 * <p>
 * This class is thread safe.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @see RoundingMode
 */
final class DefaultRounding implements MonetaryRounding, Serializable {

	private static final long serialVersionUID = -211054408229261721L;

	/**
     * The scale key to be used.
     */
    private static final String SCALE_KEY = "scale";

    /**
     * The provider class key to be used.
     */
    private static final String PROVCLASS_KEY = "providerClass";

    /**
     * The {@link RoundingMode} used.
     */
    private final RoundingContext context;

    /**
     * Creates an rounding instance.
     *
     * @param roundingMode The {@link java.math.RoundingMode} to be used, not {@code null}.
     */
    DefaultRounding(int scale, RoundingMode roundingMode) {
        Objects.requireNonNull(roundingMode, "RoundingMode required.");
        if (scale < 0) {
            scale = 0;
        }
        this.context = RoundingContextBuilder.of("default", "default").
                set(PROVCLASS_KEY, getClass().getName()).set(SCALE_KEY, scale).set(Optional.ofNullable(roundingMode)
                .orElseThrow(
                        () -> new
                                IllegalArgumentException(
                                "roundingMode missing")))
                .build();
    }

    /**
     * Creates an {@link DefaultRounding} for rounding {@link MonetaryAmount}
     * instances given a currency.
     *
     * @param currency The currency, which determines the required precision. As
     *                 {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
     *                 is used.
     */
    DefaultRounding(CurrencyUnit currency, RoundingMode roundingMode) {
        this(currency.getDefaultFractionDigits(), roundingMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryFunction#apply(java.lang.Object)
     */
    @Override
    public MonetaryAmount apply(MonetaryAmount amount) {
        return amount.getFactory().setCurrency(amount.getCurrency()).setNumber(
                amount.getNumber().numberValue(BigDecimal.class)
                        .setScale(this.context.getInt(SCALE_KEY), this.context.get(RoundingMode.class))).create();
    }

    @Override
    public RoundingContext getRoundingContext() {
        return context;
    }
}
