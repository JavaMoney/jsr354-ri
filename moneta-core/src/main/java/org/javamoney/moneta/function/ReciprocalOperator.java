/*
  Copyright (c) 2012, 2020, Anatole Tresch, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.function;

import java.math.BigDecimal;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.NumberValue;

import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Objects.requireNonNull;

/**
 * This class allows to extract the reciprocal value (multiplicative inversion)
 * of a {@link MonetaryAmount} instance.
 *
 * @author Anatole Tresch
 */
final class ReciprocalOperator implements MonetaryOperator {

    /**
     * Access the shared instance of {@link ReciprocalOperator} for use.
     */
    ReciprocalOperator(){
    }

    /**
     * Gets the amount as reciprocal / multiplicative inversed value (1/n).
     * <p>
     * E.g. 'EUR 2.0' will be converted to 'EUR 0.5'.
     *
     * @return the reciprocal / multiplicative inversed of the amount
     * @throws ArithmeticException if the arithmetic operation failed
     */
    @Override
    public MonetaryAmount apply(MonetaryAmount amount) {
        requireNonNull(amount, "Amount required.");
        NumberValue num = amount.getNumber();
        BigDecimal one = ONE.setScale(num.getScale() < 5 ? 5 : num.getScale(), HALF_EVEN);
        BigDecimal reciprocal = one.divide(num.numberValue(BigDecimal.class), HALF_EVEN);
        return amount.getFactory().setNumber(reciprocal).create();
    }

}
