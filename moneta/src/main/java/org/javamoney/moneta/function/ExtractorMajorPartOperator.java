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
package org.javamoney.moneta.function;

import javax.money.*;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * This class allows to extract the major part of a {@link MonetaryAmount} instance.
 *
 * @author Anatole Tresch
 */
final class ExtractorMajorPartOperator implements MonetaryOperator {

    private static final MonetaryRounding DOWN_ROUNDING =
            Monetary.getRounding(RoundingQueryBuilder.of().setScale(0).set(RoundingMode.DOWN).build());

    /**
     * Access the shared instance of {@link ExtractorMajorPartOperator} for use.
     */
    ExtractorMajorPartOperator() {
    }

    /**
     * Gets the amount in major units as a {@code MonetaryAmount} with scale 0.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency, truncating the
     * amount if necessary. For example, 'EUR 2.35' will return 'EUR 2', and 'BHD -1.345' will
     * return 'BHD -1'.
     * <p>
     * This is returned as a {@code MonetaryAmount} rather than a {@code BigInteger} . This is to
     * allow further calculations to be performed on the result. Should you need a
     * {@code BigInteger}, simply call {@code asType(BigInteger.class)}.
     * <p>
     *
     * @return the major units part of the amount, never {@code null}
     */
    @Override
    public MonetaryAmount apply(MonetaryAmount amount) {
        Objects.requireNonNull(amount, "Amount required.");
        return amount.with(DOWN_ROUNDING);
    }

}
