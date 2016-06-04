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

import java.math.RoundingMode;
import java.util.Objects;

import javax.money.*;

/**
 * This class allows to extract the major part of a {@link MonetaryAmount}
 * instance. Gets the amount in major units as a {@code long}.
 * <p>
 * For example, 'EUR 2.35' will return 2,
 * and 'BHD -1.345' will return -1.
 * <p>
 * @return the major units part of the amount
 * @author Anatole Tresch
 * @author Otavio Santana
 */
final class ExtractorMajorPartQuery implements MonetaryQuery<Long> {

    private final MonetaryOperator downRounding =
            Monetary.getRounding(RoundingQueryBuilder.of().setScale(0).set(RoundingMode.DOWN).build());

    /**
     * Access the shared instance of {@link ExtractorMajorPartQuery} for use.
     */
    ExtractorMajorPartQuery() {
    }

    /**
     * Gets the amount in major units as a {@code long}.
     * <p>
     * This returns the monetary amount in terms of the major units of the
     * currency, truncating the amount if necessary. For example, 'EUR 2.35'
     * will return 2, and 'BHD -1.345' will return -1.
     * <p>
     * This method matches the API of {@link java.math.BigDecimal}.
     *
     * @return the major units part of the amount
     * @throws ArithmeticException if the amount is too large for a {@code long}
     */
    @Override
    public Long queryFrom(MonetaryAmount amount) {
        Objects.requireNonNull(amount, "Amount required.");
        return amount.with(downRounding).getNumber().longValueExact();
    }
}
